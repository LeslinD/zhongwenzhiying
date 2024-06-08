import tkinter as tk
import webbrowser
import ttkbootstrap as ttk
from ttkbootstrap.constants import *
import threading
import pyautogui
import requests
import json
from openai import OpenAI
import pyaudio
import base64
from urllib.request import urlopen
from urllib.request import Request
from urllib.error import URLError
from urllib.parse import urlencode
import numpy as np
import time
from io import BytesIO
from PIL import Image, ImageTk, ImageDraw
import httpx

import websocket
import logging

token = ''
trans_token = ''
baiduID = ''
API_KEY = 'mh82eBZsHARlV6DOsTW2x4x4'
SECRET_KEY = '3dalBxwaOHkqG6yDRsLi4L5hhiHNo6ZT'
DEV_PID = 80001
ASR_URL = 'http://vop.baidu.com/pro_api'
SCOPE = 'brain_enhanced_asr'  # 有此scope表示有极速版能力，没有请在网页里开通极速版
client = OpenAI(
    base_url="https://hk.xty.app/v1",
    api_key="sk-zS65xUguCnl0IdO6Cd5067A14dFd4b76A25dFaB93fCb33Aa",
    http_client=httpx.Client(
        base_url="https://api.xty.app/v1",
        follow_redirects=True,
    ),
)
start_x = None
start_y = None
draw = None

# 此处是实时翻译配置

user_config = {
    "app_id": "75689767",
    "app_key": "Ne8GNCd1piCYzRZ0yoOWeLAa",
}

business_config = {
    "asr_url": "wss://aip.baidubce.com/ws/realtime_speech_trans",
    "start_frame": {
        "type": "START",
        "from": "zh",
        "to": "en",
        "sampling_rate": 16000,
        # "return_target_tts": False,
        # "tts_speaker": "man",
        "app_id": user_config["app_id"],
        "app_key": user_config["app_key"],
    },
    "finish_frame": {
        "type": "FINISH"
    }
}
logger = logging.getLogger()

"""
1. 连接 ws_app.run_forever()
2. 连接成功后发送数据 on_open()
2.1 发送开始参数帧 send_start_params()
2.2 发送音频数据帧 send_audio()
2.3 库接收识别结果 on_message()
2.4 发送结束帧 send_finish()
3. 关闭连接 on_close()
库的报错 on_error()
"""


def send_start_params(ws):
    """
    发送开始帧
    :param ws:
    :return:
    """
    body = json.dumps(business_config.get("start_frame"))
    ws.send(body, websocket.ABNF.OPCODE_TEXT)
    logger.info("send START frame with params:" + body)


def send_audio(ws, data):
    """
    发送二进制音频数据，注意每个帧之间需要有间隔时间
    :param ws:
    :param data:
    :return:
    """
    # audio_file = user_config.get("asr_file")
    chunk_ms = 40  # 每40ms音频打包成一个数据帧
    audio_rate = 16000  # 音频采样率
    chunk_len = int(audio_rate * 2 / 1000 * chunk_ms)
    # with open(audio_file, 'rb') as f:
    #     audio_binary = f.read()

    index = 0
    total = len(data)
    logger.info("send_audio total={}".format(total))
    while index < total:
        end = index + chunk_len
        if end >= total:
            # 最后一个音频数据帧
            end = total
        body = data[index:end]
        logger.debug("try to send audio length {}, from bytes [{},{})".format(len(body), index, end))
        ws.send(body, websocket.ABNF.OPCODE_BINARY)
        index = end
        time.sleep(chunk_ms / 1000.0)  # ws.send 也有点耗时，这里没有计算


def send_finish(ws):
    """
    发送结束帧
    :param ws:
    :return:
    """
    body = json.dumps(business_config.get("finish_frame"))
    ws.send(body, websocket.ABNF.OPCODE_TEXT)
    logger.info("send FINISH frame")


