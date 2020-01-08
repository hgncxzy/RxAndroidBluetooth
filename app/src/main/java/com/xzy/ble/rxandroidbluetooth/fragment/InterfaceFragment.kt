package com.xzy.ble.rxandroidbluetooth.fragment

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xzy.ble.rxandroidbluetooth.R
import com.xzy.ble.rxandroidbluetooth.ble.BLE
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.notifyChanged
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.writeFail
import com.xzy.ble.rxandroidbluetooth.ble.BLE.Actions.writeSuccess
import com.xzy.ble.rxandroidbluetooth.ble.MyReceiver
import com.xzy.ble.rxandroidbluetooth.functions.decodeHex
import kotlinx.android.synthetic.main.fragment_interface.*


/**
 * @author 002034
 */
class InterfaceFragment : Fragment(), MyReceiver.Receiver {

    private val myReceiver = MyReceiver(this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_interface, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerMyReceiver()
        btn.setOnClickListener {
            val text = edit_et.text.toString().trim()
            BLE.write(decodeHex(text))
        }
    }


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

    override fun onDestroy() {
        super.onDestroy()
        unRegisterMyReceiver()
    }
}