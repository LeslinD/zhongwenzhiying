<img src="https://socialify.git.ci/LeslinD/zhongwenzhiying/image?font=Raleway&language=1&logo=https%3A%2F%2Fs4.aconvert.com%2Fconvert%2Fp3r68-cdx67%2Fazvrw-1j2ik.svg&name=1&owner=1&pattern=Plus&stargazers=1&theme=Light" alt="zhongwenzhiying" />

<h1 align="center">中文智影 | 打造中华文化智能影视助手</h1>

## 一. AI-VIDEO-TOOL

AI-VIDEO-TOOL是程序的核心模块，采用Python进行编写，作为一种良好的脚本语言，Python保证了良好的可移植性，确保了在主流PC操作系统上（Windows/Linux/MacOS）上均能运行。对于Windows操作系统，不需要客户机上安装任何Python环境，在发布时即可使用Pyinstaller打包发布可执行文件。目前程序的主要功能有三个，分别是向AI提问问答，AI截屏内容识别，实时翻译。下文将逐个解释每个功能的详细细节。

### **AI提问问答**

发送文字提AI是一个相对简单而用户友好的功能。TKinter提供了Chatbox组件和用户输入框组件，所以当用户点击按钮的时候，读取输入框内的所有内容，随后调用Openai的API接口即可。当前从成本方面考虑，默认采用的是GPT-3.5 Turbo，也可以提供自己选择模型的ComboBox。当前程序调用了ChatGPT的国内中转接口，来降低网络延迟对回答时效性的影响。与ChatGPT之间的接口支持Stream流式传输，因此用户不需要等待过多的时间让AI生成完整回答后才能接收，而是在AI生成的过程中，接收API发来的“增量”包，把AI当前生成的几个字/几句话不断以附加的形式添加到对话框中。这也模拟了人类输入文字的方式，给用户带来更好的使用体验。如下图分别是生成过程中以及生成完成的完整回答。

### AI截屏内容识别

本功能主要实现了图像识别。pyautogui库提供了截屏函数，将截取整个屏幕。但对于整个屏幕的识别必然是成本更高而且结果更不准确的，因为用户并不一定在全屏观看视频，或者用户只想对屏幕的某个区域进行截屏，因此还需要一部交互式的选择图像处理。在用户点击截屏之后，程序将对整个屏幕进行截屏保存，同时把这一帧的图像全屏显示出来，并覆盖RGBA(0,0,0,128)的半透明蒙版，指示用户当前正在进行截屏的图像处理。用户此时可以通过鼠标点击并拖动，来框选出区域。每次用户鼠标移动的过程，蒙版将自动更新，即用户鼠标点击处与当前所在处框出的矩形将修改蒙版RGBA值为(0,0,0,0)全透明，进行蒙版的动态更新。当用户的鼠标松开时，回调函数将根据鼠标最开始点击的坐标以及当前坐标，对上文提到的图片进行裁剪并保存。由此实现了类似于QQ或者微信的能与用户进行互动的选区截屏。下图分别是开始截图模式，以及鼠标拖动后的效果。最后截出来的图片即为高亮区域，便于用户操作。

在用户完成了截图之后，不但把截图存储到当前目录下进行缓存，同时在也直接把图像数据当作ByteArray存入变量中，向后端训练好的物品识别模型的API接口传入相关参数，并使用预设的prompt，让AI进行图像识别后仍然在Chatbox内输出。

### 实时翻译

实时翻译属于程序的核心部分。本程序使用pyaudio库。这一库通常只能抓取电脑的麦克风输入，但我们可以设置它的抓取设备为立体声混音，则可以抓取电脑将通过扬声器输出的声音。让用户自行来设置系统的默认输入设备是不方便的，而且要求用户对声音设置进行修改对于没有电脑基础的人或许有些困难，或者在未来可能导致意想不到的声音故障。本软件通过直接枚举用户电脑中所有存在的输入设备，自动检测是否含有立体声混音设备并设置为默认抓取来源，不更改系统设置的同时实现对扬声器声音的获取。这样的操作让程序能够在启动此功能后持续监听用户电脑的所有声音，则不仅限于是对某个软件播放视频进行处理，也不限制媒体的形式，只要有声音输出就可以同声传译。

