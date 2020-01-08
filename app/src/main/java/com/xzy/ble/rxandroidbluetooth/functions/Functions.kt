package com.xzy.ble.rxandroidbluetooth.functions

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.Dimension
import com.xzy.ble.rxandroidbluetooth.RootApp
import com.xzy.ble.rxandroidbluetooth.ble.Device
import java.io.UnsupportedEncodingException
import java.lang.StringBuilder
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.xor


/**
 * Author: xzy
 */
@Suppress("unused")
class Functions

fun hideStatusBar(window: Window) {
    val attrs: WindowManager.LayoutParams = window.attributes
    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
    window.attributes = attrs
}

@Suppress("unused")
fun showStatusBar(window: Window) {
    val attrs: WindowManager.LayoutParams = window.attributes
    attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
    window.attributes = attrs
}

@Suppress("unused")
fun removeRepeatListItem(list: MutableList<Device>): MutableList<Device>? {
    // 从list中索引为0开始往后遍历
    for (i in 0 until list.size - 1) {
        for (j in list.size - 1 downTo i + 1) {
            if (list[j].mac == list[i].mac) {
                list.removeAt(j)
            }
        }
    }
    return list
}

/**
 * 用于建立十六进制字符的输出的小写字符数组
 */
private val DIGITS_LOWER =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

/**
 * 用于建立十六进制字符的输出的大写字符数组
 */
private val DIGITS_UPPER =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

/**
 * 将字节数组转换为十六进制字符数组
 *
 * @param data     byte[]
 * @param toDigits 用于控制输出的char[]
 * @return 十六进制char[]
 */
private fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
    val l = data.size
    val out = CharArray(l shl 1)
    // two characters form the hex value.
    var i = 0
    var j = 0
    while (i < l) {
        out[j++] = toDigits[(0xF0 and data[i].toInt()).ushr(4)]
        out[j++] = toDigits[0x0F and data[i].toInt()]
        i++
    }
    return out
}

/**
 * 将字节数组转换为十六进制字符串
 *
 * @param data        byte[]
 * @param toLowerCase `true` 传换成小写格式 ， `false` 传换成大写格式
 * @return 十六进制String
 */
