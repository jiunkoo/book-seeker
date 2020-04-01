package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.R
import com.example.bookseeker.adapter.MyEvaluationAdapter
import com.example.bookseeker.adapter.MyEvaluationDelegateAdapter
import com.example.bookseeker.adapter.listener.GridInfiniteScrollListener
import com.example.bookseeker.contract.MyEvaluationContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.presenter.MyEvaluationPresenter
import com.google.android.material.tabs.TabLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_myevaluation.*


class MyEvaluationActivity : BaseActivity(), MyEvaluationContract.View, MyEvaluationDelegateAdapter.onViewSelectedListener {
    // MyEvaluationActivity와 함께 생성될 MyEvaluationPresenter를 지연 초기화
    private lateinit var myEvaluationPresenter: MyEvaluationPresenter

    // Disposable 객체 지연 초기화
    private lateinit var disposables: CompositeDisposable

    // RecyclerView 설정
    private lateinit var recyclerView: RecyclerView

    // RecyclerView Adapter 설정
    private val myEvaluationAdapter by lazy { MyEvaluationAdapter(this) }

    // Spinner Item Change Flag 설정
    private var state = 0

    // Tab Item Change Flag 설정
    private var tabPosition = 0

    // onCreate : Activity가 생성될 때 동작하는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myevaluation)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        myEvaluationPresenter.takeView(this)

        // Disposable 객체 지정
        disposables = CompositeDisposable()

        // Spinner, Recyclerview 설정
        recyclerView = findViewById(R.id.myevaluation_recyclerview) as RecyclerView

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // Tab Layout 이벤트 처리
        setTabLayout(savedInstanceState)

