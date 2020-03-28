package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookseeker.R
import com.example.bookseeker.contract.SearchContract
import com.example.bookseeker.presenter.SearchPresenter
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : BaseActivity(), SearchContract.View {
    // SearchActivity와 함께 생성될 SearchPresenter를 지연 초기화
    private lateinit var searchPresenter: SearchPresenter

    // onCreate : Activity가 생성될 때 동작하는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        searchPresenter.takeView(this)

        // TextView Event 처리
        setTextViewEventListener()

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        searchPresenter = SearchPresenter()
    }

    // setTextViewEventListener : TextView 이벤트를 처리하는 함수
    override fun setTextViewEventListener() {
        // SignUp TextView 이벤트를 처리하는 함수
        search_etxt_search.setOnClickListener {
            startSearchDetailActivity()
        }
    }

    // startSearchDetailActivity : SearchDetailActivity로 넘어가는 함수
    override fun startSearchDetailActivity() {
        val nextIntent = Intent(this, SearchDetailActivity::class.java)
        nextIntent.putExtra("keyword", "")
        startActivity(nextIntent)
    }

    // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        search_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btmnavmenu_itm_search -> {
                    val nextIntent = Intent(baseContext, SearchActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.btmnavmenu_itm_recommend -> {
                    val nextIntent = Intent(baseContext, RecommendActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.btmnavmenu_itm_rating -> {
                    val nextIntent = Intent(baseContext, RatingActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.btmnavmenu_itm_mypage -> {
                    val nextIntent = Intent(baseContext, MyPageActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            false
        }
        search_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_search)?.setChecked(true)
    }

    // onDestroy : Activity가 종료될 때 동작하는 함수
    override fun onDestroy() {
        super.onDestroy()

        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        searchPresenter.dropView()
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@SearchActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}