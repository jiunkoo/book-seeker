package com.example.bookseeker.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import com.example.bookseeker.model.data.SignUpData
import com.example.bookseeker.contract.SignUpContract
import com.example.bookseeker.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpPresenter : SignUpContract.Presenter {
    private var signUpView: SignUpContract.View? = null
    private val retrofitInterface = RetrofitClient.retrofitInterface
//    private var handler : Handler? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: SignUpContract.View) {
        signUpView = view
    }

    // checkRegEx : SignUpPresenter에서 EditText의 RegEx를 검사하는 함수
    override fun checkRegEx(txtv: String, etxt: String): String {
        var checkRegExResult = "NONE"
        var matcher: Matcher
        // Email RegEx, Name RegEx(2 ~ 5 digit), Password RegEx(4 ~ 10 digit)
        val EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
        val NAME_REGEX = Pattern.compile("^[a-zA-Z].{4,10}$", Pattern.CASE_INSENSITIVE)
        val PASSWORD_REGEX = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{4,10}$", Pattern.CASE_INSENSITIVE)

        when (txtv) {
            "EMAIL" -> {
                matcher = EMAIL_REGEX.matcher(etxt)
                if (matcher.find() == true) {
                    checkRegExResult = "TRUE"
                } else {
                    checkRegExResult = "FALSE"
                }
            }
            "NAME" -> {
                matcher = NAME_REGEX.matcher(etxt)
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

    // insertSignUpData : SignUpActivity에서 SignUp Data를 저장하는 함수
    override fun insertSignUpData(signUpData: SignUpData) {
        signUpView?.setProgressON("회원가입을 진행중입니다...")

        var checkEmailDuplication = retrofitInterface.selectOneSignUpDataByEmail(signUpData.email)
        checkEmailDuplication.enqueue(object : Callback<List<SignUpData>> {
            override fun onResponse(call: Call<List<SignUpData>>, response: Response<List<SignUpData>>) {
                executionLog("SELECT", "Select One SignUp Data Success!")
                if (response.isSuccessful) {
                    var response = response.body()
                    if (response != null) {
                        executionLog("RESPONSE", "$response")
                        if (response.isEmpty()) {
                            var insertSignUpData = retrofitInterface.insertSignUpData(signUpData)
                            insertSignUpData.enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    try {
                                        executionLog("INSERT", "Insert SignUp Data Success!")
                                        signUpView?.setProgressOFF()
                                        signUpView?.showMessage("성공적으로 회원가입을 완료했습니다.")
                                        signUpView?.startLoginActivity()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    executionLog("INSERT", "Insert SignUp Data Failure!")
                                    signUpView?.setProgressOFF()
                                    signUpView?.showMessage("회원가입을 실패했습니다.")
                                }
                            })
                        } else {
                            signUpView?.setProgressOFF()
                            signUpView?.showMessage("중복되는 이메일입니다.")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<SignUpData>>, t: Throwable) {
                executionLog("SELECT", "Select One SignUp Data Failure!")
                signUpView?.setProgressOFF()
            }
        })
    }

//    fun insertSignUpDataHandler() {
//        signUpView?.setProgressON("회원가입을 진행중입니다...")
//        @SuppressLint("HandlerLeak")
//        handler = object : Handler() {
//            override fun handleMessage(msg: Message) {
//                sendEmptyMessage(0) // Send Empty Message
//            }
//        }
//    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        signUpView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}