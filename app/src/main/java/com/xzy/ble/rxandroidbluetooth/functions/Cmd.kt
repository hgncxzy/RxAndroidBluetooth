package com.xzy.ble.rxandroidbluetooth.functions

import android.util.Log
import java.text.DecimalFormat
import kotlin.experimental.xor

/**
 * @author 002034
 */
@Suppress("unused")
object Cmd {
    /**
     * 温度
     */
    const val TEMPERATURE: Byte = 0x7A.toByte()
    /**
     * 电压
     */
    const val VOLTAGE: Byte = 0x7B
    /**
     * 电流
     */
    const val CURRENT: Byte = 0x7C
    /**
     * 相对电量 relative_power
     */
    const val RELATIVE_POWER: Byte = 0x7D
    /**
     * 绝对电量 absolute_power
     */
    const val ABSOLUTE_POWER: Byte = 0x7E

    /**
     * 开某个格口
     * */
    const val OPEN_CELL: Byte = 0x90.toByte()

    /**
     * 查询某个格口锁状态
     * */
    const val READ_CELL_INFO: Byte = 0x96.toByte()
    /**
     * 开所有格口
     * */
    const val OPEN_ALL_CELLS: Byte = 0x91.toByte()

    /**
     * 查询所有格口锁状态
     * */
    const val READ_ALL_CELLS_INFO: Byte = 0x97.toByte()

    /**
     * 开面板锁
     * */
    const val OPEN_SCREEN_LOCKER: Byte = 0x92.toByte()

    /**
     * 查询面板锁
     * */
    const val READ_SCREEN_LOCKER_INFO: Byte = 0x93.toByte()

