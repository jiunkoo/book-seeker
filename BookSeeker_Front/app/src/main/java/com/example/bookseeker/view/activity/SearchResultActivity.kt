package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.R
import com.example.bookseeker.adapter.SearchAdapter
import com.example.bookseeker.adapter.SearchDelegateAdapter
import com.example.bookseeker.adapter.listener.InfiniteScrollListener
import com.example.bookseeker.contract.SearchResultContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.model.data.BooksSearch
import com.example.bookseeker.presenter.SearchResultPresenter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_result.*


class SearchResultActivity : BaseActivity(), SearchResultContract.View, SearchDelegateAdapter.onViewSelectedListener {
    // Activity와 함께 생성될 Presenter를 지연 초기화
    private lateinit var searchResultPresenter: SearchResultPresenter
    // Disposable 객체 지정
    internal val disposables = CompositeDisposable()
    // RecyclerView Adapter 설정
    private val searchAdapter by lazy { SearchAdapter(this) }
    // Spinner Item Change Flag 설정
    private var filter = 0
    private var spinnerFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        searchResultPresenter.takeView(this)

        // SearchDetailActivity에서 데이터 받아오기
        val intent = intent
        val keyword = intent.extras!!.get("keyword").toString()

        // Spinner, Recyclerview 설정
        val spinner = findViewById(R.id.search_result_spinner) as Spinner
        val recyclerView = findViewById(R.id.search_result_recyclerview) as RecyclerView

        // 검색창에 keyword 넣기
        search_result_etxt_keyword.setText(keyword)
        search_result_etxt_keyword.setSelection(keyword.length)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // Button Event 처리
        setButtonEventListener()

        // Edit Text Event 처리
        setEditTextEventListener(keyword)

