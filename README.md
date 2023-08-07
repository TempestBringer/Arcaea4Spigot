# Arcaea4Spigot

在Minecraft中实现了Arcaea谱面播放（Spigot系列核心）
对比Python实现的版本，提供了以下特性：
 - 减少90%以上编译和运行时内存消耗
 - 减少95%以上谱面编译时间
 - 更加灵活的在线Aff选择-编译-播放
 - 优化了方块填充顺序，减少了重复填充
 - 一定的自制谱兼容性

展望：
 - 添加打击效果，这涉及判定
 - 优化代码结构以及可读性（如你所见这里现在是一坨屎山）
 - 多平台适配（其他的服务器核心，或者客户端执行，这样能减少网络开支）

如何使用：
0. 要注意，插件本体不包含任何关于Arcaea的游戏素材，无论是谱面还是songlist这里都是不能提供的，需要你自己补充；
1. 准备一个1.19.2+的spigot系的minecraft服务器核心，下载右边release中的.jar文件放入plugins文件夹中作为插件加载；
2. 启动服务器，初次启动本插件如有报错不必担心，关闭服务器并编辑本插件的config.yml；
3. 特别注意编辑config中的"Render.Position.dimension"设置为用于播放的维度
4. 特别注意编辑"File.execute_path"为你自己准备的谱面以及songlist相关资源文件夹；资源文件夹要求下有songlist、packlist(这个暂时没用)以及songs文件夹，songs文件夹里有若干以songlist中各歌曲的id作为命名的文件夹，而这些文件夹中需要有对应的谱面，分别以0.aff，1.aff，2.aff以此类推，如果不使用绝对路径，则相对路径的起点应当是服务端jar所在路径。
5. 配置好以后就可以使用/arcaea系列命令进行观看了。

参考视频：
https://www.bilibili.com/video/BV1du411J7aC