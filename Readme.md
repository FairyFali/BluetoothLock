# Java实现蓝牙测距自动锁屏功能
## 设计思路
* 手机监听PC端的信号强度（可以获得没有Bug），电脑端设计一个消息接收模块，根据手机传过来的RSSI判断当前的距离，对屏保进行设置；
* 自定义屏幕保护，对屏幕保护时期的Ctrl、Shift、Del等可以触发系统调用的按键进行屏蔽，类似于网吧管理平台。
## 应用缺点
* ① 蓝牙信号十分不稳定，并且与是否存在障碍物有很大的关系；
* ② Java对于蓝牙模块的支持力度较小，蓝牙模块上一版本的更新是2008年。
## TODO
* 调试PC端不能获取手机距离的Bug；
* 尝试对Window或者Mac的锁屏进行解锁，尽可能的实现思路一的功能。

