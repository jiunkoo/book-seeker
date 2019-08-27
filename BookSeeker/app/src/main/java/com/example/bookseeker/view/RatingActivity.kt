package com.example.bookseeker.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookseeker.adapter.RatingRecvAdapter
import com.example.bookseeker.R
import com.example.bookseeker.contract.RatingContract
import com.example.bookseeker.item.RatingRecvItem
import com.example.bookseeker.presenter.RatingPresenter
import kotlinx.android.synthetic.main.activity_rating.*

class RatingActivity : BaseActivity(), RatingContract.View {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var ratingPresenter: RatingPresenter
    //recyclerview를 담을 빈 데이터 리스트 변수를 지연 초기화
    private lateinit var bookRatingRecvList: ArrayList<RatingRecvItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        ratingPresenter.takeView(this)

        // RecyclerView에 평가 데이터 불러오기
        setRecyclerView()

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        ratingPresenter = RatingPresenter()
    }

    // setRecyclerView : RatingActivity에서 평가할 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
    override fun setRecyclerView() {
        // 일단 급한대로 하드코딩...나중에 DB에서 긁어올 것
        bookRatingRecvList = arrayListOf<RatingRecvItem>(
            RatingRecvItem("NONE", "원피스", "오다 에이이치로", "소년 점프", 5f),
            RatingRecvItem("NONE", "나루토", "키시모토 마사시", "소년 점프", 3.5f),
            RatingRecvItem("NONE", "블리치", "쿠보 타이토", "소년 점프", 3.0f),
            RatingRecvItem("NONE", "귀멸의 칼날", "고토게 코요하루", "소년 점프", 4.5f),
            RatingRecvItem("NONE", "나의 히어로 아카데미아", "호리코시 요헤이", "소년 점프", 4.5f),
            RatingRecvItem("NONE", "드래곤볼", "토리야마 아키라", "소년 점프", 5f)
        )

        // 레이아웃 매니저 설정
        rating_recyclerview_booklist.layoutManager = LinearLayoutManager(this)
        rating_recyclerview_booklist.setHasFixedSize(true)

        //어댑터 설정
        rating_recyclerview_booklist.adapter = RatingRecvAdapter(bookRatingRecvList)
    }

    // switchBottomNavigationView : RatingActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        rating_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btmnavmenu_itm_search -> {
                    val nextIntent = Intent(baseContext, SearchActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                }
                R.id.btmnavmenu_itm_recommend -> {
                    val nextIntent = Intent(baseContext, RecommendActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                }
                R.id.btmnavmenu_itm_rating -> {
                    val nextIntent = Intent(baseContext, RatingActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                }
                R.id.btmnavmenu_itm_mypage -> {
                    val nextIntent = Intent(baseContext, MypageActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                }
            }
            false
        }
        rating_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_rating)?.setChecked(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        ratingPresenter.dropView()
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
        Toast.makeText(this@RatingActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}