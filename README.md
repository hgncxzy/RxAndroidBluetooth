# RxAndroidBluetooth

### 概述

基于 git 项目 [AndroidBluetoothPro](https://github.com/duoshine/AndroidBluetoothPro) 封装实现的蓝牙调试工具 demo。基于该库，实现了如下功能：

1. 获取蓝牙列表(过滤了蓝牙名称为空的设备)
2. 搜索、连接、重连以及其他状态改变发送广播的二次封装
3. 发送 16 进制字符串与接收 16 进制字符串
4. 搜索名称过滤
5. 自动扫描
6. 自动重连上一次的设备

### 使用

该项目可用于快速上手低功耗蓝牙项目开发。具体步骤如下：

#### 引入依赖

```groovy
implementation 'com.github.duoshine:AndroidBluetoothPro:1.8'
```

#### 实现广播接收器中的接口

```kotlin
MyReceiver.Receiver
```

#### 注册与反注册广播接收器

在设备连接页面，请注册这些广播：

```kotlin
override fun onResume() {
        super.onResume()
        registerMyReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        BLE.disConnect()
        unRegisterMyReceiver()
    }

    private fun registerMyReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(scanResultNotify)
        intentFilter.addAction(scanFinish)
        intentFilter.addAction(connectSuccess)
        intentFilter.addAction(connectFail)
        intentFilter.addAction(connectTimeout)
        intentFilter.addAction(retryConnect)
        intentFilter.addAction(disconnect)
        requireActivity().registerReceiver(myReceiver, intentFilter)
    }

    private fun unRegisterMyReceiver() {
        requireActivity().unregisterReceiver(myReceiver)
    }
```

在收发数据页面，请注册这些广播：

```kotlin
private fun registerMyReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(writeSuccess)
        intentFilter.addAction(writeFail)
        intentFilter.addAction(notifyChanged)
        requireActivity().registerReceiver(myReceiver, intentFilter)
    }

    private fun unRegisterMyReceiver() {
        requireActivity().unregisterReceiver(myReceiver)
    }
```

#### 发送数据

输入的是 16 进制字符串，发送的是字节数组。

```kotlin
 val text = edit_et.text.toString().trim()
 BLE.write(decodeHex(text))
```



#### 接收数据

在 MyReceiver.Receiver 接口回调方法中处理收到的数据，接收的是 16 进制字符串。

```kotlin
 @SuppressLint("SetTextI18n")
    override fun update(action: String, data: String) {
        when (action) {
            writeSuccess -> {
                result_tv.text = "发送成功"
            }
            writeFail -> {
                result_tv.text = "发送失败"
            }
            notifyChanged -> {
                result_tv.text = "收到数据：$data"
            }
        }
    }
```

