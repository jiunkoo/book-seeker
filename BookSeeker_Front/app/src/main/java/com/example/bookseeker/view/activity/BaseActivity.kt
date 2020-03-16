package com.example.bookseeker.view.activity

import android.os.Bundle
import com.example.bookseeker.view.BaseApplication
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity

abstract class BaseActivity : RxAppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }
    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    abstract fun initPresenter()

    fun progressON() {
        BaseApplication.instance.progressON(this, null!!)
    }

    fun progressON(message: String) {
        BaseApplication.instance.progressON(this, message)
    }

    fun progressOFF() {
        BaseApplication.instance.progressOFF()
    }
}