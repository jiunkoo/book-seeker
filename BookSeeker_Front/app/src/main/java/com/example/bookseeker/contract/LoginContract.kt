package com.example.bookseeker.contract

import android.content.Context
import com.example.bookseeker.model.data.Login
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable

interface LoginContract {
    interface View : BaseView {
        // setLoginButtonEventListener : LoginActivity에서 Button Event를 처리하는 함수
        fun setButtonEventListener()

        // setTextViewEventListener : LoginActivity에서 TextView Event를 처리하는 함수
        fun setTextViewEventListener()

        // setEditTextEventListener : SignUpActivity에서 EditText Event를 처리하는 함수
        fun setEditTextEventListener()

        // startSearchActivity : LoginActivity에서 SearchActivity로 넘어가는 함수
        fun startSearchActivity()

        // startRegisterActivity : LoginActivity에서 SignUpActivity로 넘어가는 함수
        fun startRegisterActivity()
    }

    interface Presenter : BasePresenter<View> {
        // checkRegEx : LoginPresenter에서 EditText의 RegEx를 검사하는 함수
        fun checkRegEx(txtv: String, etxt: String): String

        // checkLoginData : LoginPresenter에서 Email과 Password의 일치 여부를 비교하는 함수
        fun checkLoginData(context: Context, login : Login): Observable<JsonObject>
    }
}