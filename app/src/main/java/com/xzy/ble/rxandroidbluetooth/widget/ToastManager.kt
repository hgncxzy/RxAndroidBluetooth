package com.xzy.ble.rxandroidbluetooth.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.Toast


/**
 * 使用反射实现 Toast 以及动画。
 */
@Suppress("unused")
class ToastManager private constructor(private val context: Context) {

    private var toast: Toast? = null


    init {
        toast = Toast(context)
    }

    /**
     * 自定义View和显示位置的Toast
     *
     * @param view    Toast的View视图
     * @param gravity Toast的显示位置
     * @param xOffset Toast在x方向上偏移量,x大于0往右，x小于0往左
     * @param yOffset Toast在y方向上偏移量,y值大于0往上，小于0往下
     */
    fun makeToastSelfView(view: View, gravity: Int, xOffset: Int, yOffset: Int) {
        if (toast == null) {
            toast = Toast(context)
        }

        toast!!.view = view
        toast!!.setGravity(gravity, xOffset, yOffset)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    /**
     * 构造带有动画的Toast
     *
     * @param tText       提示的文本
     * @param animationID style封装的动画资源id
     */
    fun makeToastSelfAnimation(tText: String, animationID: Int) {
        toast = Toast.makeText(context, tText, Toast.LENGTH_SHORT)
        try {
            val mTNField = toast!!.javaClass.getDeclaredField("mTN")
            mTNField.isAccessible = true
            val mTNObject = mTNField.get(toast)
            val tnClass = mTNObject!!.javaClass
            val paramsField = tnClass.getDeclaredField("mParams")
            /*由于WindowManager.LayoutParams mParams的权限是private*/
            paramsField.isAccessible = true
            val layoutParams = paramsField.get(mTNObject) as WindowManager.LayoutParams
            layoutParams.windowAnimations = animationID
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        toast!!.show()
    }

    /**
     * 设置自定义View和Animation
     *
     * @param view        自定义View
     * @param animationID 动画资源id
     */
    fun makeToastSelfViewAnim(view: View, animationID: Int) {
        if (toast == null) {
            toast = Toast(context)
        }

        toast!!.view = view
        try {
            val mTNField = toast!!.javaClass.getDeclaredField("mTN")
            mTNField.isAccessible = true
            val mTNObject = mTNField.get(toast)
            val tnClass = mTNObject!!.javaClass
            val paramsField = tnClass.getDeclaredField("mParams")
            /**由于WindowManager.LayoutParams mParams的权限是private */
            paramsField.isAccessible = true
            val layoutParams = paramsField.get(mTNObject) as WindowManager.LayoutParams
            layoutParams.windowAnimations = animationID
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        toast!!.show()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: ToastManager? = null

        /**
         * 构造ToastManager对象
         *
         * @param context 上下文对象
         * @return
         */
        fun getInstance(context: Context): ToastManager {
            if (instance == null) {
                instance = ToastManager(context)
            }
            return instance as ToastManager
        }
    }
}