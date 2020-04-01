package com.example.bookseeker.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.bookseeker.contract.RegisterContract
import com.example.bookseeker.presenter.RegisterPresenter
import android.util.Log
import com.example.bookseeker.R
import com.example.bookseeker.model.data.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity(), RegisterContract.View {
    // RegisterActivity와 함께 생성될 RegisterPresenter를 지연 초기화
    private lateinit var registerPresenter: RegisterPresenter

    // Disposable 객체 지연 초기화
    private lateinit var disposables: CompositeDisposable

    // onCreate() : Activity가 생성될 때 동작하는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        registerPresenter.takeView(this)

        // Disposable 객체 지정
        disposables = CompositeDisposable()

        // Button Event 처리
        setButtonEventListener()

        // EditText Event 처리
        setEditTextEventListener()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        registerPresenter = RegisterPresenter()
    }

    // setSignUpButtonEventListener : SignUpActivity에서 Button 이벤트를 처리하는 함수
    override fun setButtonEventListener() {
        // SignUp Button 이벤트를 처리하는 함수
        register_btn_register.setOnClickListener() {
            var userData = Register(
                register_etxt_email.text.toString(),
                register_etxt_nickname.text.toString(),
                register_etxt_password.text.toString()
            )
            registerSubscribe(userData)
        }

        // SignIn Button 이벤트를 처리하는 함수
        signup_txtv_signin.setOnClickListener {
            startLoginActivity()
        }
    }

    // setEditTextEventListener : RegisterActivity에서 EditText 이벤트를 처리하는 함수
    override fun setEditTextEventListener() {
        // Email EditText 이벤트를 처리하는 함수
        register_etxt_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                var checkRegExResult = registerPresenter.checkRegEx("EMAIL", register_etxt_email.text.toString())
                when (checkRegExResult) {
                    "TRUE" -> {
                        register_etxt_email.setTextColor(Color.parseColor("#ffffff")) // basicWhite

                        if (register_etxt_email.textColors == register_etxt_nickname.textColors &&
                            register_etxt_nickname.textColors == register_etxt_password.textColors &&
                            register_etxt_password.textColors == register_etxt_passwordconfirm.textColors
                        ) {
                            register_btn_register.isEnabled = true
                            register_btn_register.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                        }
                    }
                    "FALSE" -> {
                        register_etxt_email.setTextColor(Color.parseColor("#e02947")) // mediumRed
                        register_btn_register.isEnabled = false
                        register_btn_register.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                    }
                    "NONE" -> {
                        executionLog("ERROR", "Email TextChangedListener Running Error")
                    }
                }
            }
        })

        // Nickname EditText 이벤트를 처리하는 함수
        register_etxt_nickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                var checkRegExResult = registerPresenter.checkRegEx("NICKNAME", register_etxt_nickname.text.toString())
                when (checkRegExResult) {
                    "TRUE" -> {
                        register_etxt_nickname.setTextColor(Color.parseColor("#ffffff")) // basicWhite

                        if (register_etxt_email.textColors == register_etxt_nickname.textColors &&
                            register_etxt_nickname.textColors == register_etxt_password.textColors &&
                            register_etxt_password.textColors == register_etxt_passwordconfirm.textColors
                        ) {
                            register_btn_register.isEnabled = true
                            register_btn_register.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                        }
                    }
                    "FALSE" -> {
                        register_etxt_nickname.setTextColor(Color.parseColor("#e02947")) // mediumRed
                        register_btn_register.isEnabled = false
                        register_btn_register.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                    }
                    "NONE" -> {
                        executionLog("ERROR", "Name TextChangedListener Running Error")
                    }
                }
            }
        })

        // Password EditText 이벤트를 처리하는 함수
        register_etxt_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                register_etxt_passwordconfirm.text = null
            }

            override fun afterTextChanged(p0: Editable?) {
                var checkRegExResult = registerPresenter.checkRegEx("PASSWORD", register_etxt_password.text.toString())
                when (checkRegExResult) {
                    "TRUE" -> {
                        register_etxt_password.setTextColor(Color.parseColor("#ffffff")) // white
                        register_etxt_passwordconfirm.text = null
                        if (register_etxt_email.textColors == register_etxt_nickname.textColors &&
                            register_etxt_nickname.textColors == register_etxt_password.textColors &&
                            register_etxt_password.textColors == register_etxt_passwordconfirm.textColors
                        ) {
                            register_btn_register.isEnabled = true
                            register_btn_register.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                        }
                    }
                    "FALSE" -> {
                        register_etxt_password.setTextColor(Color.parseColor("#e02947")) // mediumRed
                        register_btn_register.isEnabled = false
                        register_btn_register.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                    }
                    "NONE" -> {
                        executionLog("ERROR", "Password TextChangedListener Running Error")
                    }
                }
            }
        })

        // Password Confirm EditText 이벤트를 처리하는 함수
        register_etxt_passwordconfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (register_etxt_password.text.toString() == register_etxt_passwordconfirm.text.toString()) {
                    register_etxt_passwordconfirm.setTextColor(Color.parseColor("#ffffff")) // basicWhite

                    if (register_etxt_email.textColors == register_etxt_nickname.textColors &&
                        register_etxt_nickname.textColors == register_etxt_password.textColors &&
                        register_etxt_password.textColors == register_etxt_passwordconfirm.textColors
                    ) {
                        register_btn_register.isEnabled = true
                        register_btn_register.setBackgroundColor(Color.parseColor("#80c783")) // mediumLime
                    }
                } else {
                    register_etxt_passwordconfirm.setTextColor(Color.parseColor("#e02947")) // mediumRed
                    register_btn_register.isEnabled = false
                    register_btn_register.setBackgroundColor(Color.parseColor("#c0e3c1")) // lightLime
                }
            }
        })
    }

    // startLoginActivity : SignUpActivity에서 LoginActivity로 넘어가는 함수
    override fun startLoginActivity() {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        finish()
    }

    // registerSubscribe : 관찰자에게서 사용자의 회원가입 여부를 가져오는 함수
    override fun registerSubscribe(register: Register) {
        val subscription = registerPresenter
            .registerObservable(this, register)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    showMessage(result.get("message").toString())
                    if((result.get("success").toString()).equals("true")) {
                        startLoginActivity()
                    }
                    executionLog("[INFO][REGISTER]", result.get("message").toString())
                },
                { e ->
                    executionLog("[ERROR][REGISTER]", e.message ?: "")
                }
            )
        disposables.add(subscription)
    }

    // onDestroy() : Activity가 종료될 때 동작하는 함수
    override fun onDestroy() {
        super.onDestroy()

        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        registerPresenter.dropView()

        executionLog("[INFO][REGISTER]", "disposable 객체 해제 전 상태 : " + disposables.isDisposed)
        executionLog("[INFO][REGISTER]", "disposable 객체 해제 전 크기 : " + disposables.size())

        // Disposable 객체 전부 해제
        if(!disposables.isDisposed){
            disposables.dispose()
        }

        executionLog("[INFO][REGISTER]", "disposable 객체 해제 후 상태 : " + disposables.isDisposed)
        executionLog("[INFO][REGISTER]", "disposable 객체 해제 후 크기 : " + disposables.size())
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}