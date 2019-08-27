package com.example.bookseeker.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.bookseeker.model.data.SignUpData
import com.example.bookseeker.contract.SignUpContract
import com.example.bookseeker.presenter.SignUpPresenter
import kotlinx.android.synthetic.main.activity_signup.*
import android.os.Handler
import android.util.Log
import com.example.bookseeker.R

class SignUpActivity : BaseActivity(), SignUpContract.View {
    // SignUpActivity와 함께 생성될 SignUpPresenter를 지연 초기화
    private lateinit var signUpPresenter: SignUpPresenter
    private var handler : Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        signUpPresenter.takeView(this)

        // Button Event 처리
        setButtonEventListener()

        // EditText Event 처리
        setEditTextEventListener()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        signUpPresenter = SignUpPresenter()
    }

    // setSignUpButtonEventListener : SignUpActivity에서 Button Event를 처리하는 함수
    override fun setButtonEventListener() {
        // SignUp Button Event를 처리하는 함수
        signup_btn_signup.setOnClickListener() {
            var signUpData = SignUpData(
                signup_etxt_email.text.toString(),
                signup_etxt_name.text.toString(),
                signup_etxt_password.text.toString()
            )
            signUpPresenter.insertSignUpData(signUpData)
        }

        // SignIn Button Event를 처리하는 함수
        signup_txtv_signin.setOnClickListener {
            startLoginActivity()
        }
    }

    // setEditTextEventListener : SignUpActivity에서 EditText Event를 처리하는 함수
    override fun setEditTextEventListener() {
        // Email EditText Event를 처리하는 함수
        signup_etxt_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                var checkRegExResult = signUpPresenter.checkRegEx("EMAIL", signup_etxt_email.text.toString())
                when (checkRegExResult) {
                    "TRUE" -> {
                        signup_etxt_email.setTextColor(Color.parseColor("#ffffff")) // basicWhite

                        if (signup_etxt_email.textColors == signup_etxt_name.textColors &&
                            signup_etxt_name.textColors == signup_etxt_password.textColors &&
                            signup_etxt_password.textColors == signup_etxt_passwordconfirm.textColors
                        ) {
                            signup_btn_signup.isEnabled = true
                            signup_btn_signup.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                        }
                    }
                    "FALSE" -> {
                        signup_etxt_email.setTextColor(Color.parseColor("#e02947")) // mediumRed
                        signup_btn_signup.isEnabled = false
                        signup_btn_signup.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                    }
                    "NONE" -> {
                        executionLog("ERROR", "Email TextChangedListener Running Error")
                    }
                }
            }
        })

        // Name EditText Event를 처리하는 함수
        signup_etxt_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                var checkRegExResult = signUpPresenter.checkRegEx("NAME", signup_etxt_name.text.toString())
                when (checkRegExResult) {
                    "TRUE" -> {
                        signup_etxt_name.setTextColor(Color.parseColor("#ffffff")) // basicWhite

                        if (signup_etxt_email.textColors == signup_etxt_name.textColors &&
                            signup_etxt_name.textColors == signup_etxt_password.textColors &&
                            signup_etxt_password.textColors == signup_etxt_passwordconfirm.textColors
                        ) {
                            signup_btn_signup.isEnabled = true
                            signup_btn_signup.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                        }
                    }
                    "FALSE" -> {
                        signup_etxt_name.setTextColor(Color.parseColor("#e02947")) // mediumRed
                        signup_btn_signup.isEnabled = false
                        signup_btn_signup.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                    }
                    "NONE" -> {
                        executionLog("ERROR", "Name TextChangedListener Running Error")
                    }
                }
            }
        })

        // Password EditText Event를 처리하는 함수
        signup_etxt_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                signup_etxt_passwordconfirm.text = null
            }

            override fun afterTextChanged(p0: Editable?) {
                var checkRegExResult = signUpPresenter.checkRegEx("PASSWORD", signup_etxt_password.text.toString())
                when (checkRegExResult) {
                    "TRUE" -> {
                        signup_etxt_password.setTextColor(Color.parseColor("#ffffff")) // white
                        signup_etxt_passwordconfirm.text = null
                        if (signup_etxt_email.textColors == signup_etxt_name.textColors &&
                            signup_etxt_name.textColors == signup_etxt_password.textColors &&
                            signup_etxt_password.textColors == signup_etxt_passwordconfirm.textColors
                        ) {
                            signup_btn_signup.isEnabled = true
                            signup_btn_signup.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                        }
                    }
                    "FALSE" -> {
                        signup_etxt_password.setTextColor(Color.parseColor("#e02947")) // mediumRed
                        signup_btn_signup.isEnabled = false
                        signup_btn_signup.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                    }
                    "NONE" -> {
                        executionLog("ERROR","Password TextChangedListener Running Error")
                    }
                }
            }
        })

        // Password Confirm EditText Event를 처리하는 함수
        signup_etxt_passwordconfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (signup_etxt_password.text.toString() == signup_etxt_passwordconfirm.text.toString()) {
                    signup_etxt_passwordconfirm.setTextColor(Color.parseColor("#ffffff")) // basicWhite

                    if (signup_etxt_email.textColors == signup_etxt_name.textColors &&
                        signup_etxt_name.textColors == signup_etxt_password.textColors &&
                        signup_etxt_password.textColors == signup_etxt_passwordconfirm.textColors
                    ) {
                        signup_btn_signup.isEnabled = true
                        signup_btn_signup.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                    }
                } else {
                    signup_etxt_passwordconfirm.setTextColor(Color.parseColor("#e02947")) // mediumRed
                    signup_btn_signup.isEnabled = false
                    signup_btn_signup.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                }
            }
        })
    }

    // startLoginActivity : SignUpActivity에서 LoginActivity로 넘어가는 함수
    override fun startLoginActivity() {
        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        finish()
    }

    // setProgressON :  공통으로 사용하는 Progress Bar의 시작을 정의하는 함수
    override fun setProgressON(msg: String){
        progressON(msg)
    }

    // setProgressOFF() : 공통으로 사용하는 Progress Bar의 종료를 정의하는 함수
    override fun setProgressOFF() {
        progressOFF()
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@SignUpActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}