package com.example.bookseeker.contract

import com.example.bookseeker.model.data.SignUpData
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView

interface SignUpContract {
    interface View : BaseView {
        // setSignUpButtonEventListener : SignUpActivity에서 Button Event를 처리하는 함수
        fun setButtonEventListener()

        // setEditTextEventListener : SignUpActivity에서 EditText Event를 처리하는 함수
        fun setEditTextEventListener()

        // startLoginActivity : SignUpActivity에서 LoginActivity로 넘어가는 함수
        fun startLoginActivity()
    }

    interface Presenter : BasePresenter<View> {
        // checkRegEx : SignUpPresenter에서 EditText의 RegEx를 검사하는 함수
        fun checkRegEx(txtv: String, etxt: String): String

        // insertSignUpData : SignUpPresenter에서 SignUp Data를 저장하는 함수
        fun insertSignUpData(signUpDdata: SignUpData)
    }
}