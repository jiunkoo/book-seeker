package com.example.bookseeker.contract

import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView

interface SlashContract {
    interface View : BaseView {
        // startLoginActivity : SlashActivity에서 LoginActivity로 넘어가는 함수
        fun startLoginActivity()
    }
    interface Presenter : BasePresenter<View> {
    }
}