@JvmOverloads
@Suppress("unused")
fun encodeHexStr(data: ByteArray, toLowerCase: Boolean = true): String {
    return encodeHexStr(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
}

/**
 * 将字节数组转换为十六进制字符串
 *
 * @param data     byte[]
 * @param toDigits 用于控制输出的char[]
 * @return 十六进制String
 */
private fun encodeHexStr(data: ByteArray?, toDigits: CharArray): String {
    if (data == null) {
        Log.e(TAG, "this data is null.")
        return ""
    }
    return String(encodeHex(data, toDigits))
}

/**
 * 将十六进制字符串转换为字节数组
 *
 * @param data 16 进制字符串
 * @return 16 进制字节数组
 */
@Suppress("unused")
fun decodeHex(data: String?): ByteArray {
    if (data == null) {
        Log.e(TAG, "this data is null.")
        return ByteArray(0)
    }
    return decodeHex(data.toCharArray())
}

/**
 * 将十六进制字符数组转换为字节数组
 *
 * @param data 十六进制char[]
 * @return byte[]
 */
private fun decodeHex(data: CharArray): ByteArray {

    val len = data.size

    if (len and 0x01 != 0) {
        //如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
        throw RuntimeException("Odd number of characters.")
    }

    val out = ByteArray(len shr 1)

    // two characters form the hex value.
    var i = 0
    var j = 0
    while (j < len) {
        var f = toDigit(data[j], j) shl 4
        j++
        f = f or toDigit(data[j], j)
        j++
        out[i] = (f and 0xFF).toByte()
        i++
    }

    return out
}

/**
 * 将十六进制字符转换成一个整数
 *
 * @param ch    十六进制char
 * @param index 十六进制字符在字符数组中的位置
 * @return 一个整数
 */
private fun toDigit(ch: Char, index: Int): Int {
    val digit = Character.digit(ch, 16)
    if (digit == -1) {
        // 当ch不是一个合法的十六进制字符时，抛出运行时异常
        throw RuntimeException("Illegal hexadecimal character $ch at index $index")
    }
    return digit
}

/**
 * 截取字节数组
 *
 * @param src   byte []  数组源  这里填16进制的 数组
 * @param begin 起始位置 源数组的起始位置。0位置有效
 * @param count 截取长度
 * @return byte[]
 */
@Suppress("unused")
fun subBytes(src: ByteArray, begin: Int, count: Int): ByteArray {
    val bs = ByteArray(count)
    System.arraycopy(src, begin, bs, 0, count)  // bs 目的数组  0 截取后存放的数值起始位置。0位置有效
    return bs
}

/**
 * int转byte数组
 *
 * @param bb 数组
 * @param x 需要转换的整数
 * @param index 第几位开始
 * @param flag 标识高低位顺序，高位在前为true，低位在前为false
 */
@Suppress("unused")
fun intToByte(bb: ByteArray, x: Int, index: Int, flag: Boolean) {
    if (flag) {
        bb[index] = (x shr 24).toByte()
        bb[index + 1] = (x shr 16).toByte()
        bb[index + 2] = (x shr 8).toByte()
        bb[index + 3] = x.toByte()
    } else {
        bb[index + 3] = (x shr 24).toByte()
        bb[index + 2] = (x shr 16).toByte()
        bb[index + 1] = (x shr 8).toByte()
        bb[index] = x.toByte()
    }
}

/**
 * byte数组转int
 *
 * @param bb 数组
 * @param index 第几位开始
 * @param flag 标识高低位顺序，高位在前为true，低位在前为false
 * @return int
 */
@Suppress("unused")
fun byteToInt(bb: ByteArray, index: Int, flag: Boolean): Int {
    return if (flag) {
        ((bb[index] and 0xff.toByte()).toInt() shl 24
                or ((bb[index + 1] and 0xff.toByte()).toInt() shl 16)
                or ((bb[index + 2] and 0xff.toByte()).toInt() shl 8)
                or (bb[index + 3] and 0xff.toByte()).toInt())
    } else {
        ((bb[index + 3] and 0xff.toByte()).toInt() shl 24
                or ((bb[index + 2] and 0xff.toByte()).toInt() shl 16)
                or ((bb[index + 1] and 0xff.toByte()).toInt() shl 8)
                or ((bb[index] and 0xff.toByte())).toInt())
    }
}


/**
 * 字节数组逆序
 *
 * @param data byte[]
 * @return byte[]
 */
@Suppress("unused")
fun reverse(data: ByteArray): ByteArray {
    val reverseData = ByteArray(data.size)
    for (i in data.indices) {
        reverseData[i] = data[data.size - 1 - i]
    }
    return reverseData
}

/**
 * 蓝牙传输 16进制 高低位 读数的 转换
 *
 * @param data 截取数据源，字节数组
 * @param index 截取数据开始位置
 * @param count 截取数据长度，只能为2、4、8个字节
 * @param flag 标识高低位顺序，高位在前为true，低位在前为false
 * @return long
 */
@Suppress("unused")
fun byteToLong(data: ByteArray, index: Int, count: Int, flag: Boolean): Long {
    var lg: Long = 0
    if (flag) {
        when (count) {
            2 -> lg = data[index].toLong() and 0xff shl 8 or (data[index + 1].toLong() and 0xff)

            4 -> lg = (data[index].toLong() and 0xff shl 24
                    or (data[index + 1].toLong() and 0xff shl 16)
                    or (data[index + 2].toLong() and 0xff shl 8)
                    or (data[index + 3].toLong() and 0xff))

            8 -> lg = (data[index].toLong() and 0xff shl 56
                    or (data[index + 1].toLong() and 0xff shl 48)
                    or (data[index + 2].toLong() and 0xff shl 40)
                    or (data[index + 3].toLong() and 0xff shl 32)
                    or (data[index + 4].toLong() and 0xff shl 24)
                    or (data[index + 5].toLong() and 0xff shl 16)
                    or (data[index + 6].toLong() and 0xff shl 8)
                    or (data[index + 7].toLong() and 0xff))
        }
        return lg
    } else {
        when (count) {
            2 -> lg = data[index + 1].toLong() and 0xff shl 8 or (data[index].toLong() and 0xff)
            4 -> lg = (data[index + 3].toLong() and 0xff shl 24
                    or (data[index + 2].toLong() and 0xff shl 16)
                    or (data[index + 1].toLong() and 0xff shl 8)
                    or (data[index].toLong() and 0xff))
            8 -> lg = (data[index + 7].toLong() and 0xff shl 56
                    or (data[index + 6].toLong() and 0xff shl 48)
                    or (data[index + 5].toLong() and 0xff shl 40)
                    or (data[index + 4].toLong() and 0xff shl 32)
                    or (data[index + 3].toLong() and 0xff shl 24)
                    or (data[index + 2].toLong() and 0xff shl 16)
                    or (data[index + 1].toLong() and 0xff shl 8)
                    or (data[index].toLong() and 0xff))
        }
        return lg
    }
}


/**
 * 执行异或操作
 * @param bytes 需要操作的数组
 * @return byte
 */
@Suppress("unused")
fun xOrVerify(bytes: ByteArray): Byte {
    var xOrValue: Byte = 0x00
    var i: Byte = 0x00
    while (i < bytes.size) {
        xOrValue = (bytes[i.toInt()] xor xOrValue)
        i++
    }
    return xOrValue
}

/**
 * 十六进制字符串转 10 进制整数
 *
 * @param hexString 十六进制字符串转
 * @return 10 进制整数
 */
fun hexStringToInt(hexString: String?): Int {
    val s = Integer.valueOf(hexString!!, 16).toString()
    return s.toInt()
}

/**
 * 设置EditText的hint字体大小
 *
 * @param editText EditText控件
 * @param hintText hint内容
 * @param size     hint字体大小，单位为sp
 */
fun setEditTextHintWithSize(editText: EditText, hintText: String?, @Dimension size: Int) {
    if (!TextUtils.isEmpty(hintText)) {
        val ss = SpannableString(hintText)
        //设置字体大小 true表示单位是sp
        val ass = AbsoluteSizeSpan(size, true)
        ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        editText.hint = SpannedString(ss)
    }
}

fun toUtf8(str: String): String? {
    var result: String? = null
    try {
        result = String(str.toByteArray(charset("utf-8")))
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return result
}

/**
 * 整数转二进制字符串 -- 自动补齐 8 位
 */
fun toBinary(numValue: Int): String? {
    var num = numValue
    var str = StringBuilder()
    while (num != 0) {
        str.insert(0, num % 2)
        num /= 2
    }
    val temp = StringBuilder()
    if (str.length < 8) {
        for (i in 0 until 8 - str.length) {
            temp.append("0")
        }
        str = StringBuilder((temp.toString() + str.toString()).trim { it <= ' ' })
    }
    return str.toString()
}


/**
 * 16进制字符串转字节数组
 * **/
@SuppressLint("DefaultLocale")
@Suppress("unused")
fun hexString2Bytes(hexString: String?): ByteArray? {
    var hex = hexString
    return if (hex == null || hex == "") {
        null
    } else if (hex.length % 2 != 0) {
        null
    } else {
        hex = hex.toUpperCase()
        val len = hex.length / 2
        val b = ByteArray(len)
        val hc = hex.toCharArray()
        for (i in 0 until len) {
            val p = 2 * i
            b[i] =
                (charToByte(hc[p]).toInt().shl(4) or charToByte(hc[p + 1]).toInt()).toByte()
        }
        b
    }
}

/**
 * 字符转换为字节
 * */
private fun charToByte(c: Char): Byte {
    return "0123456789ABCDEF".indexOf(c).toByte()
}

@Suppress("unused")
class Sp {
    private var sp: SharedPreferences

    private constructor(spName: String) {
        sp = RootApp.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE)
    }

    private constructor(spName: String, mode: Int) {
        sp = RootApp.getContext().getSharedPreferences(spName, mode)
    }

    @SuppressLint("ApplySharedPref")
    @JvmOverloads
    fun put(
        key: String,
        value: String?,
        isCommit: Boolean = false
    ) {
        if (isCommit) {
            sp.edit().putString(key, value).commit()
        } else {
            sp.edit().putString(key, value).apply()
        }
    }

    /**
     * Return the string value in sp.
     *
     * @param key The key of sp.
     * @return the string value if sp exists or `""` otherwise
     */
    fun getString(key: String): String? {
        return getString(key, "")
    }

    /**
     * Return the string value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the string value if sp exists or `defaultValue` otherwise
     */
    fun getString(key: String, defaultValue: String?): String? {
        return sp.getString(key, defaultValue)
    }

    @SuppressLint("ApplySharedPref")
    @JvmOverloads
    fun put(key: String, value: Int, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putInt(key, value).commit()
        } else {
            sp.edit().putInt(key, value).apply()
        }
    }

    /**
     * Return the int value in sp.
     *
     * @param key The key of sp.
     * @return the int value if sp exists or `-1` otherwise
     */
    fun getInt(key: String): Int {
        return getInt(key, -1)
    }

    /**
     * Return the int value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the int value if sp exists or `defaultValue` otherwise
     */
    fun getInt(key: String, defaultValue: Int): Int {
        return sp.getInt(key, defaultValue)
    }

    @SuppressLint("ApplySharedPref")
    @JvmOverloads
    fun put(key: String, value: Long, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putLong(key, value).commit()
        } else {
            sp.edit().putLong(key, value).apply()
        }
    }

    /**
     * Put the boolean value in sp.
     *
     * @param key   The key of sp.
     * @param value The value of sp.
     */
    @SuppressLint("ApplySharedPref")
    @JvmOverloads
    fun put(
        key: String,
        value: Boolean,
        isCommit: Boolean = false
    ) {
        if (isCommit) {
            sp.edit().putBoolean(key, value).commit()
        } else {
            sp.edit().putBoolean(key, value).apply()
        }
    }

    /**
     * Return the boolean value in sp.
     *
     * @param key The key of sp.
     * @return the boolean value if sp exists or `false` otherwise
     */
    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    /**
     * Return the boolean value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the boolean value if sp exists or `defaultValue` otherwise
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    @SuppressLint("ApplySharedPref")
    @JvmOverloads
    fun put(
        key: String,
        value: Set<String?>?,
        isCommit: Boolean = false
    ) {
        if (isCommit) {
            sp.edit().putStringSet(key, value).commit()
        } else {
            sp.edit().putStringSet(key, value).apply()
        }
    }

    /**
     * Return the set of string value in sp.
     *
     * @param key The key of sp.
     * @return the set of string value if sp exists
     * or `Collections.<String>emptySet()` otherwise
     */
    fun getStringSet(key: String): Set<String>? {
        return getStringSet(key, emptySet<String>())
    }

    /**
     * Return the set of string value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the set of string value if sp exists or `defaultValue` otherwise
     */
    fun getStringSet(
        key: String,
        defaultValue: Set<String?>?
    ): Set<String>? {
        return sp.getStringSet(key, defaultValue)
    }

    /**
     * Return all values in sp.
     *
     * @return all values in sp
     */
    val all: Map<String, *>
        get() = sp.all

    /**
     * Return whether the sp contains the preference.
     *
     * @param key The key of sp.
     * @return `true`: yes<br></br>`false`: no
     */
    operator fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    @SuppressLint("ApplySharedPref")
    @JvmOverloads
    fun remove(key: String, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().remove(key).commit()
        } else {
            sp.edit().remove(key).apply()
        }
    }

    @SuppressLint("ApplySharedPref")
    @JvmOverloads
    fun clear(isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().clear().commit()
        } else {
            sp.edit().clear().apply()
        }
    }

    companion object {
        private val SP_UTILS_MAP: MutableMap<String, Sp> =
            HashMap()

        val instance: Sp?
            get() = getInstance("", Context.MODE_PRIVATE)

        fun getInstance(mode: Int): Sp? {
            return getInstance("", mode)
        }

        fun getInstance(spName: String): Sp? {
            return getInstance(spName, Context.MODE_PRIVATE)
        }

        private fun getInstance(name: String, mode: Int): Sp? {
            var spName = name
            if (isSpace(spName)) {
                spName = "spUtils"
            }
            var spUtils = SP_UTILS_MAP[spName]
            if (spUtils == null) {
                synchronized(Sp::class.java) {
                    spUtils = SP_UTILS_MAP[spName]
                    if (spUtils == null) {
                        spUtils = Sp(spName, mode)
                        SP_UTILS_MAP[spName] = spUtils!!
                    }
                }
            }
            return spUtils
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}