def on_open(ws):
    """
    连接后发送数据帧
    :param ws:
    :return:
    """

    def run(*args):
        send_start_params(ws)
        # send_audio(ws)
        # send_finish(ws)
        # while True:
        #     time.sleep(20)  # 每 30 秒发送一次心跳
        #     ws.send(json.dumps({"type": "ping"}), websocket.ABNF.OPCODE_TEXT)
        #     logger.info("Sent ping")

    threading.Thread(target=run).start()


def on_data(ws, *args):
    """
    接收服务端返回的消息
    :param ws:
    :param args: args[0] is content
    :return:
    """
    if isinstance(args[0], str):
        global text_updator
        # text_updator(args[0].json().get('result').get("trans_result")[0]['dst'] + '\n' + Engstr)
        logger.info("Response: " + args[0])
        try:
            data = json.loads(args[0])
            status = data.get('data', {}).get('status', "")

            if status == "TRN":
                result = data.get('data', {}).get('result', {})

                asr = result.get('asr', "")
                asr_trans = result.get('asr_trans', "")
                sentence = result.get('sentence', "")
                sentence_trans = result.get('sentence_trans', "")

                if asr:
                    logger.info(f"ASR: {asr}, ASR Trans: {asr_trans}")
                    text_updator(asr + '\n' + asr_trans)
                else:
                    logger.info(f"Sentence: {sentence}, Sentence Trans: {sentence_trans}")
                    text_updator(sentence + '\n' + sentence_trans)
            else:
                logger.info("Status is not TRN, no data to process")
        except json.JSONDecodeError:
            logger.error("Failed to decode JSON response")


def on_error(ws, error):
    """
    ws库报错, 比如连接超时
    :param ws:
    :param error:
    :return:
    """
    logger.error("error: " + str(error))


def on_close(ws, close_status_code, close_msg):
    """
    Websocket关闭
    :param close_msg:
    :param close_status_code:
    :param ws:
    :return:
    """
    # if len(resp_audio_binary) > 0:
    #     # save tts audio
    #     save_tts_audio()
    logger.info("ws close: close_status_code={}, close_msg={}".format(close_status_code, close_msg))


def is_connected(ws):
    """
    检查 WebSocket 是否连接
    :param ws:
    :return: bool
    """
    return ws.sock and ws.sock.connected

def ws_task(ws):
    # 运行 WebSocket 连接
    ws.run_forever()


def vision_pic(btarr):
    base64_image = base64.b64encode(btarr).decode('utf-8')
    headers = {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + client.api_key
    }
    payload = {
        "model": "gpt-4-vision-preview",
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": "Is there something Chinese about this picture? What are the places related to China?"
                    },
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/jpeg;base64,{base64_image}"
                        }
                    }
                ]
            }
        ],
        "max_tokens": 300
    }
    response = requests.post("https://oneapi.xty.app/v1/chat/completions", headers=headers, json=payload)
    # print(response)
    bot_send_message(response.json().get('choices')[0].get('message').get('content'))


def baidu_translate(msg):
    url = "https://aip.baidubce.com/rpc/2.0/mt/texttrans/v1?access_token=" + trans_token
    payload = json.dumps({
        "from": "zh",
        "to": "en",
        "q": msg
    })
    headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
    response = requests.request("POST", url, headers=headers, data=payload)
    return response.json().get('result').get("trans_result")[0]['dst']


def center_window(window, width, height):
    screen_width = window.winfo_screenwidth()
    screen_height = window.winfo_screenheight()
    x = (screen_width - width) // 2
    y = (screen_height - height - 80)
    window.geometry(f'{width}x{height}+{x}+{y}')


