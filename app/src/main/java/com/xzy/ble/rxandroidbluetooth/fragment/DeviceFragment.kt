package com.xzy.rxandroidbluetooth.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.xzy.ble.rxandroidbluetooth.R
import com.xzy.ble.rxandroidbluetooth.adapter.DeviceListAdapter
import com.xzy.ble.rxandroidbluetooth.ble.BLE
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.connectFail
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.connectSuccess
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.connectTimeout
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.disconnect
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.retryConnect
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.scanFinish
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.scanResultNotify
import com.xzy.ble.rxandroidbluetooth.ble.BLE.logTag
import com.xzy.ble.rxandroidbluetooth.ble.BLE.targetDeviceMac
import com.xzy.ble.rxandroidbluetooth.ble.Device
import com.xzy.ble.rxandroidbluetooth.ble.MyReceiver

import com.xzy.ble.rxandroidbluetooth.functions.Sp
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_device.*


/**
 * @author 002034
 */
@Suppress("unused")
class DeviceFragment : Fragment(), AdapterView.OnItemClickListener, MyReceiver.Receiver {
    val myReceiver = MyReceiver(this)
    var mListener: MyListener? = null
    val mCompositeDisposable = CompositeDisposable()
    var deviceAdapter: DeviceListAdapter? = null
    var dialog: ZLoadingDialog? = null
    var refreshLayout: SwipeRefreshLayout? = null
    private val deviceList: MutableList<Device> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deviceAdapter = DeviceListAdapter(requireActivity())
        devices.adapter = deviceAdapter
        devices.onItemClickListener = this
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            mListener?.updateStatus("List being updated")
            BLE.startScan()
        }
        refresh()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        BLE.disConnect()
        mListener?.updateStatus("connecting...")
        targetDeviceMac = deviceList[position].mac
        dialog
            ?.setLoadingBuilder(Z_TYPE.CHART_RECT)
            ?.setLoadingColor(Color.RED)
            ?.setHintText("connecting...")
            ?.setCancelable(false)
            ?.setCanceledOnTouchOutside(false)
            ?.setHintTextSize(16f)
            ?.setHintTextColor(Color.GRAY)
            ?.setDurationTime(0.5)
            ?.setDialogBackgroundColor(Color.parseColor("#CC111111"))
            ?.show()
        BLE.connect()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = requireActivity() as MyListener
    }

    private fun refresh() {
        deviceList.clear()
        deviceAdapter?.setList(deviceList)
        swipeRefreshLayout?.isRefreshing = true
        mListener?.updateStatus("List being updated")
        BLE.startScan()
    }

    interface MyListener {
        fun updateStatus(str: String)
    }

    @SuppressLint("DefaultLocale")
    override fun update(action: String, data: String) {
        Log.d(logTag, "[DeviceFragment] 收到数据: $data")
        when (action) {
            scanResultNotify -> {
                val strArray = data.split("|")
                val device = Device(strArray[0], strArray[1])
                val inputDevName = Sp.instance?.getString("inputDevName")
                val showName = Sp.instance?.getBoolean("showName")
                if (showName == true) {
                    if (inputDevName?.isNotEmpty() == true && strArray[0].toLowerCase().contains(
                            inputDevName.toLowerCase()
                        )
                    ) {
                        deviceList.add(device)
                        deviceAdapter?.setList(deviceList)
                    }
                } else {
                    deviceList.add(device)
                    deviceAdapter?.setList(deviceList)
                }
            }
            scanFinish -> {
                swipeRefreshLayout.isRefreshing = false
                dialog?.dismiss()
                //deviceAdapter?.setList(deviceList)
                mListener?.updateStatus(data)
                if (Sp.instance?.getBoolean("autoScan") == true) {
                    BLE.connect()
                }
            }
            connectSuccess -> {
                dialog?.dismiss()
                mListener?.updateStatus("$data:$targetDeviceMac")
            }
            connectFail,
            disconnect,
            retryConnect,
            connectTimeout -> {
                dialog?.dismiss()
                mListener?.updateStatus("$data:$targetDeviceMac")
            }
        }
    }

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
}