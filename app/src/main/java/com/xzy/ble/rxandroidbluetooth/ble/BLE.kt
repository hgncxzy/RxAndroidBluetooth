package com.xzy.ble.rxandroidbluetooth.ble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.xzy.ble.rxandroidbluetooth.RootApp.Companion.getContext
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.connectSuccess
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.connectTimeout
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.disconnect
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.notifyChanged
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.retryConnect
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.scanFinish
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.scanResultNotify
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.writeFail
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.writeSuccess
import com.xzy.ble.rxandroidbluetooth.functions.encodeHexStr
import duoshine.rxandroidbluetooth.BluetoothController
import duoshine.rxandroidbluetooth.BluetoothWorker
import duoshine.rxandroidbluetooth.bluetoothprofile.BluetoothConnectProfile
import duoshine.rxandroidbluetooth.bluetoothprofile.BluetoothWriteProfile
import duoshine.rxandroidbluetooth.observable.Response
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Author: xzy
 */
@Suppress("unused")
object BLE {
    /**
     * ble deviceList
     * */
    private var deviceList: MutableList<Device>? = ArrayList()

    const val logTag: String = "[-------XZY_BLE------]"

    /**
     * ble uuid 、 mac、name 配置
     * */
    private val serviceUUID = UUID.fromString("00001000-0000-1000-8000-00805f9b34fb")
    private val writeUUID = UUID.fromString("00001001-0000-1000-8000-00805f9b34fb")
    private val notifyUUID = UUID.fromString("00001002-0000-1000-8000-00805f9b34fb")
    var targetDeviceMac = "A4:34:F1:4A:08:05"
    var targetDeviceName = ""

    /**
     * 扫描周期
     * */
    private const val scanTime = 5000L

    /**
     * 蓝牙控制器
     * **/
    var bluetoothController: BluetoothWorker? = null

    private var timer: Timer? = Timer()
    private var timer2: Timer? = Timer()

    /**
     * 权限请求代码
     * */
    val permissionReqCode = 0x91
    /**
     * 蓝牙使能请求代码
     * */
    val enableBTCode = 0x92

    object Actions {
        /**
         * 扫描结果广播
         * */
        const val scanResultNotify = "com.xzy.ble.test.scan.result.notify"
        const val scanFinish = "com.xzy.ble.test.scan.result.finish"

        /**
         *连接广播
         * */
        const val connectSuccess = "com.xzy.ble.test.connect.success"
        const val connectFail = "com.xzy.ble.test.connect.fail"
        const val connectTimeout = "com.xzy.ble.test.connect.timeout"
        const val disconnect = "com.xzy.ble.test.connect.disconnect"
        const val retryConnect = "com.xzy.ble.test.connect.retry.connect"


        /**
         * 发送与接收广播
         * */
        const val writeSuccess = "com.xzy.ble.test.write.success"
        const val writeFail = "com.xzy.ble.test.write.fail"
        const val notifyChanged = "com.xzy.ble.test.notify.changed"
    }