def create_transparent_window(parent):
    global text_window
    window = tk.Toplevel(parent)
    window.title("")
    window.overrideredirect(True)  # 移除窗口边框和标题栏
    window.attributes('-alpha', 0.7)  # 设置窗口透明度
    window.attributes('-topmost', True)  # 窗口始终置于顶层

    # 创建一个半透明黑色背景
    background = tk.Canvas(window, bg='black', highlightthickness=0)
    background.pack(expand=True, fill='both')

    # 让窗口处于中间位置
    window_width = 1200
    window_height = 80
    center_window(window, window_width, window_height)

    # 创建一个Label用于显示文字
    label_var = tk.StringVar()
    label_var.set("语音识别字幕将展示在此处")
    label = tk.Label(window, textvariable=label_var, fg="white", bg="black", font=("Helvetica", 20))
    label.place(relx=0.5, rely=0.5, anchor="center")

    # 更新窗口中的文字内容
    def update_text(new_text):
        label_var.set(new_text)

    text_window = window
    # 返回更新文字内容的函数
    return update_text


def fetch_token():
    TOKEN_URL = 'http://aip.baidubce.com/oauth/2.0/token'
    params = {'grant_type': 'client_credentials',
              'client_id': API_KEY,
              'client_secret': SECRET_KEY}
    post_data = urlencode(params)
    post_data = post_data.encode('utf-8')
    req = Request(TOKEN_URL, post_data)
    try:
        f = urlopen(req)
        result_str = f.read()
    except URLError as err:
        print('token http response http code : ' + str(err))
        result_str = err
    result_str = result_str.decode()

    print(result_str)
    result = json.loads(result_str)
    print(result)
    if ('access_token' in result.keys() and 'scope' in result.keys()):
        if SCOPE and (not SCOPE in result['scope'].split(' ')):  # SCOPE = False 忽略检查
            raise DemoError('scope is not correct')
        print('SUCCESS WITH TOKEN: %s ; EXPIRES IN SECONDS: %s' % (result['access_token'], result['expires_in']))
        return result['access_token']
    else:
        raise DemoError('MAYBE API_KEY or SECRET_KEY not correct: access_token or scope not found in token response')


def fetch_token_trans():
    TOKEN_URL = 'http://aip.baidubce.com/oauth/2.0/token'
    params = {'grant_type': 'client_credentials',
              'client_id': 'Ne8GNCd1piCYzRZ0yoOWeLAa',
              'client_secret': 'mm342PItcm5xwIgVUrFJJbhGxHelCppB'}
    post_data = urlencode(params)
    post_data = post_data.encode('utf-8')
    req = Request(TOKEN_URL, post_data)
    try:
        f = urlopen(req)
        result_str = f.read()
    except URLError as err:
        print('token http response http code : ' + str(err))
        result_str = err
    result_str = result_str.decode()

    print(result_str)
    result = json.loads(result_str)
    print(result)
    if ('access_token' in result.keys() and 'scope' in result.keys()):
        print('SUCCESS WITH TOKEN: %s ; EXPIRES IN SECONDS: %s' % (result['access_token'], result['expires_in']))
        return result['access_token']
    else:
        raise DemoError('MAYBE API_KEY or SECRET_KEY not correct: access_token or scope not found in token response')


def asr(frames):
    global token
    length = len(frames)
    params = {'cuid': '123456PYTHON', 'token': token, 'dev_pid': DEV_PID}
    params_query = urlencode(params)
    headers = {
        'Content-Type': 'audio/' + 'wav' + '; rate=' + str(16000),
        'Content-Length': length
    }
    url = ASR_URL + "?" + params_query
    req = Request(ASR_URL + "?" + params_query, frames, headers)
    f = urlopen(req)
    result_str = json.loads(f.read())
    if result_str.get('err_msg') == 'success.':
        return result_str.get('result')[0]
    return ''


