package com.example.bookseeker.contract

import android.content.Context
import com.example.bookseeker.model.data.Register
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable

interface RegisterContract {
    interface View : BaseView {
        // setButtonEventListener : RegisterActivity에서 Button Event를 처리하는 함수
        fun setButtonEventListener()

        // setEditTextEventListener : RegisterActivity에서 EditText Event를 처리하는 함수
        fun setEditTextEventListener()

        // startLoginActivity : RegisterActivity에서 LoginActivity로 넘어가는 함수
        fun startLoginActivity()
    }

    interface Presenter : BasePresenter<View> {
        // checkRegEx : RegisterPresenter에서 EditText의 RegEx를 검사하는 함수
        fun checkRegEx(txtv: String, etxt: String): String

        // registerObservable : RegisterPresenter에서 Register Data를 저장하는 함수
        fun registerObservable(context: Context, register: Register): Observable<JsonObject>
    }
}