package com.example.bookseeker.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookseeker.adapter.RatingRecvAdapter
import com.example.bookseeker.R
import com.example.bookseeker.contract.RatingContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.presenter.RatingPresenter
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import androidx.core.os.HandlerCompat.postDelayed
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.adapter.RatingAdapter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.schedulers.Schedulers


class RatingActivity : BaseActivity(), RatingContract.View {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var ratingPresenter: RatingPresenter
    //recyclerview를 담을 빈 데이터 리스트 변수를 지연 초기화
    var bookList = arrayListOf<BookData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        ratingPresenter.takeView(this)

        // RecyclerView에 평가 데이터 불러오기
        setRecyclerView(savedInstanceState)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        ratingPresenter = RatingPresenter()
    }

    // setRecyclerView : RatingActivity에서 평가할 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
    override fun setRecyclerView(savedInstanceState: Bundle?) {
        // 레이아웃 매니저 설정
        rating_recyclerview_booklist.setHasFixedSize(true)
        rating_recyclerview_booklist.layoutManager = LinearLayoutManager(this)

        //어댑터 설정
        if (rating_recyclerview_booklist.adapter == null) {
            rating_recyclerview_booklist.adapter = RatingAdapter()
        }

        if (savedInstanceState == null) {
            val subscription = ratingPresenter.getAllRomanceBookList().subscribeOn(Schedulers.io()).subscribe(
                { retrivedBookData ->
                    (rating_recyclerview_booklist.adapter as RatingAdapter).addBookList(retrivedBookData)
                },
                { e ->
                    Snackbar.make(rating_recyclerview_booklist, e.message ?: "", Snackbar.LENGTH_LONG).show()
                }
            )
        }
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
    override fun setProgressON(msg: String) {
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
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}