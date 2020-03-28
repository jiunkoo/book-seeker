package com.example.bookseeker.contract

import android.content.Context
import com.example.bookseeker.model.data.Login
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable


interface LoginContract {
    interface View : BaseView {
        // setLoginButtonEventListener : Button 이벤트를 처리하는 함수
        fun setButtonEventListener()

        // setTextViewEventListener : TextView 이벤트를 처리하는 함수
        fun setTextViewEventListener()

        // setEditTextEventListener : EditText 이벤트를 처리하는 함수
        fun setEditTextEventListener()

        // startSearchActivity : SearchActivity로 넘어가는 함수
        fun startSearchActivity()

        // startRegisterActivity : RegisterActivity로 넘어가는 함수
        fun startRegisterActivity()

        // loginSubscribe : 관찰자에게서 로그인 데이터를 가져오는 함수
        fun loginSubscribe(login: Login)
    }

    interface Presenter : BasePresenter<View> {
        // checkRegEx : LoginPresenter에서 EditText의 RegEx를 검사하는 함수
        fun checkRegEx(txtv: String, etxt: String): String

        // login : 로그인 데이터를 서버로 보내고 응답을 관찰하는 함수
        fun loginObservable(context: Context, login : Login): Observable<JsonObject>
    }
}