class TranslateThread(threading.Thread):
    def __init__(self):
        super().__init__()
        self._is_running = False

    def run(self):
        global text_updator
        global ws_app
        ws_thread = threading.Thread(target=ws_task, args=(ws_app,))
        ws_thread.start()
        self._is_running = True
        minimum_volume = 1000  # 最小声音，大于则开始录音，否则结束
        recording_started = False  # 开始录音节点
        continue_recording = True  # 判断是否继续录音
        low_volume_detected = False  # 判断声音小了
        time_counter = 0  # 计时器
        detected_time = int(time.time() * 1000)
        CHUNK = 512
        FORMAT = pyaudio.paInt16
        CHANNELS = 1
        RATE = 16000
        p = pyaudio.PyAudio()
        for i in range(p.get_device_count()):
            dev = p.get_device_info_by_index(i)
            print(dev)
            if '立体声混音' in dev['name']:
                input_device_index = i
                break
            else:
                input_device_index = 0
        # print(input_device_index)
        stream = p.open(format=FORMAT,
                        channels=CHANNELS,
                        rate=RATE,
                        input=True,
                        input_device_index=input_device_index,
                        frames_per_buffer=CHUNK)
        start_time = 0
        while self._is_running:
            recorded_audio = bytearray()
            while continue_recording:
                data = stream.read(CHUNK, exception_on_overflow=False)
                audio_data = np.frombuffer(data, dtype=np.short)
                max_volume = np.max(audio_data)
                if max_volume > minimum_volume:
                    if not recording_started:
                        recording_started = True
                        print("开始录音")
                        start_time = int(time.time() * 1000)
                    detected_time = int(time.time() * 1000)
                elif max_volume <= minimum_volume or (
                        recording_started and int(time.time() * 1000) - start_time > 3000):
                    idle_time = int(time.time() * 1000) - detected_time
                    if idle_time > 400 or (recording_started and int(time.time() * 1000) - start_time > 3000):
                        continue_recording = False
                        recording_started = False
                        # print("out")
                if recording_started:
                    # print(data)
                    for i in range(0, len(data), 1000):
                        recorded_audio.extend(data)
                else:
                    time.sleep(0.01)
                time_counter += 1
            # print(len(recorded_audio))
            if len(recorded_audio) > 0:
                if not is_connected(ws_app):
                    ws_thread = threading.Thread(target=ws_task, args=(ws_app,))
                    ws_thread.start()
                    time.sleep(0.5)
                    print("reconnect")
                send_audio(ws_app, recorded_audio)
                # asr_time=int(time.time() * 1000)
                # Chinesestr = asr(recorded_audio)

                # print(int(time.time() * 1000) - asr_time)
                # asr_time = int(time.time() * 1000)
                # if (Chinesestr != ''):
                #     Engstr = baidu_translate(Chinesestr)
                #     text_updator(Chinesestr + '\n' + Engstr)
                # print(Chinesestr)
                # print(int(time.time() * 1000) - asr_time)
            continue_recording = True

        send_finish(ws_app)
        stream.stop_stream()
        stream.close()
        p.terminate()
        print("Thread stopped.")

    def stop(self):
        self._is_running = False


def toggle_thread():
    global TranslateThread, is_thread_running
    if is_thread_running:
        stop_thread()
        start_button.config(text="同声传译")
        is_thread_running = False
    else:
        start_thread()
        start_button.config(text="关闭同声传译")
        is_thread_running = True


def start_thread():
    global my_thread
    global text_updator
    global ws_app
    text_updator = create_transparent_window(root)
    # 创建 WebSocketApp 实例
    ws_app = websocket.WebSocketApp(business_config.get("asr_url"),
                                    on_open=on_open,
                                    on_message=on_data,
                                    on_error=on_error,
                                    on_close=on_close)
    my_thread = TranslateThread()
    my_thread.start()


def stop_thread():
    global my_thread
    global text_window
    if my_thread:
        if text_window is not None:
            text_window.destroy()
        my_thread.stop()
        print("Thread stopped successfully.")
    else:
        print("Thread is not running.")


def ask_ai(content):
    chat_box.config(state=tk.NORMAL)
    chat_box.insert(tk.END, "Bot: ")
    completion = client.chat.completions.create(
        model="gpt-3.5-turbo-0125",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": content}
        ],
        stream=True
    )
    collected_messages = []
    for chunk in completion:
        if chunk.choices[0].delta.content is None:
            break
        collected_messages.append(chunk.choices[0].delta.content)
        chat_box.insert(tk.END, chunk.choices[0].delta.content)
        root.update_idletasks()
        chat_box.yview_moveto(1.0)  # 将聊天框滚动到最底部
    chat_box.insert(tk.END, "\n")
    root.update_idletasks()
    chat_box.config(state=tk.DISABLED)


