package com.example.bookseeker.view.activity

import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.example.bookseeker.R
import com.example.bookseeker.contract.SlashContract
import com.example.bookseeker.presenter.SlashPresenter


class SlashActivity : BaseActivity(), SlashContract.View {
    // SlashActivity와 함께 생성될 SlashPresenter를 지연 초기화
    private lateinit var slashPresenter: SlashPresenter

    // 뒤로가기 버튼 이벤트 변수 및 핸들러 선언
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0
    private var handler: Handler? = null

    // onCreate : Activity가 생성될 때 동작하는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slash)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        slashPresenter.takeView(this)

        // 대기 시간 경과 후 LoginActivity 실행
        handler = Handler()
        Handler().postDelayed({
            startLoginActivity()

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, FINISH_INTERVAL_TIME)
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        slashPresenter = SlashPresenter()
    }

    // startLoginActivity : 현재 Activity에서 LoginActivity로 넘어가는 함수
    override fun startLoginActivity() {
        startActivity(Intent(this@SlashActivity, LoginActivity::class.java))
        finish()
    }

    // onBackPressed : 뒤로가기 버튼을 눌렀을 때 동작을 지정하는 함수
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        val intervalTime = currentTime - backPressedTime

        // 2초 안에 뒤로가기 버튼을 두 번 누른 경우
        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            super.onBackPressed()

            // 어플리케이션 종료
            handler!!.removeMessages(0)
        } else {
            backPressedTime = currentTime
            showMessage("종료하시려면 한 번 더 누르세요.")
        }
    }

    // onDestroy : Activity가 종료될 때 동작하는 함수
    override fun onDestroy() {
        super.onDestroy()

        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        slashPresenter.dropView()
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@SlashActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}