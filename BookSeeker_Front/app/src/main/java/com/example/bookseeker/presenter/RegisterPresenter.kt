package com.example.bookseeker.presenter

import android.content.Context
import android.util.Log
import com.example.bookseeker.contract.RegisterContract
import com.example.bookseeker.model.data.RegisterRequest
import com.example.bookseeker.network.RetrofitClient
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.OkHttpClient
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterPresenter : RegisterContract.Presenter {
    private var registerView: RegisterContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: RegisterContract.View) {
        registerView = view
    }

    // checkRegEx : SignUpPresenter에서 EditText의 RegEx를 검사하는 함수
    override fun checkRegEx(txtv: String, etxt: String): String {
        var checkRegExResult = "NONE"
        var matcher: Matcher
        // Email RegEx, Name RegEx(2 ~ 5 digit), Password RegEx(4 ~ 10 digit)
        val EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
        val NICKNAME_REGEX = Pattern.compile("^[a-zA-Z가-힣0-9].{4,10}$", Pattern.CASE_INSENSITIVE)
        val PASSWORD_REGEX = Pattern.compile("^(?=.*[a-z])(?=.*[0-9]).{4,10}$", Pattern.CASE_INSENSITIVE)

        when (txtv) {
            "EMAIL" -> {
                matcher = EMAIL_REGEX.matcher(etxt)
                if (matcher.find() == true) {
                    checkRegExResult = "TRUE"
                } else {
                    checkRegExResult = "FALSE"
                }
            }
            "NICKNAME" -> {
                matcher = NICKNAME_REGEX.matcher(etxt)
                if (matcher.find() == true) {
                    checkRegExResult = "TRUE"
                } else {
                    checkRegExResult = "FALSE"
                }
            }
            "PASSWORD" -> {
                matcher = PASSWORD_REGEX.matcher(etxt)
                if (matcher.find() == true) {
                    checkRegExResult = "TRUE"
                } else {
                    checkRegExResult = "FALSE"
                }
            }
        }
        return checkRegExResult
    }

    // insertRegisterData : SignUpActivity에서 User Data를 저장하는 함수
    override fun insertRegisterData(context: Context, registerRequest: RegisterRequest): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "NONE")
        val retrofitInterface = RetrofitClient.retrofitInterface(client)

        registerView?.setProgressON("회원가입을 진행중입니다...")

        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.register(registerRequest)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                var result = response.body()

                if (result != null) {
                    subscriber.onNext(result)
                }
                subscriber.onComplete() // 구독자에게 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        registerView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}