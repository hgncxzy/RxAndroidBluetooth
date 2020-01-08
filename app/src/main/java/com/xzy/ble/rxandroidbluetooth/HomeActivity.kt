package com.xzy.ble.rxandroidbluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.xzy.ble.rxandroidbluetooth.ble.BLE
import com.xzy.ble.rxandroidbluetooth.ble.BLE.bluetoothController
import com.xzy.ble.rxandroidbluetooth.ble.BLE.enableBTCode
import com.xzy.ble.rxandroidbluetooth.ble.BLE.logTag
import com.xzy.rxandroidbluetooth.fragment.DeviceFragment
import com.xzy.ble.rxandroidbluetooth.fragment.InterfaceFragment
import com.xzy.ble.rxandroidbluetooth.fragment.SettingFragment
import com.xzy.ble.rxandroidbluetooth.functions.showStatusBar
import com.xzy.ble.rxandroidbluetooth.widget.XToast
import com.next.easynavigation.constant.Anim
import com.next.easynavigation.view.EasyNavigationBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity(), DeviceFragment.MyListener {
    private var exitTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showStatusBar(window)
        setContentView(R.layout.activity_main)
        checkPermissionAndBleEnable()
        initBottomNav()
    }

    private fun initBottomNav() {
        val tabText = arrayOf("Device", "Interface", "Setting")
        val normalIcon = intArrayOf(
            R.mipmap.device_normal,
            R.mipmap.interface_normal,
            R.mipmap.setting_normal
        )
        val selectIcon = intArrayOf(
            R.mipmap.device_selected,
            R.mipmap.interface_selected,
            R.mipmap.setting_selected
        )
        val fragments: MutableList<Fragment> = ArrayList()
        fragments.add(DeviceFragment())
        fragments.add(InterfaceFragment())
        fragments.add(SettingFragment())
        navigationBar.titleItems(tabText)
            .normalIconItems(normalIcon)
            .selectIconItems(selectIcon)
            .fragmentList(fragments)
            .fragmentManager(supportFragmentManager)
            .iconSize(30)
            .tabTextSize(15)
            .tabTextTop(2)
            .normalTextColor(Color.parseColor("#666666"))
            .selectTextColor(Color.parseColor("#333333"))
            .scaleType(ImageView.ScaleType.CENTER_INSIDE)
            .smoothScroll(true)
            .canScroll(true)
            .anim(Anim.FadeIn)
            .navigationHeight(60)
            .lineColor(Color.parseColor("#cccccc"))
            .addLayoutRule(EasyNavigationBar.RULE_BOTTOM)
            .addLayoutBottom(10)
            .hasPadding(true)
            .onTabClickListener { _, _ ->
                //Tab点击事件 return true 页面不会切换
                false
            }
            .build()
    }

    @SuppressLint("SetTextI18n")
    private fun checkPermissionAndBleEnable() {
        if (Build.VERSION.SDK_INT >= 6.0) {
            BLE.requestPermission(this)
        }
        if (!bluetoothController!!.isEnabled()) {
            bluetoothController!!.enable()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == BLE.permissionReqCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                XToast.showToast(this, "Location permission obtained")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == enableBTCode) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(logTag, "[HomeActivity]: Has been open ble")
            } else {
                XToast.showToastWithAnimAndImg(this, "Did not open the ble", R.mipmap.hc_warn)
            }
        }
    }

    /**
     * 两秒内按返回键两次退出程序
     *
     * @param keyCode keyCode
     * @param event   event
     * @return boolean
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                XToast.showToast(this, "Press again to exit the program")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                exitProcess(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun updateStatus(str: String) {
        status.leftTextView.text = str
    }
}
