/*
 * Copyright 2015-2019 Hive Box.
 */

package com.xzy.ble.rxandroidbluetooth.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.xzy.ble.rxandroidbluetooth.ble.Device
import com.xzy.ble.rxandroidbluetooth.functions.removeRepeatListItem
import com.xzy.ble.rxandroidbluetooth.functions.toUtf8
import com.xzy.ble.rxandroidbluetooth.R
import java.util.*

@Suppress("unused")
class DeviceListAdapter(context: Context) : BaseAdapter() {

    private var deviceList: MutableList<Device>? = ArrayList()
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    fun setList(list: MutableList<Device>) {
        deviceList = removeRepeatListItem(list)
        notifyDataSetChanged()
    }

    fun getList(): MutableList<Device>? {

        return deviceList
    }

    override fun getCount(): Int {
        deviceList?.let {
            return it.size
        }
        return 0
    }

    override fun getItem(i: Int): Any? {
        return if (count == 0) null else deviceList?.get(i)
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("StringFormatMatches", "SetTextI18n")
    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
        var customView = view
        val viewHolder: ViewHolder
        if (customView == null) {
            customView =
                layoutInflater.inflate(R.layout.fragment_device_list_item, viewGroup, false)
            viewHolder = ViewHolder()
            viewHolder.tvName = customView?.findViewById(R.id.devName)
            viewHolder.tvMac = customView?.findViewById(R.id.devMac)
            customView.tag = viewHolder
        } else {
            viewHolder = customView.tag as ViewHolder
        }
        deviceList?.let {
            if (count > 0) {
                val device = it[i]
                viewHolder.tvName?.text = toUtf8(device.name)
                viewHolder.tvMac?.text = device.mac
            }
        }
        return customView
    }


    internal class ViewHolder {
        var tvName: TextView? = null
        var tvMac: TextView? = null
    }
}
