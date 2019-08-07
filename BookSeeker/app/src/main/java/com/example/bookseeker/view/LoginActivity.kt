package com.example.bookseeker.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
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

        // 로그인 버튼 이벤트 처리
        setLoginButton()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        loginPresenter = LoginPresenter()
    }

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
    override fun startSearchActivity() {
        val nextIntent = Intent(this, SearchActivity::class.java)
        startActivity(nextIntent)
    }

    override fun onDestroy() {
        super.onDestroy()

        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        loginPresenter.dropView()
    }

    // showError : 공통으로 쓰이는 error 출력 부분을 생성하는 함수
    override fun showError(error: String) {
        println("LoginActivity에서 Error가 발생하였습니다.")
        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_SHORT).show()
    }
}