def send_message():
    message = user_input.get()
    if message.strip() != "":
        chat_box.config(state=tk.NORMAL)
        chat_box.insert(tk.END, "您: " + message + "\n")
        chat_box.config(state=tk.DISABLED)
        user_input.delete(0, tk.END)
        print("Sent:", message)
        root.update_idletasks()  # 更新Tkinter窗口以确保显示新消息
        ai_thread = threading.Thread(target=ask_ai, args=(message,))
        # 启动线程
        ai_thread.start()
        # 等待线程结束
        # ai_thread.join()
        # ask_ai(message)


def bot_send_message(message):
    chat_box.config(state=tk.NORMAL)
    chat_box.insert(tk.END, "Bot: " + message + "\n")
    chat_box.config(state=tk.DISABLED)


def take_screenshot():
    # 在按钮按下后隐藏主窗口
    root.withdraw()
    # 截屏
    screenshot = pyautogui.screenshot()
    # 显示主窗口
    root.deiconify()
    # 保存截屏
    screenshot.save("screenshot.png")
    bot_send_message("ScreenShot mode")
    show_screenshot("screenshot.png")


def add_overlay(image_path, alpha=128, x1=None, x2=None, y1=None, y2=None):
    img = Image.open(image_path).convert('RGBA')
    overlay = Image.new('RGBA', img.size, (0, 0, 0, alpha))  # 创建透明度为0的蒙版
    draw = ImageDraw.Draw(overlay)
    if x1 is not None and x2 is not None and y1 is not None and y2 is not None:
        draw.rectangle([x1, y1, x2, y2], fill=(0, 0, 0, 0))  # 在指定矩形区域内添加半透明黑色蒙版
    img = Image.alpha_composite(img, overlay)
    return img


def update_overlay(event):
    global img_tk, img, label, overlay_image, overlay_photo
    global start_x, start_y
    if start_x < event.x:
        x1 = start_x
        x2 = event.x
    else:
        x2 = start_x
        x1 = event.x
    if start_y < event.y:
        y1 = start_y
        y2 = event.y
    else:
        y2 = start_y
        y1 = event.y
    # 更新蒙版
    overlay_image = add_overlay("screenshot.png", 128, x1, x2, y1, y2)  # 根据实际情况更新截图路径
    img_tk = ImageTk.PhotoImage(overlay_image)

    # 更新Label的图像
    label.config(image=img_tk)
    # label.image = overlay_photo  # 需要保存对图片对象的引用，否则会被垃圾回收


def show_screenshot(image_path):
    # 添加半透明蒙版
    img = add_overlay(image_path)

    # 将图像转换为Tkinter支持的格式
    global img_tk
    img_tk = ImageTk.PhotoImage(img)

    # 创建全屏窗口
    fullscreen_window = tk.Toplevel()
    fullscreen_window.attributes("-fullscreen", True)

    # 显示图像
    global label
    label = tk.Label(fullscreen_window, image=img_tk)
    label.pack(fill=tk.BOTH, expand=tk.YES)

    # 绑定鼠标按下和释放事件
    label.bind("<ButtonPress-1>", start_select)
    label.bind("<B1-Motion>", update_overlay)  # 鼠标移动时动态更新蒙版
    label.bind("<ButtonRelease-1>", end_select)

    # 保持窗口显示
    fullscreen_window.mainloop()


def start_select(event):
    global start_x, start_y
    start_x = event.x
    start_y = event.y


