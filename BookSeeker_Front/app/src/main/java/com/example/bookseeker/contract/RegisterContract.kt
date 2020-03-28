package com.example.bookseeker.contract

import android.content.Context
import com.example.bookseeker.model.data.Register
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable


interface RegisterContract {
    interface View : BaseView {
        // setButtonEventListener : Button 이벤트를 처리하는 함수
        fun setButtonEventListener()

        // setEditTextEventListener : EditText 이벤트를 처리하는 함수
        fun setEditTextEventListener()

        // startLoginActivity : LoginActivity로 넘어가는 함수
        fun startLoginActivity()

        // registerSubscribe : 관찰자에게서 사용자의 회원가입 여부를 가져오는 함수
        fun registerSubscribe(register: Register)
    }

    interface Presenter : BasePresenter<View> {
        // checkRegEx : EditText의 RegEx를 검사하는 함수
        fun checkRegEx(txtv: String, etxt: String): String

        // registerObservable : 사용자 데이터를 서버로 보내고 관찰하는 함수
        fun registerObservable(context: Context, register: Register): Observable<JsonObject>
    }
}