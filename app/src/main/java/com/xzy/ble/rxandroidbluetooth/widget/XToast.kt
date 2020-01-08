@file:Suppress("DEPRECATION")

package com.xzy.ble.rxandroidbluetooth.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import android.widget.ImageView
import android.widget.TextView
import android.app.Activity
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.xzy.ble.rxandroidbluetooth.R
import java.util.*


@Suppress("unused")
object XToast {

    private var toast: Toast? = null
    private var imgToast: Toast? = null
    private var textToast: Toast? = null
    private val handler = Handler()

    /**
     * 线程安全的 Toast
     *
     * */
    @Suppress("unused")
    fun showToast(ctx: Context, showMsg: String) {
        handler.post { Toast.makeText(ctx, showMsg, Toast.LENGTH_SHORT).show() }
    }


    /**
     * 默认的 Toast
     *
     * */
    @Suppress("unused")
    fun showDefaultToast(ctx: Context, showMsg: String) {
        Toast.makeText(ctx, showMsg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 自定义位置的 Toast
     *
     * */
    @Suppress("unused")
    fun showCustomPositionToast(ctx: Context, showMsg: String) {
        val toast = Toast.makeText(ctx, showMsg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 66, 80)
        toast.view.setBackgroundColor(ctx.resources.getColor(R.color.colorPrimary))
        toast.show()
    }

    /**
     * 自定义位置的 Toast
     *
     * */
    @Suppress("unused")
    fun showCustomBgToast(ctx: Context, showMsg: String) {
        val temp = "<font color=\"#FFFFFF\">$showMsg</font>"
        val text = Html.fromHtml(temp)
        val toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.view.setBackgroundColor(ctx.resources.getColor(R.color.colorPrimary))
        toast.show()
    }

    /**
     * 带图片的 Toast -- 自定义布局
     * **/
    @Suppress("unused")
    @SuppressLint("InflateParams")
    fun showToastWithImg(activity: Activity, content: String, @DrawableRes imageResource: Int) {
        cancel()
        imgToast = Toast(activity)
        val view = activity.layoutInflater.inflate(R.layout.toast_with_img, null)
        val tvContent = view.findViewById(R.id.tv_content) as TextView
        val ivImage = view.findViewById(R.id.tv_image) as ImageView
        tvContent.text = content
        ivImage.setImageResource(imageResource)
        imgToast?.view = view
        imgToast?.setGravity(Gravity.CENTER, 0, 0)
        toast = imgToast
        toast?.show()
    }

    /**
     * 自定义文本 Toast -- 自定义布局
     * */
    @Suppress("unused")
    @SuppressLint("ResourceType", "InflateParams")
    fun showCustomToast(activity: Activity, content: String) {
        cancel()
        textToast = Toast.makeText(activity, content, Toast.LENGTH_SHORT)
        val view = activity.layoutInflater.inflate(R.layout.toast, null)
        textToast?.view = view
        textToast?.setGravity(Gravity.CENTER, 0, 0)
        textToast?.setText(content)
        toast = textToast
        toast?.show()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ShowToast")
    @Suppress("unused")
            /**
             *
             * 使用系统 Toast 的布局，并带动画效果
             * */
    fun showToastWithAnim(context: Context, changeShowTime: Boolean, text: String) {
        val wdm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var isShow = false
        // 设置布局，可以自定义
        val toastView = Toast.makeText(context, text, Toast.LENGTH_SHORT).view
        toastView.setBackgroundColor(context.resources.getColor(R.color.colorAccent, null))
        val timer = Timer()
        val layoutParams = WindowManager.LayoutParams()

        // 设置参数
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.windowAnimations = R.style.MiuiToast//设置进入退出动画效果
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutParams.gravity = Gravity.BOTTOM
//        mParams.y = 250

        // 设置展示
        if (!isShow) {//如果Toast没有显示，则开始加载显示
            isShow = true
            wdm.addView(toastView, layoutParams)//将其加载到windowManager上
            timer.schedule(object : TimerTask() {
                override fun run() {
                    wdm.removeView(toastView)
                    isShow = false
                }
            }, (if (changeShowTime) 3500 else 2000).toLong())
        }

    }


    /**
     *
     * 使用自定义布局，并带图片和动画效果
     * */
    @Suppress("unused")
    @SuppressLint("ShowToast", "InflateParams")
    fun showToastWithAnimAndImg(activity: Activity, content: String, imageResource: Int) {
        val wdm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var isShow = false
        // 设置布局，可以自定义
        val toastView = activity.layoutInflater.inflate(R.layout.toast_with_img, null)
        val tvContent = toastView.findViewById(R.id.tv_content) as TextView
        val ivImage = toastView.findViewById(R.id.tv_image) as ImageView
        tvContent.text = content
        ivImage.setImageResource(imageResource)
        val timer = Timer()
        val layoutParams = WindowManager.LayoutParams()

        // 设置参数
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.windowAnimations = R.style.MiuiToast//设置进入退出动画效果
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutParams.gravity = Gravity.CENTER

        // 设置展示
        if (!isShow) {//如果Toast没有显示，则开始加载显示
            isShow = true
            wdm.addView(toastView, layoutParams)//将其加载到windowManager上
            timer.schedule(object : TimerTask() {
                override fun run() {
                    wdm.removeView(toastView)
                    isShow = false
                }
            }, 2000.toLong())
        }
    }

    private fun cancel() {
        toast?.cancel()
    }
}

/**
 * 参考了 https://www.cnblogs.com/net168/p/4237528.html
 * 但是去掉了 setParams 方法中的 mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
 * 否则在 Android7.0以上会闪退。
 * **/
@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("ShowToast")
@Suppress("unused")
class AnimToast private constructor(
    context: Context,
    text: String,
    private val mShowTime: Boolean //记录Toast的显示长短类型
) {
    private val mWdm: WindowManager
    private var mIsShow: Boolean = false
    private val mToastView: View
    private val mTimer: Timer
    private var mParams: WindowManager.LayoutParams? = null

    init {
        mIsShow = false//记录当前Toast的内容是否已经在显示
        mWdm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //通过Toast实例获取当前android系统的默认Toast的View布局
        mToastView = Toast.makeText(context, text, Toast.LENGTH_SHORT).view
        mToastView.setBackgroundColor(context.resources.getColor(R.color.colorAccent, null))
        mTimer = Timer()
        //设置布局参数
        setParams()
    }


    private fun setParams() {
        mParams = WindowManager.LayoutParams()
        mParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams!!.format = PixelFormat.TRANSLUCENT
        mParams!!.windowAnimations = R.style.MiuiToast//设置进入退出动画效果
        mParams!!.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mParams!!.gravity = Gravity.BOTTOM
        mParams!!.y = 250
    }


    fun show() {
        if (!mIsShow) {//如果Toast没有显示，则开始加载显示
            mIsShow = true
            mWdm.addView(mToastView, mParams)//将其加载到windowManager上
            mTimer.schedule(object : TimerTask() {
                override fun run() {
                    mWdm.removeView(mToastView)
                    mIsShow = false
                }
            }, (if (mShowTime) 3500 else 2000).toLong())
        }

    }

    companion object {
        fun makeText(context: Context, text: String, showTime: Boolean): AnimToast {
            return AnimToast(context, text, showTime)
        }
    }
}