        // spinner 처리
        setSpinner(keyword, spinner, recyclerView, savedInstanceState)
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        searchResultPresenter = SearchResultPresenter()
    }

    // switchBottomNavigationView : SearchDetailActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        search_result_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
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
        search_result_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_search)?.setChecked(true)
    }

    // setButtonEventListener() : SearchDetailActivity에서 Button Event를 처리하는 함수
    fun setButtonEventListener() {
        search_result_ibtn_back.setOnClickListener {
            // 뒤로가기 버튼을 누르면 SearchDetailActivity로 이동함
            startSearchDetailActivity()
        }

        search_result_ibtn_clear.setOnClickListener {
            // 클리어 버튼을 누르면 검색어 전부 삭제
            search_result_etxt_keyword.text = null
        }
    }

    // setEditTextEventListener : EditText Event를 처리하는 함수
    fun setEditTextEventListener(keyword: String) {
        search_result_etxt_keyword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                // 검색어가 변경된 경우
                if(!keyword.equals(search_result_etxt_keyword.text.toString())){
                    // SearchDetailActiviy로 돌아감
                    startSearchDetailActivity()
                }
            }
        })
    }

    // startSearchDetailActivity : SearchDetailActivity로 넘어가는 함수
    fun startSearchDetailActivity() {
        val nextIntent = Intent(this, SearchDetailActivity::class.java)
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        nextIntent.putExtra("keyword", search_result_etxt_keyword.text.toString())
        startActivity(nextIntent)
        overridePendingTransition(0, 0)
        finish() // 이전의 Activity로 돌아가는 것이므로 현재 Activity 종료
    }

    // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
    fun startBookInfoActivity(jsonObject: JsonObject) {
        val nextIntent = Intent(this, BookInfoActivity::class.java)
        nextIntent.putExtra("bookData", jsonObject.toString())
        startActivity(nextIntent)
    }

    // setSpinner : 검색한 도서 목록에 대한 Spinner를 초기화 및 정의하는 함수
    fun setSpinner(keyword: String, spinner: Spinner, recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 이전 카테고리와 현재 포지션과 다른 경우 변경
                if (filter != position) {
                    filter = position
                    spinnerFlag = true
                } else {
                    spinnerFlag = false
                }
                setRecyclerView(keyword, recyclerView, savedInstanceState)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // setRecyclerView : 검색한 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
    fun setRecyclerView(keyword: String, recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        // Layout Manager 설정
        recyclerView.setHasFixedSize(true)
        val linearLayout = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayout

        recyclerView.clearOnScrollListeners() // 아이템 끝까지 도달되었을 때 클리어
        recyclerView.addOnScrollListener(
            InfiniteScrollListener(
                { booksSearchSubscribe(keyword, recyclerView) },
                linearLayout
            )
        ) // 다시 갱신

        // Adapter 설정
        if (recyclerView.adapter == null) {
            recyclerView.adapter = searchAdapter
        }

        if (savedInstanceState == null) {
            booksSearchSubscribe(keyword, recyclerView)
        }
    }

    override fun onItemSelected(bookData: BookData) {
        // 해당 도서 데이터 가져오기
        getEvaluationSubscribe(bookData)
    }

    // booksSearchSubscribe : 관찰자에게서 발행된 데이터를 가져오는 함수
    private fun booksSearchSubscribe(keyword: String, recyclerView: RecyclerView) {
        var searchRequest = BooksSearch(keyword)
        val subscription =
            searchResultPresenter.booksSearchObservable(this, searchRequest, filter, searchAdapter.itemCount / 10 + 1, 10)
                .subscribeOn(Schedulers.io()).subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            // 반복문을 돌려 서버에서 응답받은 데이터를 recyclerview에 저장
                            var bookDataArray = ArrayList<BookData>()
                            var jsonArray = (result.get("data")).asJsonArray

                            for (i in 0 until jsonArray.size()) {
                                var jsonObject = jsonArray[i].asJsonObject
                                // 데이터 가공 처리(큰따옴표 제거)
                                var bookData = BookData(
                                    jsonObject.get("bsin").toString().replace("\"", ""),
                                    jsonObject.get("title").toString().replace("\"", ""),
                                    jsonObject.get("author").toString().replace("\"", ""),
                                    jsonObject.get("publisher").toString().replace("\"", ""),
                                    jsonObject.get("introduction").toString().replace("\"", ""),
                                    jsonObject.get("cover").toString().replace("\"", ""),
                                    jsonObject.get("link").toString().replace("\"", ""),
                                    jsonObject.get("keyword").toString().replace("\"", ""),
                                    jsonObject.get("adult").toString().replace("\"", ""),
                                    jsonObject.get("genre").toString().replace("\"", ""),
                                    jsonObject.get("publication_date").toString().replace("\"", ""),
                                    0.0f
                                )
                                bookDataArray.add(bookData)
                            }

                            // 도서 목록 만들기
                            val bookList = BookList(bookDataArray)

                            if (spinnerFlag == true) {
                                (recyclerView.adapter as SearchAdapter).clearAndAddBookList(bookList.results)
                                spinnerFlag = false
                            } else {
                                (recyclerView.adapter as SearchAdapter).addBookList(bookList.results)
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
        disposables.add(subscription)
    }

    // getEvaluationSubscribe : 하나의 평가 데이터 조회 관찰자를 구독하는 함수
    private fun getEvaluationSubscribe(bookData: BookData) {
        val subscription =
            searchResultPresenter.getEvaluationObservable(this, bookData.bsin)
                .subscribeOn(Schedulers.io()).subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            // 서버에서 응답받은 데이터를 가져옴
                            var jsonObject = (result.get("data")).asJsonObject

                            Looper.prepare()
                            setProgressOFF()
                            showMessage(result.get("message").toString())
                            startBookInfoActivity(jsonObject)
                            Looper.loop()
                        } else {
                            Looper.prepare()
                            setProgressOFF()
                            showMessage(result.get("message").toString())
                            Looper.loop()
                        }
                    },
                    { e ->
                        Looper.prepare()
                        showMessage("Get evaluation error!")
                        println(e)
                        Looper.loop()
                    }
                )
        disposables.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        searchResultPresenter.dropView()
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
        Toast.makeText(this@SearchResultActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}