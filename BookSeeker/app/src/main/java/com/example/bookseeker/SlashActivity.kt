package com.example.bookseeker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.content.Intent
import android.os.Handler

class SlashActivity : AppCompatActivity(){
    private val FINISH_INTERVAL_TIME: Long = 2000 //2초의 시간 간격을 둠
    private var backPressedTime: Long = 0 //뒤로가기 버튼을 두 번 누르면 종료
    private var handler: Handler? = null //슬래시 화면에서 몇 초 쉬어갈 핸들러 작성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slash)
    }

    override fun onResume() {
        super.onResume()

        //대기 시간이 지난 뒤 LoginActivity를 실행하고 MainActivity를 종료
        //별도 thread로 실행하기 위해 handler 객체 생성
        handler = Handler()
        //대기 시간이 지난 뒤 IntroActivity를 실행하고 MainActivity를 종료
        handler!!.postDelayed({
            launchLoginScreen() //IntroActivity 실행
            //fade-in, fade-out 효과.
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2000) //2초 후 run 함수를 시작하도록 설정
    }

    //로그인 화면 호출 메소드
    private fun launchLoginScreen() {
        startActivity(Intent(this@SlashActivity, LoginActivity::class.java))
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
}