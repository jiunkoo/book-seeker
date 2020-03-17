package com.example.bookseeker.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.bookseeker.R
import com.example.bookseeker.contract.LoginContract
import com.example.bookseeker.model.data.Login
import com.example.bookseeker.presenter.LoginPresenter
import com.trello.rxlifecycle3.android.ActivityEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.reactivestreams.Subscription

class LoginActivity : BaseActivity(), LoginContract.View {
    // LoginActivity와 함께 생성될 LoginPresenter를 지연 초기화
    private lateinit var loginPresenter: LoginPresenter
    // Disposable 객체 지연 초기화
    private lateinit var disposables: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        println("로그인 액티비티 : [ONCREATE]")

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        loginPresenter.takeView(this)

        // Disposable 객체 지정
        disposables = CompositeDisposable()

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
            var loginData = Login(
                login_etxt_email.text.toString(),
                login_etxt_password.text.toString()
            )
            loginSubscribe(loginData)
        }
    }

    // setTextViewEventListener : LoginActivity에서 TextView Event를 처리하는 함수
    override fun setTextViewEventListener() {
        // SignUp TextView Event를 처리하는 함수
        login_txtv_register.setOnClickListener {
            startRegisterActivity()
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
        finish()
    }

    // startRegisterActivity : LoginActivity에서 SignUpActivity로 넘어가는 함수
    override fun startRegisterActivity() {
        val nextIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(nextIntent)
        finish()
    }

    // loginSubscribe : 관찰자에게서 발행된 데이터를 가져오는 함수
    fun loginSubscribe(login: Login) {
        val subscription = loginPresenter
            .loginObservable(this, login)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if((result.get("success").toString()).equals("true")){
                        showMessage("로그인에 성공하였습니다.")
                        startSearchActivity()
                    } else {
                        showMessage(result.get("message").toString())
                    }
                },
                { e ->
                    showMessage("Login Error!")
                }
            )
        disposables.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()

        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        loginPresenter.dropView()

        println("[로그인][ONDESTROY] disposable 객체 해제 전 상태 : " + disposables.isDisposed)
        println("[로그인][ONDESTROY] disposable 객체 해제 전 크기 : " + disposables.size())

        // Disposable 객체 전부 해제
        if(!disposables.isDisposed){
            disposables.dispose()
        }

        println("[로그인][ONDESTROY] disposable 객체 해제 후 상태 : " + disposables.isDisposed)
        println("[로그인][ONDESTROY] disposable 객체 해제 후 크기 : " + disposables.size())
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