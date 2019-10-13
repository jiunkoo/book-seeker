package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.bookseeker.R
import com.example.bookseeker.contract.SearchDetailContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.presenter.SearchDetailPresenter
import com.example.bookseeker.view.fragment.BookInfoFragment
import com.example.bookseeker.view.fragment.SearchResultFragment
import com.example.bookseeker.view.fragment.SearchWordFragment
import kotlinx.android.synthetic.main.activity_search_detail.*


class SearchDetailActivity : BaseActivity(), SearchDetailContract.View {
    // Activity와 함께 생성될 Presenter를 지연 초기화
    private lateinit var searchDetailPresenter: SearchDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_detail)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        searchDetailPresenter.takeView(this)

        // Button Event 처리
        setButtonEventListener()

        // Edit Text Event 처리
        setEditTextEventListener()

        // Fragment Event 처리
        val searchWordFragment = SearchWordFragment(searchDetailPresenter)
        replaceFragment(searchWordFragment)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        searchDetailPresenter = SearchDetailPresenter()
    }

    // setButtonEventListener() : SearchDetailActivity에서 Button Event를 처리하는 함수
    fun setButtonEventListener() {
        search_detail_ibtn_back.setOnClickListener {
            // 뒤로가기 버튼을 누르면 SearchActivity로 이동함
            startSearchActivity()
        }

        search_detail_ibtn_clear.setOnClickListener {
            // 클리어 버튼을 누르면 검색어 전부 삭제
            search_detail_etxt_searchword.text = null
        }
    }

    // setEditTextEventListener : EditText Event를 처리하는 함수
    override fun setEditTextEventListener() {
        // Input EditText Event를 처리하는 함수
        search_detail_etxt_searchword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (search_detail_etxt_searchword.text.toString().length == 0) {
                    showMessage("검색어를 입력해주세요.")
                } else {
                    val searchResultFragment =
                        SearchResultFragment(searchDetailPresenter, search_detail_etxt_searchword.text.toString())
                    replaceFragment(searchResultFragment)
                }
            }
            true
        }
        search_detail_etxt_searchword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (search_detail_etxt_searchword.text.toString().length == 0) {
                    // 검색이 전부 지워지면 Fragment 변경
                    val searchWordFragment = SearchWordFragment(searchDetailPresenter)
                    replaceFragment(searchWordFragment)
                }
            }
        })
    }

    override fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        var transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.search_detail_layout_frame, fragment).commitAllowingStateLoss()
    }

    // startSearchActivity : SearchActivity로 넘어가는 함수
    fun startSearchActivity() {
        val nextIntent = Intent(this, SearchActivity::class.java)
        startActivity(nextIntent)
        finish() // 이전의 Activity로 돌아가는 것이므로 현재 Activity 종료
    }

    // switchBottomNavigationView : SearchDetailActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        search_detail_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
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
        search_detail_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_search)?.setChecked(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        searchDetailPresenter.dropView()
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
        Toast.makeText(this@SearchDetailActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}