package com.example.bookseeker.view.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.bookseeker.R
import com.example.bookseeker.contract.LoginContract
import com.example.bookseeker.model.data.LoginData
import com.example.bookseeker.presenter.LoginPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), LoginContract.View {
    // LoginActivity와 함께 생성될 LoginPresenter를 지연 초기화
    private lateinit var loginPresenter: LoginPresenter
    // Disposable 객체 지정
    private var subscriptions = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        loginPresenter.takeView(this)

        // Button Event 처리
        setButtonEventListener()

        // TextView Event 처리
        setTextViewEventListener()

        //Edit Text Event 처리
        setEditTextEventListener()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        loginPresenter = LoginPresenter()
    }

    // setButtonEventListener : LoginActivity에서 Button Event를 처리하는 함수
    override fun setButtonEventListener() {
        // Login Button Event를 처리하는 함수
        login_btn_login.setOnClickListener {
            var loginData = LoginData(
                login_etxt_email.text.toString(),
                login_etxt_password.text.toString()
            )
            requestLoginResult(loginData)
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
    override fun startSearchActivity() {
        val nextIntent = Intent(this, SearchActivity::class.java)
        startActivity(nextIntent)
    }

    // startSignUpActivity : LoginActivity에서 SignUpActivity로 넘어가는 함수
    override fun startSignUpActivity() {
        val nextIntent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(nextIntent)
    }

    // requestLoginResult : 관찰자에게서 발행된 데이터를 가져오는 함수
    fun requestLoginResult(loginData: LoginData) {
        val subscription = loginPresenter.checkLoginData(loginData)
            .subscribeOn(Schedulers.io()).subscribe(
                { result ->
                    when (result.status) {
                        "0", "1" -> {
                            Looper.prepare()
                            setProgressOFF()
                            showMessage("가입하지 않은 아이디거나 잘못된 비밀번호입니다.")
                            Looper.loop()
                        }
                        "2" -> {
                            Looper.prepare()
                            setProgressOFF()
                            showMessage("로그인에 성공하였습니다.")

                            /*
                            println("loginUser id : " + result.loginUser.id)
                            println("loginUser email : " + result.loginUser.email)
                            println("loginUser username : " + result.loginUser.username)
                            println("loginUser token : " + result.token)
                            */

                            // ShardPreference에 토큰 값 집어넣기
                            val pref = this.getPreferences(0)
                            val editor = pref.edit()
                            editor.putString("token", result.token)
                            editor.commit()

                            println("sharedPreference에 저장된 토큰 값은 : " + pref.getString("token", "None"))

                            startSearchActivity()
                            Looper.loop()
                        }
                    }
                },
                { e ->
                    Looper.prepare()
                    showMessage("Login Error!")
                    Looper.loop()
                }
            )
        subscriptions.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        loginPresenter.dropView()
    }

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
    }
}