def end_select(event):
    global start_x, start_y
    end_x = event.x
    end_y = event.y
    # print("Selected Area: ", (start_x, start_y), "-", (end_x, end_y))
    event.widget.master.destroy()  # 关闭全屏窗口

    # 截取选定区域
    screenshot = pyautogui.screenshot()
    selected_area = screenshot.crop(
        (min(start_x, end_x), min(start_y, end_y), max(start_x, end_x), max(start_y, end_y)))
    # 覆盖保存截图
    selected_area.save("screenshot.png")
    bot_send_message("正在识别图中信息")
    with BytesIO() as output:
        selected_area.save(output, format='PNG')
        image_bytes = output.getvalue()

    # 将 BytesIO 对象中的图像数据转换为 bytearray 数组
    bytearray_image = bytearray(image_bytes)
    t = threading.Thread(target=vision_pic, args=(bytearray_image,))
    t.start()


def on_entry_click(event):
    if user_input.get() == 'Wait for typing...':
        user_input.delete(0, "end")  # 删除默认的提示文字
        user_input.config(fg="black")  # 修改文本颜色


def on_focus_out(event):
    if user_input.get() == '':
        user_input.insert(0, "Wait for typing...")  # 如果没有输入内容则显示默认提示文字
        user_input.config(fg="grey")  # 修改文字颜色为灰色


def show_main_page():
    sub_frame.pack_forget()  # 隐藏子界面
    main_frame.pack(fill=tk.BOTH, expand=True)  # 显示主界面


def show_sub_page1():
    main_frame.pack_forget()  # 隐藏主界面
    sublabel.configure(text="\n识图解析 | Identifying\n")

    sub_frame.pack(fill=tk.BOTH, expand=True)  # 显示子界面
    user_input.pack_forget()
    button_frame.pack_forget()
    take_screenshot()


def show_sub_page2():
    toggle_thread()


def show_sub_page3():
    main_frame.pack_forget()  # 隐藏主界面
    sublabel.configure(text="\n智能问答 | Ask Anything\n")
    sub_frame.pack(fill=tk.BOTH, expand=True)  # 显示子界面
    chat_box.config(state=tk.NORMAL)
    chat_box.delete(1.0, tk.END)
    chat_box.config(state=tk.DISABLED)
    user_input.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
    send_button.pack(side=tk.LEFT)
    button_frame.pack()


def openurl():
    webbrowser.open_new_tab("https://github.com/LeslinD/zhongwenzhiying")


# 创建全局变量
text_box = None
text_window = None
text_updator = None
ws_app = None
# 创建主窗口
root = tk.Tk()
root.title("中文智影")
root.geometry("700x900")

style = ttk.Style()

main_frame = ttk.Frame(root)

# 从文件加载图片
image = Image.open("res/logo.png")
# 调整图片大小
image = image.resize((400, 126), Image.LANCZOS)
# 创建Tkinter PhotoImage对象
photo = ImageTk.PhotoImage(image)
# 在标签中显示图片
mainlabel = tk.Label(main_frame, image=photo)
mainlabel.pack(fill=tk.BOTH)

style.configure('My.TFrame', background='#FFD6D4')
style.configure("My.TLabel", background="#E25148")  # 设置标签的背景颜色
style.configure('My.TButton', padding=(20, 20), background="#E25148", borderwidth=0,
                font=("微软雅黑", 20, "bold"))  # 设置按钮的字体为Helvetica，大小为12
style.configure('My2.TButton', padding=(20, 20), background="#FFD6D4", borderwidth=0,
                font=("微软雅黑", 20, "bold"))  # 设置按钮的字体为Helvetica，大小为12
style.map("My2.TButton",
          background=[('active', '#FFD6D4')])

main_frame_sub = ttk.Frame(main_frame, style='My.TFrame')

tlabel = ttk.Label(main_frame_sub, text="中华文化智能影视助手", anchor="center", background="#FFD6D4",
                   foreground="black", font=("优设标题黑", 28, "normal"), padding=(0, 60, 0, 10))
tlabel.pack(fill=tk.BOTH)

tlabel2 = ttk.Label(main_frame_sub, text="CHINESE CULTURE INTELLIGENT\n              VIDEO ASSISTANT", anchor="center",
                    background="#FFD6D4", foreground="black", font=("Arial", 20, "italic"), padding=(0, 0, 0, 40))
