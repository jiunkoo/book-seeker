package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.R
import com.example.bookseeker.adapter.RecommendAdapter
import com.example.bookseeker.adapter.RecommendDelegateAdapter
import com.example.bookseeker.contract.RecommendContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.presenter.RecommendPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_recommend.*
import android.widget.LinearLayout
import com.example.bookseeker.adapter.listener.InfiniteScrollHorizonListener


class RecommendActivity : BaseActivity(), RecommendContract.View, RecommendDelegateAdapter.onViewSelectedListener {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var recommendPresenter: RecommendPresenter
    // Disposable 객체 지정
    internal val disposables = CompositeDisposable()
    // RecyclerView Adapter 설정
    private val recommendAdapter by lazy { RecommendAdapter(this) }

    // 서버에 보낼 객체 지정
    private var genre = "FANTASY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        recommendPresenter.takeView(this)

        // Recyclerview 설정
        val recyclerView = findViewById(R.id.recommend_recyclerview) as RecyclerView
        setRecyclerView(recyclerView, savedInstanceState)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        recommendPresenter = RecommendPresenter()
    }

    // switchBottomNavigationView : RecommendActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        recommend_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
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
        recommend_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_recommend)?.setChecked(true)
    }

    // setRecyclerView : 검색한 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
    fun setRecyclerView(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        // Layout Manager 설정
        recyclerView.setHasFixedSize(true)
        val horizonalLayout = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recyclerView.layoutManager = horizonalLayout

        recyclerView.clearOnScrollListeners() // 아이템 끝까지 도달되었을 때 클리어
        recyclerView.addOnScrollListener(
            InfiniteScrollHorizonListener(
                { getRecommendSubscribe(recyclerView, genre) },
                horizonalLayout
            )
        ) // 다시 갱신

        // Adapter 설정
        if (recyclerView.adapter == null) {
            recyclerView.adapter = recommendAdapter
        }

        if (savedInstanceState == null) {
            getRecommendSubscribe(recyclerView, genre)
        }
    }

    // onItemSelected : recyclerview 아이템 선택 함수
    override fun onItemSelected(bookData: BookData) {
        TODO("아이템 누르면 평가할 수 있도록 하기")
    }

    // getRecommendSubscribe : 관찰자에게서 발행된 데이터를 가져오는 함수
    private fun getRecommendSubscribe(recyclerView: RecyclerView, genre: String) {
        val subscription =
            recommendPresenter.getRecommendObservable(this, genre, recommendAdapter.itemCount / 10 + 1, 10)
                .subscribeOn(Schedulers.io()).subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            var bookDataArray = ArrayList<BookData>()
                            // 반복문을 돌려 서버에서 응답받은 데이터를 저장
                            var jsonArray = (result.get("data")).asJsonArray

                            for (i in 0 until jsonArray.size()) {
                                var jsonObject = jsonArray[i].asJsonObject
                                println(jsonObject.get("title").toString().replace("\"", ""))
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

                            // spinner가 없으므로 계속 추가
                            (recyclerView.adapter as RecommendAdapter).addBookList(bookList.results)
                        }
                        Looper.prepare()
                        this.showMessage(result.get("message").toString())
                        Looper.loop()
                    },
                    { e ->
                        Looper.prepare()
                        showMessage("Get Recommend Error!")
                        Looper.loop()
                    }
                )
        disposables.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        recommendPresenter.dropView()
    }

    // setProressON :  공통으로 사용하는 Progress Bar의 시작을 정의하는 함수
    override fun setProgressON(msg: String) {
        progressON(msg)
    }

    // setProgressOFF() : 공통으로 사용하는 Progress Bar의 종료를 정의하는 함수
    override fun setProgressOFF() {
        progressOFF()
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@RecommendActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}