        // Spinner 처리
        setSpinner(savedInstanceState)
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        myEvaluationPresenter = MyEvaluationPresenter()
    }

    // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        myevaluation_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
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
        myevaluation_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_mypage)?.setChecked(true)
    }

    // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
    override fun startBookInfoActivity(bookData: BookData) {
        val nextIntent = Intent(this, BookInfoActivity::class.java)
        nextIntent.putExtra("bsin", bookData.bsin)
        nextIntent.putExtra("genre", bookData.genre)
        nextIntent.putExtra("link", bookData.link)
        startActivity(nextIntent)
    }

    // setTabLayout : 내가 평가한 도서 목록에 대한 Tab Layout을 초기화 및 정의하는 함수
    override fun setTabLayout(savedInstanceState: Bundle?) {
        myevaluation_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                var position = tab.position

                // 이전 탭과 현재 탭이 다른 경우 변경
                if (tabPosition != position) {
                    tabPosition = position
                    (recyclerView.adapter as MyEvaluationAdapter).clearBookList()
                    getCountGenreSubscribe()
                }
                setRecyclerView(savedInstanceState)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    // setSpinner : 내가 평가한 도서 목록에 대한 Spinner를 초기화 및 정의하는 함수
    override fun setSpinner(savedInstanceState: Bundle?) {
        myevaluation_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 이전 카테고리와 현재 포지션과 다른 경우 변경
                if (state != position) {
                    state = position
                    (recyclerView.adapter as MyEvaluationAdapter).clearBookList()
                    getCountGenreSubscribe()
                }
                setRecyclerView(savedInstanceState)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // setRecyclerView : 내가 평가한 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
    override fun setRecyclerView(savedInstanceState: Bundle?) {
        // Layout Manager 설정
        recyclerView.setHasFixedSize(true)
        val gridLayout = GridLayoutManager(this, 2)
        recyclerView.layoutManager = gridLayout

        recyclerView.clearOnScrollListeners() // 아이템 끝까지 도달되었을 때 클리어
        recyclerView.addOnScrollListener(
            GridInfiniteScrollListener(
                { getEvaluationsSubscribe() },
                gridLayout
            )
        ) // 다시 갱신

        // Adapter 설정
        if (recyclerView.adapter == null) {
            recyclerView.adapter = myEvaluationAdapter
        }

        if (savedInstanceState == null) {
            getEvaluationsSubscribe()
        }
    }

    // getCountGenreSubscribe : 관찰자에게서 장르별 도서 평가 개수를 가져오는 함수
    override fun getCountGenreSubscribe() {
        val subscription =
            myEvaluationPresenter
                .getCountGenreObservable(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            val jsonObject = (result.get("data")).asJsonObject
                            val countComic = jsonObject.get("count_comic").toString().replace("\"", "")
                            val countRomance = jsonObject.get("count_romance").toString().replace("\"", "")
                            val countFantasy = jsonObject.get("count_fantasy").toString().replace("\"", "")

                            if (tabPosition == 0) {
                                myevaluation_txtv_averagecount.text = countComic
                            } else if (tabPosition == 1) {
                                myevaluation_txtv_averagecount.text = countRomance
                            } else {
                                myevaluation_txtv_averagecount.text = countFantasy
                            }
                        }
                        executionLog("[INFO][MYEVALUATION]", result.get("message").toString())
                    },
                    { e ->
                        executionLog("[ERROR][MYEVALUATION]", e.message ?: "")
                    }
                )
        disposables.add(subscription)
    }

    // getEvaluationsSubscribe : 관찰자에게서 내가 평가한 도서 목록을 가져오는 함수
    override fun getEvaluationsSubscribe() {
        // Tab Position에 따라 Genre 설정
        var genre: String
        if (tabPosition == 0) {
            genre = "COMIC"
        } else if (tabPosition == 1) {
            genre = "ROMANCE"
        } else {
            genre = "FANTASY"
        }

        val subscription =
            myEvaluationPresenter
                .getEvaluationsObservable(this, genre, state, myEvaluationAdapter.itemCount / 10 + 1, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
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
                                    jsonObject.get("introduction").toString()
                                        .replace("\"", "").replace("\\n", "\n"),
                                    jsonObject.get("cover").toString().replace("\"", ""),
                                    jsonObject.get("link").toString().replace("\"", ""),
                                    jsonObject.get("keyword").toString().replace("\"", ""),
                                    jsonObject.get("adult").toString().replace("\"", ""),
                                    jsonObject.get("genre").toString().replace("\"", ""),
                                    jsonObject.get("publication_date").toString().replace("\"", ""),
                                    jsonObject.get("rating").toString().replace("\"", "").toFloat(),
                                    -1
                                )
                                bookDataArray.add(bookData)
                            }

                            // 도서 목록 만들기
                            val bookList = BookList(myEvaluationAdapter.itemCount / 10 + 1, bookDataArray)

                            (recyclerView.adapter as MyEvaluationAdapter).addBookList(bookList.results)
                        }
                        executionLog("[INFO][MYEVALUATION]", result.get("message").toString())
                    },
                    { e ->
                        executionLog("[ERROR][MYEVALUATION]", e.message ?: "")
                    }
                )
        disposables.add(subscription)

        // 도서 개수 가져오기
        getCountGenreSubscribe()
    }

    // onItemSelected : recyclerview의 아이템 선택 이벤트를 처리하는 함수
    override fun onItemSelected(bookData: BookData) {
        // bookInfoActivity로 이동
        startBookInfoActivity(bookData)
    }

    // onDestroy : Activity가 종료될 때 동작하는 함수
    override fun onDestroy() {
        super.onDestroy()

        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        myEvaluationPresenter.dropView()

        executionLog("[INFO][MYEVALUATION]", "disposable 객체 해제 전 상태 : " + disposables.isDisposed)
        executionLog("[INFO][MYEVALUATION]", "disposable 객체 해제 전 크기 : " + disposables.size())

        // Disposable 객체 전부 해제
        if(!disposables.isDisposed){
            disposables.dispose()
        }

        executionLog("[INFO][MYEVALUATION]", "disposable 객체 해제 후 상태 : " + disposables.isDisposed)
        executionLog("[INFO][MYEVALUATION]", "disposable 객체 해제 후 크기 : " + disposables.size())
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@MyEvaluationActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}