tlabel2.pack(fill=tk.BOTH)

main_button1 = ttk.Button(main_frame_sub, text="截图识别 | Screenshot Identifying", command=show_sub_page1,
                          style="My.TButton")
main_button1.pack(fill=tk.BOTH, pady=10)

start_button = ttk.Button(main_frame_sub, text="同声传译 | Video Interpretation ", command=show_sub_page2,
                          style="My.TButton")
start_button.pack(fill=tk.BOTH, pady=10)

main_button3 = ttk.Button(main_frame_sub, text="智能问答 |     Ask Anything     ", command=show_sub_page3,
                          style="My.TButton")
main_button3.pack(fill=tk.BOTH, pady=10)

main_button4 = ttk.Button(main_frame_sub, text="       关于我们 | ABOUT US       ", command=openurl,
                          style="My2.TButton")
main_button4.pack(side=tk.BOTTOM, fill=tk.BOTH, pady=10)

main_frame_sub.pack(fill=tk.BOTH, expand=True)

## 子界面
sub_frame = ttk.Frame(root)

sublabel = ttk.Label(sub_frame, text="", style="My.TLabel", font=("微软雅黑", 14, "bold"))
sublabel.config(anchor="center", foreground="white")
sublabel.pack(fill=tk.BOTH)

# 创建框架
frame = tk.Frame(sub_frame)
frame.pack(fill=tk.BOTH, expand=True)

# 创建垂直滚动条
vertical_scrollbar = tk.Scrollbar(frame)
vertical_scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

# 创建横向滚动条
horizontal_scrollbar = tk.Scrollbar(frame, orient=tk.HORIZONTAL)
horizontal_scrollbar.pack(side=tk.BOTTOM, fill=tk.X)

# 创建文本框
chat_box = tk.Text(frame, width=57, state=tk.DISABLED, yscrollcommand=vertical_scrollbar.set,
                   xscrollcommand=horizontal_scrollbar.set)
chat_box.pack(side=tk.TOP, fill=tk.BOTH, expand=True)

# 配置垂直滚动条和文本框的联动
vertical_scrollbar.config(command=chat_box.yview)
# 配置横向滚动条和文本框的联动
horizontal_scrollbar.config(command=chat_box.xview)

# 创建消息输入框
user_input = tk.Entry(sub_frame, width=50, highlightbackground="blue", highlightcolor="blue", font=("黑体", 12))
user_input.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
user_input.insert(0, "Wait for typing...")
user_input.config(fg="grey")  # 设置默认文字的颜色为灰色
user_input.bind('<FocusIn>', on_entry_click)
user_input.bind('<FocusOut>', on_focus_out)

button_frame = tk.Frame(sub_frame)
button_frame.pack()

# 创建发送按钮
send_button = tk.Button(button_frame, text="Ask Ying", command=send_message)
send_button.pack(side=tk.LEFT)

# 将按钮的背景色设置为透明
style.configure("White.TButton", background="white", borderwidth=0)  # 设置按钮背景颜色为白色
style.map("White.TButton",
          background=[('active', 'white')])

image = tk.PhotoImage(file="res/home_logo.png")
scaled_image = image.subsample(10, 10)  # 调整按钮图片大小
btn = ttk.Button(sub_frame, text="HOME", image=scaled_image, style="White.TButton", takefocus=0, command=show_main_page)
btn.place(relx=1, rely=1, x=-50, y=-100, anchor="se")  # 将按钮放置在窗口的右下角

main_frame.pack(fill=tk.BOTH, expand=True)
is_thread_running = False

if __name__ == "__main__":
    token = fetch_token()
    trans_token = fetch_token_trans()
    print(token, trans_token)
    print(baidu_translate('你好'))
    logging.basicConfig(format='[%(asctime)-15s] [%(funcName)s()][%(levelname)s] %(message)s')
    logger.setLevel(logging.INFO)
    logger.info("begin")
    logger.info("uri is " + business_config.get("asr_url"))
    root.mainloop()
