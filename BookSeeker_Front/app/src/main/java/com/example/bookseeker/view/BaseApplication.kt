package com.example.bookseeker.view

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDialog
import com.example.bookseeker.R
import kotlinx.android.synthetic.main.dialog_loading.*

class BaseApplication : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: BaseApplication
    }
    private var appCompatDialog: AppCompatDialog? = null

//    override fun onCreate() {
//        super.onCreate()
//    }

    fun progressON(activity: Activity, message: String) {
        if (activity == null || activity.isFinishing) {
            return
        }
        if (appCompatDialog != null && appCompatDialog?.isShowing!!) {
            progressSET(message)
        } else {
            appCompatDialog = AppCompatDialog(activity)
            appCompatDialog?.setCancelable(false)
            appCompatDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            appCompatDialog?.setContentView(R.layout.dialog_loading)
            appCompatDialog?.show()
        }
        val frameAnimation = appCompatDialog?.loading_imgv_frame?.background as AnimationDrawable
        appCompatDialog?.loading_imgv_frame?.post { frameAnimation.start() }

        if (!TextUtils.isEmpty(message)) {
            appCompatDialog?.loading_txtv_message?.text = message
        }
    }

    fun progressSET(message: String) {
        if (appCompatDialog == null || !appCompatDialog?.isShowing!!) {
            return
        }

        if (!TextUtils.isEmpty(message)) {
            appCompatDialog?.loading_txtv_message?.text = message
        }
    }

    fun progressOFF() {
        if (appCompatDialog != null && appCompatDialog?.isShowing!!) {
            appCompatDialog?.dismiss()
        }
    }
}