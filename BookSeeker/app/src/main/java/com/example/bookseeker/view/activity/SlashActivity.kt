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
    private val FINISH_INTERVAL_TIME: Long = 2000 //2초의 시간 간격을 둠
    private var backPressedTime: Long = 0 //뒤로가기 버튼을 두 번 누르면 종료
    private var handler: Handler? = null //슬래시 화면에서 몇 초 쉬어갈 핸들러 작성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slash)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        slashPresenter.takeView(this)

        //별도 thread로 실행하기 위해 handler 객체 생성
        handler = Handler()
        //대기 시간(2초)이 지난 뒤 LoginActivity를 실행하고 SlashActivity를 종료
        Handler().postDelayed({
            startLoginActivity() //SlashActivity 실행
            //fade-in, fade-out 효과
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, FINISH_INTERVAL_TIME)
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        slashPresenter = SlashPresenter()
    }

    // startLoginActivity : SlashActivity에서 LoginActivity로 넘어가는 함수
    override fun startLoginActivity() {
        startActivity(Intent(this@SlashActivity, RatingActivity::class.java))
        finish()
    }

    //뒤로가기 버튼을 눌렀을 때 할 행동 지정
    override fun onBackPressed() {
        //현재 시간을 long 타입 변수에 저장
        val currentTime = System.currentTimeMillis()
        //시간 간격을 long 타입 변수에 저장
        //시간 간격 = 현재 시간 - 뒤로가기 버튼을 눌렀을 때의 시간
        val intervalTime = currentTime - backPressedTime

        //시간 간격이 0보다 크고 2000과 같거나 작을 때
        //즉 2초 사이일 때 한번 더 뒤로가기 버튼을 누른 경우
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed() //현재 화면을 onDestroy 상태로 만듦

            handler!!.removeMessages(0) //핸들러 메세지를 삭제
        } else {
            //뒤로가기 버튼을 눌렀을 때의 현재 시간을 저장
            backPressedTime = currentTime
            Toast.makeText(this, "종료하시려면 한 번 더 누르세요.", Toast.LENGTH_SHORT).show()
        }//현재시간 - 뒤로가기 버튼을 누른 시간이 2초보다 큰 경우
        //즉 처음 뒤로가기 버튼을 눌렀거나, 뒤로가기 버튼을 누르고 2초 이상의 시간이 경과한 경우
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        slashPresenter.dropView()
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
        Toast.makeText(this@SlashActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}