package com.xzy.ble.rxandroidbluetooth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.xzy.ble.rxandroidbluetooth.functions.Sp
import com.xzy.ble.rxandroidbluetooth.functions.setEditTextHintWithSize
import com.xzy.ble.rxandroidbluetooth.widget.LSettingItem
import com.xzy.ble.rxandroidbluetooth.R
import scut.carson_ho.diy_view.SuperEditText

/**
 * @author 002034
 */
@Suppress("DEPRECATION")
class SettingFragment : Fragment() {
    val myTag = "SettingFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.fragment_setting, container, false)
        initView(mView)
        return mView
    }

    private fun initView(mView: View) {
        val autoScan = mView.findViewById<LSettingItem>(R.id.item_one)
        val autoConnect = mView.findViewById<LSettingItem>(R.id.item_two)
        val showName = mView.findViewById<LSettingItem>(R.id.item_three)

        if (Sp.instance?.getBoolean("autoScan") == true) {
            autoScan.clickOn()
        }
        if (Sp.instance?.getBoolean("autoConnect") == true) {
            autoConnect.clickOn()
        }
        if (Sp.instance?.getBoolean("showName") == true) {
            showName.clickOn()
        }

        autoScan.setmOnLSettingItemClick(object : LSettingItem.OnLSettingItemClick {
            override fun click(isChecked: Boolean) {
                Sp.instance?.put("autoScan", isChecked, false)
                Log.d(myTag, "autoScan:${isChecked}")
            }
        })

        autoConnect.setmOnLSettingItemClick(object : LSettingItem.OnLSettingItemClick {
            override fun click(isChecked: Boolean) {
                Sp.instance?.put("autoConnect", isChecked, false)
                Log.d(myTag, "autoConnect:${isChecked}")
            }
        })

        showName.setmOnLSettingItemClick(object : LSettingItem.OnLSettingItemClick {
            override fun click(isChecked: Boolean) {
                Sp.instance?.put("showName", isChecked, false)
                Log.d(myTag, "showName:${isChecked}")
            }
        })

        val inputDevName = mView.findViewById<SuperEditText>(R.id.input_dev_name)
        inputDevName.setText(Sp.instance?.getString("inputDevName"))
        setEditTextHintWithSize(inputDevName, "Enter device name", 14)
        inputDevName.addTextChangedListener {
            Sp.instance?.put("inputDevName", inputDevName.text.toString().trim(), false)
        }
    }
}