在进行ASR识别之前，只能抓取声音是不够的，我们也需要判断什么时候是有人说话的，什么时候是需要进行识别的，以及什么时候识别结束。这里采用了一种算法，首先计算当前0.5秒内捕捉到的所有音频帧的声强平均值并判断是否大于某一固定值，来判断是否有人说话，因为有人说话时通常声强更大。在满足该条件之后便开始录音，同时继续对声强开始判断。设置一个0.5秒的定时器，如果声音声强均值小于固定值并持续超过0.5秒则停止录音并进行语音转文字，如果出现了声强均值大于固定值的时刻则重置定时器，即空闲时间为0.5秒。为了防止录音时间过长，设置一个上限来规定超时，例如如果录音时长超过4秒则强制停止并进行语音识别同时进行下一轮录音。

高速、准确的ASR也是同声传译的必要部分。后端连接产品的超高精度高性能语音识别模型，录音后使用变量存储Bytearray直接作为wav格式传入给此API接口，将在返回值内得到识别内容。 类似的，采用模型接口对返回的中文字符串进行翻译。无论是ASR还是翻译接口都需要在调用之前获取一次鉴权令牌。令牌持续时间较长，同声传译模式通过“切换”按钮控制，即按一下开再按一次关，则可以在每次启动同声传译功能时进行一次获取。 GUI的设计灵感来源于YouTube等类似视频网站的字幕样式，即在顶层创建一个没有标题栏不可移动的半透明黑色覆盖窗口，该窗口作为主窗口的子窗口创建，在其中显示中文和英文双语字幕。

整个翻译功能被封装成完整的一个类，通过创建类可以直接调用其中所有功能。用户按下按钮后，可以直接通过类进行线程的启动，保证了程序的并行性，让用户可以在同声传译功能启用的同时，正常使用程序的其他功能。 默认是从中文翻译到英文，当然因为程序采用UTF8编码理论支持所有语言，只需调整调用API接口时传入的参数就可以控制翻译语言。那么仍然可以通过添加ComboBox组件让用户自行选择翻译的源语言和目标语言，增强程序的可用性。

## 二. JavaMovieDB

JavaWebMovieDB是本地构建的影视管理推荐系统，是一个基于公开影视作品数据库The Movie Database（TMDB）搭建的本地影视作品管理平台，使用前端Sveltekit框架和后端Java管理，并使用考虑用户偏好和评分变化的混合协同过滤推荐算法为用户推荐相关内容下更加个性化的影视作品。

### 环境配置

- **Java**: jdk-17
- **IDE**: JetBrains IntelliJ IDEA 2022.3.2（专业版）
- **MySQL**: v8.0.33
- **Tomcat**: v9.0.83
- **JARs**:
  - mysql-connector-j-8.0.33.jar：用于连接MySQL数据库；
  - javax.servlet.*.jar：用于构建Servlet接口；
  - jackson-annotations/core/databind-*.jar：用于将对象/JSON转换为JSON/对象；
  - 更多细节可在lib目录中查看。

配置详情可在CONFIGURATION.md中查看。

### 特点

- 电影和演职员查询/更新的简明功能
  - 应用MVC模式；详情可在Entity, Dao, Servlet中查看。
- SQL生成的简易模板
  - 分离常量数据（如表名、列名等）；详情可在SQLTemplate中查看。
- 按年份或电影进行统计分析
  - 为特定需求生成二维统计表；详情可在Statistics中查看。
- 推荐算法
  - 使用结合协同过滤技术、当前页面内容和历史浏览信息的整体混合推荐算法对用户个性化推荐，详情可在recommend下查看
- 现代前端框架
  - 利用Svelte框架构建简洁美观的Node.js应用；详情可在MovieDB app中查看。
