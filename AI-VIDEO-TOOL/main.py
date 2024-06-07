import tkinter as tk
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
)
start_x = None
start_y = None
draw = None


def vision_pic(btarr):
    base64_image = base64.b64encode(btarr).decode('utf-8')
    headers = {
      "Content-Type": "application/json",
      "Authorization": "Bearer "
    }
    payload = {
      "model": "gpt-4-vision-preview",
      "messages": [
        {
          "role": "user",
          "content": [
            {
              "type": "text",
              "text": "用中文告诉我，这张图片是否有什么中国特色的东西？有哪些地方与中国相关？"
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
    params_query = urlencode(params);
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
        self._is_running = True
        minimum_volume = 2000  # 最小声音，大于则开始录音，否则结束
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
                elif max_volume <= minimum_volume or (recording_started and int(time.time() * 1000) - start_time > 4000):
                    idle_time = int(time.time() * 1000) - detected_time
                    if idle_time > 400 or (recording_started and int(time.time() * 1000) - start_time > 4000):
                        continue_recording = False
                        recording_started = False
                        # print("out")
                if recording_started:
                    # print(data)
                    for i in range(0, len(data), 1000):
                        recorded_audio.extend(data)
                else:
                    time.sleep(0.1)
                time_counter += 1
            # print(len(recorded_audio))
            if len(recorded_audio) > 0:
                Chinesestr = asr(recorded_audio)
                # print("Chinesestr:"+Chinesestr)
                if (Chinesestr!=''):
                    Engstr = baidu_translate(Chinesestr)
                    text_updator(Chinesestr + '\n' + Engstr)
                print(Chinesestr)
            continue_recording = True
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
    text_updator = create_transparent_window(root)
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
            break;
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
        #ai_thread.join()
        #ask_ai(message)


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
    global img_tk, img, label, overlay_image,overlay_photo
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
    #label.image = overlay_photo  # 需要保存对图片对象的引用，否则会被垃圾回收

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


text_window = None
text_updator = None
# 创建主窗口
root = tk.Tk()
root.title("中文智影")
# 创建聊天记录框
scrollbar = tk.Scrollbar(root)
scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
chat_box = tk.Text(root, height=20, width=50, state=tk.DISABLED,yscrollcommand=scrollbar.set)
chat_box.pack()
# 创建消息输入框
user_input = tk.Entry(root, width=50)
user_input.pack()
button_frame = tk.Frame(root)
button_frame.pack()
# 创建发送按钮
send_button = tk.Button(button_frame, text="发送文字问AI", command=send_message)
send_button.pack(side=tk.LEFT, padx=5)
# 创建截屏按钮
screenshot_button = tk.Button(button_frame, text="截屏问AI", command=take_screenshot)
screenshot_button.pack(side=tk.LEFT, padx=5)
is_thread_running = False
start_button = tk.Button(button_frame, text="同声传译", command=toggle_thread)
start_button.pack(side=tk.LEFT, padx=5)


if __name__ == "__main__":
    token = fetch_token()
    trans_token = fetch_token_trans()
    print(token, trans_token)
    print(baidu_translate('你好'))
    root.mainloop()