    /**
     *请求授权
     */
    fun requestPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
            )
            val permissionsList = ArrayList<String>()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsList.add(permission)
                }
            }
            if (permissionsList.size != 0) {
                val permissionsArray = permissionsList.toTypedArray()
                ActivityCompat.requestPermissions(
                    context as Activity, permissionsArray,
                    permissionReqCode
                )
            }
        }
    }

    /**
     * 初始化
     * */
    fun initBluetoothController() {
        bluetoothController =
            BluetoothController.Builder(getContext())
                .setNotifyUuid(notifyUUID)
                .setServiceUuid(serviceUUID)
                .setWriteUuid(writeUUID)
                .build()
    }

    private var scanDispose: Disposable? = null
    /**
     * 开启扫描
     * */
    fun startScan() {
        scanDispose = bluetoothController!!
            .startLeScan()
            .timer(scanTime, TimeUnit.MILLISECONDS)
            .filter { response ->
                !TextUtils.isEmpty(response.getDevice()?.name)
            }
            .map {
                it.getDevice()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { checkScanResult(it) },
                { error -> checkError(error) },
                {
                    sendBroadcast(scanFinish, "Scanning completed")
                    Log.d(logTag, "扫描完成")
                })
    }

    /**
     * 停止扫描
     * */
    fun stopScan() {
        scanDispose?.dispose()
    }

    private var connectDisposable: Disposable? = null

    /**
     * 连接设备
     * **/
    fun connect() {
        connectDisposable = bluetoothController!!
            .connect(targetDeviceMac)
            .auto()
            .timer(6000, TimeUnit.MILLISECONDS)
            .subscribe(
                { response -> checkResultState(response) },
                { error -> checkError(error) }
            )
    }

    /**
     * 断开连接
     * */
    fun disConnect() {
        connectDisposable?.dispose()
    }

    @SuppressLint("CheckResult")
    fun write(byteArray: ByteArray) {
        Log.d(logTag, "send data：" + encodeHexStr(byteArray))
        bluetoothController!!
            .writeOnce(byteArray)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response -> checkResult(response) },
                { error -> checkError(error) }
            )
    }


    private fun checkResultState(response: Response) {
        when (response.code) {
            BluetoothConnectProfile.connected -> {
                sendBroadcast(connectSuccess, "Connection successful")
                Log.d(logTag, "连接成功")
            }
            BluetoothConnectProfile.disconnected -> {
                sendBroadcast(disconnect, "Disconnect")
                Log.d(logTag, "断开连接")
            }
            BluetoothConnectProfile.connectTimeout -> {
                sendBroadcast(connectTimeout, "Connection time-out")
                Log.d(logTag, "连接超时")
            }
            BluetoothConnectProfile.enableNotifySucceed -> Log.d(logTag, "启用通知特征成功")
            BluetoothConnectProfile.enableNotifyFail -> Log.d(logTag, "启用通知特征失败")
            BluetoothConnectProfile.serviceNotfound -> Log.d(logTag, "未获取到对应uuid的服务特征")
            BluetoothConnectProfile.notifyNotFound -> Log.d(logTag, "未获取到对应uuid的通知特征")
            BluetoothConnectProfile.reconnection -> {
                sendBroadcast(retryConnect, "Reconnecting")
                Log.d(logTag, "重连中")
            }
        }
    }

    private fun checkResult(response: Response) {
        when (response.code) {
            BluetoothWriteProfile.writeSucceed -> {
                sendBroadcast(writeSuccess, "Load successful")
                Log.d(logTag, "写入成功")
            }
            BluetoothWriteProfile.writeFail -> {
                sendBroadcast(writeFail, "Load failure")
                Log.d(logTag, "写入失败")
            }
            //Arrays.toString(response.data)
            BluetoothWriteProfile.characteristicChanged -> {
                //sendBroadcast(notifyChanged, parseBytes2HexString(response.data!!))
                sendBroadcast(notifyChanged, encodeHexStr(response.data!!, true))
                Log.d(logTag, "New value received:${encodeHexStr(response.data!!)}")
            }
        }
    }

    private fun sendBroadcast(action: String, data: String) {
        val intent = Intent(action)
        intent.putExtra(action, data)
        getContext().sendBroadcast(intent)
        Log.d(logTag, "发送广播，action = $action,data = $data")
    }


    private fun checkScanResult(it: BluetoothDevice?) {
        Log.d(logTag, " Device found:${it!!.name}---${it.address}")
        sendBroadcast(scanResultNotify, it.name + "|" + it.address)
    }

    private fun checkError(error: Throwable) {
        Log.d(logTag, "error:$error")
    }
}

/**
 * Author: xzy
 */
class Device(var name: String, var mac: String) {
    override fun toString(): String {
        return "Device{" +
                "mac='" + mac + '\'' +
                ", name='" + name + '\'' +
                '}'
    }
}


/**
 * Author: xzy
 */
class MyReceiver(receiver: Receiver) : BroadcastReceiver() {
    private val tag = "[MyReceiver]"
    private var result: Receiver = receiver
    @SuppressLint("SetTextI18n")
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getStringExtra(intent.action)?.let {
            Log.d(tag, it)
            result.update(intent.action!!, it)
        }
    }

    interface Receiver {
        fun update(action: String, data: String)
    }
}