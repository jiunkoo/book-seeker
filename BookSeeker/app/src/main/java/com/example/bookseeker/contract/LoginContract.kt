package com.example.bookseeker.contract

import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView

interface LoginContract {
    interface View : BaseView {
        // startMainActivity : LoginActivity에서 MainActivity로 넘어가는 함수
        fun startMainActivity ()
    }
    interface Presenter : BasePresenter<View> {
        // loginCheck : View에서 Email과 Password를 받아와 일치 여부를 비교하는 함수
        fun loginCheck (inputEmail: String?, inputPassword: String?): Int
    }
}