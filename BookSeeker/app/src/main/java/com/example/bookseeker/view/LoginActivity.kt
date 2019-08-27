package com.example.bookseeker.view

import android.content.Intent
<<<<<<< HEAD
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
=======
import android.os.Bundle
import android.widget.EditText
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
import android.widget.Toast
import com.example.bookseeker.R
import com.example.bookseeker.contract.LoginContract
import com.example.bookseeker.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), LoginContract.View {
    // LoginActivity와 함께 생성될 LoginPresenter를 지연 초기화
    private lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        loginPresenter.takeView(this)

<<<<<<< HEAD
        // Button Event 처리
        setButtonEventListener()

        // TextView Event 처리
        setTextViewEventListener()

        //Edit Text Event 처리
        setEditTextEventListener()
=======
        // 로그인 버튼 이벤트 처리
        setLoginButton()
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        loginPresenter = LoginPresenter()
    }

<<<<<<< HEAD
    // setButtonEventListener : LoginActivity에서 Button Event를 처리하는 함수
    override fun setButtonEventListener() {
        // Login Button Event를 처리하는 함수
        login_btn_login.setOnClickListener {
            loginPresenter.checkLoginData(login_etxt_email.text.toString(), login_etxt_password.text.toString())
        }
    }

    // setTextViewEventListener : LoginActivity에서 TextView Event를 처리하는 함수
    override fun setTextViewEventListener() {
        // SignUp TextView Event를 처리하는 함수
        login_txtv_signup.setOnClickListener {
            startSignUpActivity()
        }
    }

    // setEditTextEventListener : SignUpActivity에서 EditText Event를 처리하는 함수
    override fun setEditTextEventListener() {
        // Email EditText Event를 처리하는 함수
        login_etxt_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                var checkRegExResult = loginPresenter.checkRegEx("EMAIL", login_etxt_email.text.toString())
                when (checkRegExResult) {
                    "TRUE" -> {
                        login_etxt_email.setTextColor(Color.parseColor("#ffffff")) // basicWhite

                        if (login_etxt_email.textColors == login_etxt_password.textColors) {
                            login_btn_login.isEnabled = true
                            login_btn_login.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                        }
                    }
                    "FALSE" -> {
                        login_etxt_email.setTextColor(Color.parseColor("#e02947")) // mediumRed
                        login_btn_login.isEnabled = false
                        login_btn_login.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                    }
                    "NONE" -> {
                        executionLog("ERROR", "Email TextChangedListener Running Error")
                    }
                }
            }
        })

        // Password EditText Event를 처리하는 함수
        login_etxt_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                var checkRegExResult = loginPresenter.checkRegEx("PASSWORD", login_etxt_password.text.toString())
                when (checkRegExResult) {
                    "TRUE" -> {
                        login_etxt_password.setTextColor(Color.parseColor("#ffffff")) // basicWhite

                        if (login_etxt_email.textColors == login_etxt_password.textColors) {
                            login_btn_login.isEnabled = true
                            login_btn_login.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                        }
                    }
                    "FALSE" -> {
                        login_etxt_password.setTextColor(Color.parseColor("#e02947")) // mediumRed
                        login_btn_login.isEnabled = false
                        login_btn_login.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                    }
                    "NONE" -> {
                        executionLog("ERROR", "Email TextChangedListener Running Error")
                    }
                }
            }
        })
    }

    // startSearchActivity : LoginActivity에서 SearchActivity로 넘어가는 함수
=======
    // setLoginButton : 로그인 버튼 이벤트를 처리하는 함수
    private fun setLoginButton(){
        login_btn_login.setOnClickListener {
            var loginResult = loginPresenter.loginCheck(login_etxt_email.text.toString(), login_etxt_password.text.toString())

            when(loginResult){
                0 -> Toast.makeText(this@LoginActivity, "존재하지 않는 사용자입니다.", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(this@LoginActivity, "아이디 혹은 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
                2 -> startSearchActivity()
            }
            println("loginResult $loginResult")
        }
    }

    // startMainActivity : LoginActivity에서 MainActivity로 넘어가는 함수
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
    override fun startSearchActivity() {
        val nextIntent = Intent(this, SearchActivity::class.java)
        startActivity(nextIntent)
    }

<<<<<<< HEAD
    // startSignUpActivity : LoginActivity에서 SignUpActivity로 넘어가는 함수
    override fun startSignUpActivity() {
        val nextIntent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(nextIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
=======
    override fun onDestroy() {
        super.onDestroy()

>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        loginPresenter.dropView()
    }

<<<<<<< HEAD
    // setProgressON :  공통으로 사용하는 Progress Bar의 시작을 정의하는 함수
    override fun setProgressON(msg: String) {
        progressON(msg)
    }

    // setProgressOFF() : 공통으로 사용하는 Progress Bar의 종료를 정의하는 함수
    override fun setProgressOFF() {
        progressOFF()
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
=======
    // showError : 공통으로 쓰이는 error 출력 부분을 생성하는 함수
    override fun showError(error: String) {
        println("LoginActivity에서 Error가 발생하였습니다.")
        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_SHORT).show()
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
    }
}