    /**
     * 单柜序号
     * */
    private val CABINET_CODES = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06)

    private fun getCabinetNum(cabinetCode: String): Byte {
        var bytes: Byte = 0x00
        when (cabinetCode) {
            "Z" -> bytes = 0x00
            "A" -> bytes = 0x01
            "B" -> bytes = 0x02
            "C" -> bytes = 0x03
            "D" -> bytes = 0x04
            "E" -> bytes = 0x05
            "F" -> bytes = 0x06
        }
        return bytes
    }

    /**
     * 开门序号
     */
    private val CELL_NOS = byteArrayOf(
        0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
        0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a
    )


    /**
     * 开某个格口
     * @param cabinetCode 单柜别名
     * @param cellIndex 格口索引
     * @return byte[]
     */
    fun openCellCmd(cabinetCode: String, cellIndex: Int): ByteArray {
        //val pos = getPos(alias)
        // ４２　４８　９０　０２　００　０１　９９
        val cabinetCodeByte = getCabinetNum(cabinetCode)
        Log.d("xzy", "cabinetCode:$cabinetCode,cabinetCodeByte=$cabinetCodeByte")
        val temp = byteArrayOf(
            0x48,
            0x42,
            OPEN_CELL,
            0x02,
            cabinetCodeByte,
            CELL_NOS[cellIndex - 1]
        )
        return byteArrayOf(
            0x48,
            0x42,
            OPEN_CELL,
            0x02,
            cabinetCodeByte,
            CELL_NOS[cellIndex - 1],
            xOrVerify(temp)
        )
    }

    /**
     * 打开所有格口
     * @param cabinetCode 单柜别名
     * @return byte[]
     */
    fun openAllCells(cabinetCode: String): ByteArray {
        val cabinetCodeByte = getCabinetNum(cabinetCode)
        val temp = byteArrayOf(
            0x48,
            0x42,
            OPEN_ALL_CELLS,
            0x01,
            cabinetCodeByte
        )
        return byteArrayOf(
            0x48,
            0x42,
            OPEN_ALL_CELLS,
            0x01,
            cabinetCodeByte,
            xOrVerify(temp)
        )
    }

    /**
     * 开面板锁
     * */
    fun openScreenLocker(cabinetCode: String): ByteArray {
        val cabinetCodeByte = getCabinetNum(cabinetCode)
        val temp = byteArrayOf(
            0x48,
            0x42,
            OPEN_SCREEN_LOCKER,
            0x01,
            cabinetCodeByte
        )
        return byteArrayOf(
            0x48,
            0x42,
            OPEN_SCREEN_LOCKER,
            0x01,
            cabinetCodeByte,
            xOrVerify(temp)
        )
    }

    /**
     * 查询面板锁状态
     * */
    fun readScreenLockerInfo(cabinetCode: String): ByteArray {
        val cabinetCodeByte = getCabinetNum(cabinetCode)
        val temp = byteArrayOf(
            0x48,
            0x42,
            READ_SCREEN_LOCKER_INFO,
            0x01,
            cabinetCodeByte
        )
        return byteArrayOf(
            0x48,
            0x42,
            READ_SCREEN_LOCKER_INFO,
            0x01,
            cabinetCodeByte,
            xOrVerify(temp)
        )
    }

    /**
     * 读取某个格口信息
     * */
    fun readCellInfo(cabinetCode: String, cellIndex: Int): ByteArray {
        val cabinetCodeByte = getCabinetNum(cabinetCode)
        val temp = byteArrayOf(
            0x48,
            0x42,
            READ_CELL_INFO,
            0x02,
            cabinetCodeByte,
            CELL_NOS[cellIndex - 1]
        )
        return byteArrayOf(
            0x48,
            0x42,
            READ_CELL_INFO,
            0x02,
            cabinetCodeByte,
            CELL_NOS[cellIndex - 1],
            xOrVerify(temp)
        )
    }

    /**
     * 查询所有格口的信息
     * */
    fun readAllCellsInfo(cabinetCode: String): ByteArray {
        val cabinetCodeByte = getCabinetNum(cabinetCode)
        val temp = byteArrayOf(
            0x48,
            0x42,
            READ_ALL_CELLS_INFO,
            0x01,
            cabinetCodeByte
        )
        return byteArrayOf(
            0x48,
            0x42,
            READ_ALL_CELLS_INFO,
            0x01,
            cabinetCodeByte,
            xOrVerify(temp)
        )
    }


    /**
     * @param alias 别名
     * @return byte[]
     */
    fun queryLockerStatusCmd(alias: String): ByteArray {
        val pos = getPos(alias)
        val temp = byteArrayOf(
            0x48,
            0x42,
            0x96.toByte(),
            0x02,
            CELL_NOS[0],
            CABINET_CODES[0]
        )
        return byteArrayOf(
            0x48,
            0x42,
            0x96.toByte(),
            0x02,
            CELL_NOS[0],
            CABINET_CODES[0],
            xOrVerify(temp)
        )
    }

    /**
     * 查温度
     * @return byte[]
     */
    fun queryTemperatureCmd(): ByteArray {
        val temp = byteArrayOf(0x48, 0x42, TEMPERATURE, 0x01, CABINET_CODES[0])
        return byteArrayOf(0x48, 0x42, TEMPERATURE, 0x01, CABINET_CODES[0], xOrVerify(temp))
    }

    /**
     * 查电压
     * @return byte[]
     */
    fun queryVoltageCmd(): ByteArray {
        val temp = byteArrayOf(0x48, 0x42, VOLTAGE, 0x01, CABINET_CODES[0])
        return byteArrayOf(0x48, 0x42, VOLTAGE, 0x01, CABINET_CODES[0], xOrVerify(temp))
    }

    /**
     * 查电流
     * @return byte[]
     */
    fun queryCurrentCmd(): ByteArray {
        val temp = byteArrayOf(0x48, 0x42, CURRENT, 0x01, CABINET_CODES[0])
        return byteArrayOf(0x48, 0x42, CURRENT, 0x01, CABINET_CODES[0], xOrVerify(temp))
    }

    /**
     * 查相对电量
     * @return byte[]
     */
    fun queryRelativePowerCmd(): ByteArray {
        val temp = byteArrayOf(0x48, 0x42, RELATIVE_POWER, 0x01, CABINET_CODES[0])
        return byteArrayOf(0x48, 0x42, RELATIVE_POWER, 0x01, CABINET_CODES[0], xOrVerify(temp))
    }

    /**
     * 查绝对电量
     * @return byte[]
     */
    fun queryAbsolutePowerCmd(): ByteArray {
        val temp = byteArrayOf(0x48, 0x42, ABSOLUTE_POWER, 0x01, CABINET_CODES[0])
        return byteArrayOf(0x48, 0x42, ABSOLUTE_POWER, 0x01, CABINET_CODES[0], xOrVerify(temp))
    }

    private fun getPos(alias: String): Int {
        var index = alias.substring(1)
        if (index.startsWith("0")) {
            index = index.replace("0", "")
        }
        return index.toInt() - 1
    }

    private fun xOrVerify(bytes: ByteArray): Byte {
        var xOrValue: Byte = 0x00
        var i: Byte = 0x00
        while (i < bytes.size) {
            xOrValue = (bytes[i.toInt()] xor xOrValue)
            i++
        }
        return xOrValue
    }

    fun handleHexData(data: ByteArray): String {
        var result = "Analyse error."
        val cmd = data[2]
        val dataLength = data[3].toInt()
        when (cmd) {
            /**
             * 温度
             */
            TEMPERATURE -> {
                val bytes = ByteArray(dataLength) { 0 }
                for (index in bytes.indices) {
                    bytes[index] = data[4 + index]
                }
                // 转换 开氏温标 到 摄氏度
                //℃ =K - 273.15
                val temperature =
                    DecimalFormat("#.00")
                        .format(hexStringToInt(encodeHexStr(bytes)) * 0.1f - 273.15)
                result = "$temperature °C"
            }
            /**
             * 电压
             */
            VOLTAGE -> {
                val bytes = ByteArray(dataLength) { 0 }
                for (index in bytes.indices) {
                    bytes[index] = data[4 + index]
                }
                val voltage = hexStringToInt(encodeHexStr(bytes))
                result = "$voltage mV"
            }
            /**
             * 电流
             */
            CURRENT -> {
                val bytes = ByteArray(dataLength) { 0 }
                for (index in bytes.indices) {
                    bytes[index] = data[4 + index]
                }
                val current = hexStringToInt(encodeHexStr(bytes))
                result = "$current ma"
            }
            /**
             * 相对电量 relative_power
             */
            RELATIVE_POWER -> {
                val bytes = ByteArray(dataLength) { 0 }
                for (index in bytes.indices) {
                    bytes[index] = data[4 + index]
                }
                val relativePower = hexStringToInt(encodeHexStr(bytes))
                result = "$relativePower%"
            }
            /**
             * 绝对电量 absolute_power
             */
            ABSOLUTE_POWER -> {
                val bytes = ByteArray(dataLength) { 0 }
                for (index in bytes.indices) {
                    bytes[index] = data[4 + index]
                }
                val absolutePower = hexStringToInt(encodeHexStr(bytes))
                result = "$absolutePower%"
            }

            /**
             * 开某个格口
             * */
            OPEN_CELL -> {
                val cabinetCode = hexStringToInt(encodeHexStr(byteArrayOf(data[4])))
                val cellIndex = hexStringToInt(encodeHexStr(byteArrayOf(data[5])))
                val openCell = hexStringToInt(encodeHexStr(byteArrayOf(data[6])))
                result = if (openCell == 0) {
                    " Locker serial number:$cabinetCode,\n Index of cells:$cellIndex ,\n Status: Open success"
                } else {
                    " Locker serial number:$cabinetCode,\n Index of cells:$cellIndex ,\n Status: Open failed"
                }
            }

            /**
             * 查询某个格口锁状态
             * */
            READ_CELL_INFO -> {
                val cabinetCode = hexStringToInt(encodeHexStr(byteArrayOf(data[4])))
                val cellIndex = hexStringToInt(encodeHexStr(byteArrayOf(data[5])))
                val lockerStatus = hexStringToInt(encodeHexStr(byteArrayOf(data[6])))
                result = if (lockerStatus == 0) {
                    " Locker serial number:$cabinetCode,\n Index of cells:$cellIndex ,\n Status: Locker is closed."
                } else {
                    " Locker serial number:$cabinetCode,\n Index of cells:$cellIndex ,\n Status: Locker is open."
                }
            }
            /**
             * 开所有格口
             * */
            OPEN_ALL_CELLS -> {
                val cabinetCode = hexStringToInt(encodeHexStr(byteArrayOf(data[4])))
                val openCell = hexStringToInt(encodeHexStr(byteArrayOf(data[5])))
                result = if (openCell == 0) {
                    " Locker serial number:$cabinetCode ,\n Status: Open success"
                } else {
                    " Locker serial number:$cabinetCode,\n  Status: Open failed."
                }
            }

            /**
             * 查询所有格口锁状态
             * */
            READ_ALL_CELLS_INFO -> {
                val bytes = ByteArray(dataLength) { 0 }
                for (index in bytes.indices) {
                    bytes[index] = data[4 + index]
                }
                val cabinetCode = bytes[0].toString()
                result = " Locker serial number: $cabinetCode , \n All cells status ：\n"
                val tempStr = StringBuffer("")
                for (index in bytes.indices) {
                    if (index == 0) { // 箱号
                        continue
                    }
                    val value = bytes[index].toInt()
                    tempStr.append(toBinary(value))
                }
                val charArray = tempStr.reverse().toString().toCharArray()
                for (i in charArray.indices) {
                    if (charArray[i] == '1') {
                        result += " cellIndex：${i + 1},Status：Open\n"
                    } else if (charArray[i] == '0') {
                        result += " cellIndex：${i + 1},Status：Closed\n"
                    }
                }
            }

            /**
             * 开面板锁
             * */
            OPEN_SCREEN_LOCKER -> {
                val openCell = hexStringToInt(encodeHexStr(byteArrayOf(data[5])))
                result = if (openCell == 0) {
                    " Open door with control panel is successful"
                } else {
                    " Open door with control panel has failed"
                }

            }

            /**
             * 查询面板锁
             * */
            READ_SCREEN_LOCKER_INFO -> {
                val lockerStatus = hexStringToInt(encodeHexStr(byteArrayOf(data[5])))
                result = if (lockerStatus == 0) {
                    " Lock on control panel is closed"
                } else {
                    " Lock on control panel is open"
                }
            }
        }
        return result

    }
}