package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.contract.BookInfoContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.presenter.BookInfoPresenter
import kotlinx.android.synthetic.main.activity_bookinfo.*
import java.io.Serializable


class BookInfoActivity : BaseActivity(), BookInfoContract.View, Serializable {
    // Activity와 함께 생성될 Presenter를 지연 초기화
    private lateinit var bookInfoPresenter: BookInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookinfo)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        bookInfoPresenter.takeView(this)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // SearchResultActivity에서 데이터 받아오기
        val intent = intent
        val bookData = intent.getParcelableExtra("bookData") as BookData

        setBookData(bookData)
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        bookInfoPresenter = BookInfoPresenter()
    }

    // startSearchResultActivity : SearchResultActivity로 넘어가는 함수
    fun startSearchResultActivity() {
        val nextIntent = Intent(this, SearchResultActivity::class.java)
        startActivity(nextIntent)
        overridePendingTransition(0, 0)
        finish() // 이전의 Activity로 돌아가는 것이므로 현재 Activity 종료
    }

    // switchBottomNavigationView : SearchDetailActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        bookinfo_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
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
        bookinfo_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_search)?.setChecked(true)
    }

    fun setBookData(bookData: BookData){
        var splitUrl = bookData.cover.split("/")
        var coverUrl:String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/xxlarge"

        Glide.with(this).load(coverUrl).into(bookinfo_imgv_book)
        bookinfo_txtv_booktitle.text = bookData.title
        bookinfo_txtv_author.text = bookData.author
        bookinfo_txtv_publisher.text = bookData.publisher
        bookinfo_txtv_date.text = bookData.publication_date
        bookinfo_txtv_introduction.text = bookData.introduction
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        bookInfoPresenter.dropView()
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
        Toast.makeText(this@BookInfoActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}