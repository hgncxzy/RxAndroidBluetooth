/*
 * Copyright 2015-2019 Hive Box.
 */

package com.xzy.ble.rxandroidbluetooth

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.xzy.ble.rxandroidbluetooth.ble.BLE

@Suppress("unused")
class RootApp : MultiDexApplication() {
    val tag = "RootApp"
    override fun onCreate() {
        super.onCreate()
        rootApp = this
        BLE.initBluetoothController()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.e(tag, "onTrimMemory, level = $level")
    }

    companion object {
        var rootApp: Application? = null
        fun getContext(): Context {
            return rootApp?.applicationContext!!
        }
    }
}