package com.example.bookseeker.presenter

<<<<<<< HEAD
import android.util.Log
import com.example.bookseeker.contract.LoginContract
import com.example.bookseeker.model.data.SignUpData
import com.example.bookseeker.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginPresenter : LoginContract.Presenter {
    private var loginView: LoginContract.View? = null
    private val retrofitInterface = RetrofitClient.retrofitInterface
=======
import com.example.bookseeker.contract.LoginContract

class LoginPresenter : LoginContract.Presenter {
    private var loginView: LoginContract.View? = null
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: LoginContract.View) {
        loginView = view
    }

<<<<<<< HEAD
    // checkRegEx : LoginPresenter에서 EditText의 RegEx를 검사하는 함수
    override fun checkRegEx(txtv: String, etxt: String): String {
        var checkRegExResult = "NONE"
        var matcher: Matcher
        // Email RegEx, Name RegEx(2 ~ 5 digit), Password RegEx(4 ~ 10 digit)
        val EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
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

    // loginCheck : View에서 Email과 Password를 받아와 일치 여부를 비교하는 함수
    override fun checkLoginData(inputEmail: String, inputPassword: String) {
        loginView?.setProgressON("로그인을 진행중입니다...")

        var checkLoginData = retrofitInterface.selectOneSignUpDataByEmail(inputEmail)
        checkLoginData.enqueue(object : Callback<List<SignUpData>> {
            override fun onResponse(call: Call<List<SignUpData>>, response: Response<List<SignUpData>>) {
                executionLog("SELECT", "Select One SignUp Data Success!")
                if (response.isSuccessful) {
                    var response = response.body()
                    if (response != null) {
                        executionLog("RESPONSE", "$response")
                        if (response.isEmpty()) {
                            loginView?.setProgressOFF()
                            loginView?.showMessage("없는 이메일입니다.")
                        } else {
                            if(inputEmail == response.get(0).email && inputPassword == response.get(0).password){
                                loginView?.setProgressOFF()
                                loginView?.startSearchActivity()
                            } else if(inputEmail == response.get(0).email && inputPassword != response.get(0).password){
                                loginView?.setProgressOFF()
                                loginView?.showMessage("비밀번호를 잘못 입력했습니다.")
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<SignUpData>>, t: Throwable) {
                executionLog("SELECT", "Select One SignUp Data Failure!")
            }
        })
=======
    // loginCheck : View에서 Email과 Password를 받아와 일치 여부를 비교하는 함수
    override fun loginCheck(inputEmail: String?, inputPassword: String?): Int {
        var inputMatchConfirm = -1

        println("inputEmail: $inputEmail and inputPassword: $inputPassword")
        // 이메일과 비밀번호가 둘 다 일치하지 않을 경우
        if (inputEmail != "admin" && inputPassword != "1111") {
            inputMatchConfirm = 0
            println("둘 다 불일치")
        }
        // 이메일과 비밀번호 중 하나가 일치하는 경우
        else if (inputEmail == "admin" && inputPassword != "1111" || inputEmail != "admin" && inputPassword == "1111") {
            inputMatchConfirm = 1
            println("하나는 일치")
        } 
        // 이메일과 비밀번호 둘 다 일치하는 경우
        else if (inputEmail == "admin" && inputPassword == "1111"){
            inputMatchConfirm = 2
            println("둘 다 일치")
        } 
        // 그 외의 경우
        else {
            inputMatchConfirm = -1
        }
        return inputMatchConfirm
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        loginView = null
    }
<<<<<<< HEAD

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
=======
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
}