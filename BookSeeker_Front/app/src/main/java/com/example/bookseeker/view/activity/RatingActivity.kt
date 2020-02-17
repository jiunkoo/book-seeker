package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.R
import com.example.bookseeker.adapter.RatingAdapter
import com.example.bookseeker.adapter.RatingDelegateAdapter
import com.example.bookseeker.adapter.listener.InfiniteScrollListener
import com.example.bookseeker.contract.RatingContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.presenter.RatingPresenter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_rating.*
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class RatingActivity : BaseActivity(), RatingContract.View, RatingDelegateAdapter.onViewSelectedListener {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var ratingPresenter: RatingPresenter
    // Disposable 객체 지정
    private var subscriptions = CompositeDisposable()
    // RecyclerView Adapter 설정
    private val ratingAdapter by lazy { RatingAdapter(this) }
    // Spinner Item Change Flag 설정
    private var filter = 0
    private var spinnerFlag = false
    // Tab Item Change Flag 설정
    private var tabPosition = 0
    private var tabFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        ratingPresenter.takeView(this)

        // Spinner, Recyclerview 설정
        val recyclerView = findViewById(R.id.rating_recyclerview) as RecyclerView

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // Tab Layout 이벤트 처리
        setTabLayout(recyclerView, savedInstanceState)

        // Spinner 처리
        setSpinner(recyclerView, savedInstanceState)
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        ratingPresenter = RatingPresenter()
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

    // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
    fun startBookInfoActivity(bookData: BookData) {
        val nextIntent = Intent(this, BookInfoActivity::class.java)
        nextIntent.putExtra("bookData", bookData)
        startActivity(nextIntent)
    }

    // setTabLayout : RatingActivity에서 Tab Layout Event를 처리하는 함수
    fun setTabLayout(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        rating_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                var position = tab.position

                // 이전 탭과 현재 탭이 다른 경우 변경
                if (tabPosition != position) {
                    tabPosition = position
                    tabFlag = true
                } else {
                    tabFlag = false
                }
                setRecyclerView(recyclerView, savedInstanceState)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    // setSpinner : 평가할 도서 목록에 대한 Spinner를 초기화 및 정의하는 함수
    fun setSpinner(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        rating_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 이전 카테고리와 현재 포지션과 다른 경우 변경
                if (filter != position) {
                    filter = position
                    spinnerFlag = true
                } else {
                    spinnerFlag = false
                }
                setRecyclerView(recyclerView, savedInstanceState)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // setRecyclerView : 검색한 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
    fun setRecyclerView(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        // Layout Manager 설정
        recyclerView.setHasFixedSize(true)
        val linearLayout = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayout

        recyclerView.clearOnScrollListeners() // 아이템 끝까지 도달되었을 때 클리어
        recyclerView.addOnScrollListener(
            InfiniteScrollListener(
                { getBooksSubscribe(recyclerView) },
                linearLayout
            )
        ) // 다시 갱신

        // Adapter 설정
        if (recyclerView.adapter == null) {
            recyclerView.adapter = ratingAdapter
        }

        if (savedInstanceState == null) {
            getBooksSubscribe(recyclerView)
        }
    }

    // requestBookData : 관찰자에게서 발행된 데이터를 가져오는 함수
    private fun getBooksSubscribe(recyclerView: RecyclerView) {
        // Tab Position에 따라 Genre 설정
        var genre: String
        if (tabPosition == 0) {
            genre = "COMIC"
        } else if (tabPosition == 1) {
            genre = "FANTASY"
        } else {
            genre = "ROMANCE"
        }

        val subscription =
            ratingPresenter.getBooksObservable(this, genre, filter, ratingAdapter.itemCount / 10 + 1, 10)
                .subscribeOn(Schedulers.io()).subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            // 반복문을 돌려 서버에서 응답받은 데이터를 recyclerview에 저장
                            var bookDataArray = ArrayList<BookData>()
                            var jsonArray = (result.get("data")).asJsonArray

                            for (i in 0 until jsonArray.size()) {
                                var jsonObject = jsonArray[i].asJsonObject
                                var bookData = BookData(
                                    jsonObject.get("bsin").toString(),
                                    jsonObject.get("title").toString(),
                                    jsonObject.get("author").toString(),
                                    jsonObject.get("publisher").toString(),
                                    jsonObject.get("introduction").toString(),
                                    jsonObject.get("cover").toString(),
                                    jsonObject.get("link").toString(),
                                    jsonObject.get("keyword").toString(),
                                    jsonObject.get("adult").toString(),
                                    jsonObject.get("genre").toString(),
                                    jsonObject.get("publication_date").toString(),
                                    0.0f
                                )
                                bookDataArray.add(bookData)
                            }

                            // 도서 목록 만들기
                            val bookList = BookList(bookDataArray)

                            // TabLayout이 변경된 경우
                            if (tabFlag == true) {
                                (recyclerView.adapter as RatingAdapter).clearAndAddBookList(bookList.results)
                                tabFlag = false
                            } else {
                                (recyclerView.adapter as RatingAdapter).addBookList(bookList.results)
                            }

                            // Spinner가 변경된 경우
                            if (spinnerFlag == true) {
                                (recyclerView.adapter as RatingAdapter).clearAndAddBookList(bookList.results)
                                spinnerFlag = false
                            } else {
                                (recyclerView.adapter as RatingAdapter).addBookList(bookList.results)
                            }
                        }
                        Looper.prepare()
                        this.showMessage(result.get("message").toString())
                        Looper.loop()
                    },
                    { e ->
                        Snackbar.make(recyclerView, e.message ?: "", Snackbar.LENGTH_LONG).show()
                    }
                )
        subscriptions.add(subscription)
    }

    // onItemSelected : RecyclerView의 아이템이 선택된 경우
    override fun onItemSelected(bookData: BookData) {
        // bookInfoActivity로 이동
        startBookInfoActivity(bookData)
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