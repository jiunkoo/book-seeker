package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.RatingBar
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class RatingActivity : BaseActivity(), RatingContract.View, RatingDelegateAdapter.onViewSelectedListener {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var ratingPresenter: RatingPresenter
    // Disposable 객체 지연 초기화
    private lateinit var disposables: CompositeDisposable
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

        // Disposable 객체 지정
        disposables = CompositeDisposable()

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
                    val nextIntent = Intent(baseContext, MypageActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            false
        }
        rating_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_rating)?.setChecked(true)
    }

    // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
    fun startBookInfoActivity(bookData: BookData) {
        val nextIntent = Intent(this, BookInfoActivity::class.java)
        nextIntent.putExtra("bsin", bookData.bsin)
        nextIntent.putExtra("genre", bookData.genre)
        nextIntent.putExtra("link", bookData.link)
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

    // onItemSelected : RecyclerView의 아이템이 선택된 경우
    override fun onItemSelected(bookData: BookData) {
        // bookInfoActivity로 이동
        startBookInfoActivity(bookData)
    }

    // onItemSelected : RecyclerView의 아이템 별점이 선택된 경우
    override fun onRatingBarChangeListener(ratingBar: RatingBar, float: Float, boolean: Boolean) {
        // TODO : 평점 평가하면 반영되도록 함
         showMessage("아이템 선택되었습니다! \n 평점은 " + float + "입니다.")
//        showMessage("평점은 " + float + "입니다.")
//        val postRating = float
//        // 변경 전 평점 == 0 && 0 < 변경 후 평점 <= 5
//        // 평가 데이터 생성
//        if (preRating == 0.0f && (postRating > 0.0f && postRating <= 5.0f)) {
//            var evaluationCreate = EvaluationCreate(bsin, genre, postRating, preState)
//            createEvaluationSubscribe(evaluationCreate)
//        }
//        // 0 < 변경 전(후) 평점 <= 5
//        // 평가 데이터 수정
//        else if ((preRating > 0.0f && preRating <= 5.0f) && (postRating > 0.0f && postRating <= 5.0f)) {
//            var evaluationPatch = EvaluationPatch(bsin, postRating, preState)
//            patchEvaluationSubscribe(evaluationPatch)
//        }
//        // 0 < 변경 전 평점 <= 5 && 변경 후 평점 == 0
//        // 평가 데이터 삭제
//        else if ((preRating > 0.0f && preRating <= 5.0f) && postRating == 0.0f) {
//            deleteEvaluationSubscribe()
//        }
    }

    // requestBookData : 관찰자에게서 발행된 데이터를 가져오는 함수
    private fun getBooksSubscribe(recyclerView: RecyclerView) {
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
            ratingPresenter
                .getBooksObservable(this, genre, filter, ratingAdapter.itemCount / 10 + 1, 10)
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
                                    -2f,
                                    -2
                                )
                                bookDataArray.add(bookData)
                            }

                            // 도서 목록 만들기
                            val bookList = BookList(ratingAdapter.itemCount / 10 + 1, bookDataArray)

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
                        this.showMessage(result.get("message").toString())
                    },
                    { e ->
                        Snackbar.make(recyclerView, e.message ?: "", Snackbar.LENGTH_LONG).show()
                    }
                )
        disposables.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()

        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        ratingPresenter.dropView()

        println("평가 disposable 객체 해제 전 : [ONDESTROY]" + disposables.isDisposed)

        // Disposable 객체 전부 해제
        if(!disposables.isDisposed){
            disposables.dispose()
        }

        println("평가 disposable 객체 해제 후 : [ONDESTROY]" + disposables.isDisposed)
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