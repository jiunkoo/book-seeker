//package com.example.bookseeker.view.activity
//
//import android.content.Intent
//import android.graphics.Color
//import android.os.Bundle
//import android.os.Looper
//import android.util.Log
//import android.widget.Toast
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.bookseeker.R
//import com.example.bookseeker.adapter.RecommendAdapter
//import com.example.bookseeker.adapter.RecommendDelegateAdapter
//import com.example.bookseeker.contract.RecommendContract
//import com.example.bookseeker.presenter.RecommendPresenter
//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.activity_recommend.*
//import android.widget.LinearLayout
//import com.example.bookseeker.adapter.listener.InfiniteScrollHorizonListener
//import com.example.bookseeker.model.data.*
//import com.google.gson.JsonObject
//import com.trello.rxlifecycle3.android.ActivityEvent
//import io.reactivex.android.schedulers.AndroidSchedulers
//
//
//class RecommendActivity : BaseActivity(), RecommendContract.View, RecommendDelegateAdapter.onViewSelectedListener {
//    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
//    private lateinit var recommendPresenter: RecommendPresenter
//    // Disposable 객체 지연 초기화
//    private lateinit var disposables: CompositeDisposable
//    // RecyclerView 설정
//    private lateinit var recyclerView: RecyclerView
//    // RecyclerView Adapter 설정
//    private val recommendAdapter by lazy { RecommendAdapter(this) }
//    // 서버에 보낼 객체 지정
//    private var genre = "COMIC"
//    // Category Change Flag 설정
//    private var categoryFlag = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_recommend)
//
//        println("추천 액티비티 : [ONCREATE]")
//
//        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
//        recommendPresenter.takeView(this)
//
//        // Disposable 객체 지정
//        disposables = CompositeDisposable()
//
//        // Recyclerview 설정
//        recyclerView = findViewById(R.id.recommend_recyclerview) as RecyclerView
//        setRecyclerView(savedInstanceState)
//
//        // Category 이벤트 처리
//        setCategoryEventListener(savedInstanceState)
//
//        // BottomNavigationView 이벤트 처리
//        switchBottomNavigationView()
//    }
//
//    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
//    override fun initPresenter() {
//        recommendPresenter = RecommendPresenter()
//    }
//
//    // switchBottomNavigationView : RecommendActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
//    override fun switchBottomNavigationView() {
//        recommend_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.btmnavmenu_itm_search -> {
//                    val nextIntent = Intent(baseContext, SearchActivity::class.java)
//                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(nextIntent)
//                    overridePendingTransition(0, 0)
//                    finish()
//                }
//                R.id.btmnavmenu_itm_recommend -> {
//                    val nextIntent = Intent(baseContext, RecommendActivity::class.java)
//                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(nextIntent)
//                    overridePendingTransition(0, 0)
//                    finish()
//                }
//                R.id.btmnavmenu_itm_rating -> {
//                    val nextIntent = Intent(baseContext, RatingActivity::class.java)
//                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(nextIntent)
//                    overridePendingTransition(0, 0)
//                    finish()
//                }
//                R.id.btmnavmenu_itm_mypage -> {
//                    val nextIntent = Intent(baseContext, MypageActivity::class.java)
//                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(nextIntent)
//                    overridePendingTransition(0, 0)
//                    finish()
//                }
//            }
//            false
//        }
//        recommend_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_recommend)?.setChecked(true)
//    }
//
//    // setEditTextEventListener : EditText Event를 처리하는 함수
//    fun setCategoryEventListener(savedInstanceState: Bundle?) {
//        // Comic Category Event를 처리하는 함수
//        recommend_layout_linear_comic.setOnClickListener {
//            if (genre == "COMIC") {
//                categoryFlag = false
//            } else {
//                genre = "COMIC"
//                categoryFlag = true
//                setRecyclerView(savedInstanceState)
//            }
//        }
//        // Romance Category Event를 처리하는 함수
//        recommend_layout_linear_romance.setOnClickListener {
//            if (genre == "ROMANCE") {
//                categoryFlag = false
//            } else {
//                genre = "ROMANCE"
//                categoryFlag = true
//                setRecyclerView(savedInstanceState)
//            }
//        }
//        // Fantasy Category Event를 처리하는 함수
//        recommend_layout_linear_fantasy.setOnClickListener {
//            if (genre == "FANTASY") {
//                categoryFlag = false
//            } else {
//                genre = "FANTASY"
//                categoryFlag = true
//                setRecyclerView(savedInstanceState)
//            }
//        }
//    }
//
//    // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
//    fun startBookInfoActivity(jsonObject: JsonObject) {
//        val nextIntent = Intent(this, BookInfoActivity::class.java)
//        nextIntent.putExtra("bookData", jsonObject.toString())
//        startActivity(nextIntent)
//    }
//
//    // setRecyclerView : 검색한 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
//    fun setRecyclerView(savedInstanceState: Bundle?) {
//        // Layout Manager 설정
//        recyclerView.setHasFixedSize(true)
//        val horizonalLayout = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
//        recyclerView.layoutManager = horizonalLayout
//
//        recyclerView.clearOnScrollListeners() // 아이템 끝까지 도달되었을 때 클리어
//        recyclerView.addOnScrollListener(
//            InfiniteScrollHorizonListener(
//                { getRecommendSubscribe() },
//                horizonalLayout
//            )
//        ) // 다시 갱신
//
//        // Adapter 설정
//        if (recyclerView.adapter == null) {
//            recyclerView.adapter = recommendAdapter
//        }
//
//        if (savedInstanceState == null) {
//            getRecommendSubscribe()
//        }
//    }
//
//    // onItemSelected : recyclerview 아이템 선택 함수(짧게 누름)
//    override fun onItemSelected(bookData: BookData, position: Int) {
//        // 해당 도서 데이터 가져오기
//        getBookSubscribe(bookData)
//    }
//
//    /*
//    // onItemSelected : recyclerview 아이템 선택 함수(길게 누름)
//    override fun onItemLongSelected(view: View, bookData: BookData) {
////        // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
////        val recommendDialog = RecommendDialog(this)
////
////        // 커스텀 다이얼로그를 호출한다.
////        recommendDialog.callFunction()
//
//        val info = arrayOf<CharSequence>("관심 없어요", "관심 없어요")
//        val builder = AlertDialog.Builder(this)
//        builder.setItems(info) { dialog, which ->
//            when (which) {
//                0 ->
//                    // 내정보
//                    Toast.makeText(this, "관심 없어요", Toast.LENGTH_SHORT).show()
//                1 ->
//                    // 로그아웃
//                    Toast.makeText(this, "관심 있어요", Toast.LENGTH_SHORT).show()
//            }
//            dialog.dismiss()
//        }
//        builder.show()
//    }
//    */
//
//    override fun boringIbtnSelected(bookData: BookData, position: Int) {
//        var evaluationCreate = EvaluationCreate(bookData.bsin, genre, 0.0f, 0)
//        createEvaluationSubscribe(bookData, position, evaluationCreate)
//    }
//
//    override fun interestingIbtnSelected(bookData: BookData, position: Int) {
//        var evaluationCreate = EvaluationCreate(bookData.bsin, genre, 0.0f, 1)
//        createEvaluationSubscribe(bookData, position, evaluationCreate)
//    }
//
//    // getRecommendSubscribe : 관찰자에게서 발행된 데이터를 가져오는 함수
//    private fun getRecommendSubscribe() {
////        setProgressON("추천을 진행중입니다...")
//
//        val subscription =
//            recommendPresenter
//                .getRecommendObservable(this, genre, recommendAdapter.itemCount / 10 + 1, 10)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
////                        setProgressOFF()
//
//                        if ((result.get("success").toString()).equals("true")) {
//                            var bookDataArray = ArrayList<BookData>()
//                            // 반복문을 돌려 서버에서 응답받은 데이터를 저장
//                            var jsonArray = (result.get("data")).asJsonArray
//
//                            for (i in 0 until jsonArray.size()) {
//                                var jsonObject = jsonArray[i].asJsonObject
//                                println(jsonObject.get("title").toString().replace("\"", ""))
//                                // 데이터 가공 처리(큰따옴표 제거)
//                                var bookData = BookData(
//                                    jsonObject.get("bsin").toString().replace("\"", ""),
//                                    jsonObject.get("title").toString().replace("\"", ""),
//                                    jsonObject.get("author").toString().replace("\"", ""),
//                                    jsonObject.get("publisher").toString().replace("\"", ""),
//                                    jsonObject.get("introduction").toString(),
//                                    jsonObject.get("cover").toString().replace("\"", ""),
//                                    jsonObject.get("link").toString().replace("\"", ""),
//                                    jsonObject.get("keyword").toString().replace("\"", ""),
//                                    jsonObject.get("adult").toString().replace("\"", ""),
//                                    jsonObject.get("genre").toString().replace("\"", ""),
//                                    jsonObject.get("publication_date").toString().replace("\"", ""),
//                                    jsonObject.get("expect_rating").toString().replace("\"", "").toFloat(),
//                                    jsonObject.get("state").toString().replace("\"", "").toInt()
//                                )
//                                bookDataArray.add(bookData)
//                            }
//                            // 도서 목록 만들기
//                            val bookList = BookList(bookDataArray)
//
//                            // Category가 변경된 경우
//                            if (categoryFlag == true) {
//                                (recyclerView.adapter as RecommendAdapter).clearAndAddBookList(bookList.results)
//                                categoryFlag = false
//                            } else {
//                                (recyclerView.adapter as RecommendAdapter).addBookList(bookList.results)
//                            }
//                        }
//                        this.showMessage(result.get("message").toString())
//                    },
//                    { e ->
//                        showMessage("Get Recommend Error!")
//                    }
//                )
//        disposables.add(subscription)
//    }
//
//    // createEvaluationSubscribe : 하나의 평가 데이터 생성 관찰자를 구독하는 함수
//    private fun createEvaluationSubscribe(bookData: BookData, position: Int, evaluationCreate: EvaluationCreate) {
////        setProgressON("평가를 진행중입니다...")
//
//        val subscription =
//            recommendPresenter
//                .createEvaluationObservable(this, evaluationCreate)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
////                        setProgressOFF()
//                        if ((result.get("success").toString()).equals("true")) {
//                            var jsonObject = (result.get("data")).asJsonObject
//                            var state = jsonObject.get("state").toString().replace("\"", "").toInt()
//
//                            bookData.state = state
//
//                            (recyclerView.adapter as RecommendAdapter).modifyBookList(bookData, position)
//                        }
//                        // 설정 끝낸 후 프로그래스 바 종료
//                        showMessage(result.get("message").toString())
//                    },
//                    { e ->
//                        showMessage("Create evaluation error!")
//                    }
//                )
//        disposables.add(subscription)
//    }
//
//    // getBookSubscribe : 하나의 평가 데이터 조회 관찰자를 구독하는 함수
//    private fun getBookSubscribe(bookData: BookData) {
//        val subscription =
//            recommendPresenter
//                .getBookObservable(this, bookData.bsin)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
//                        if ((result.get("success").toString()).equals("true")) {
//                            // 서버에서 응답받은 데이터를 가져옴
//                            var jsonArray = (result.get("data")).asJsonArray
//                            var jsonObject = jsonArray[0].asJsonObject
//
//                            showMessage(result.get("message").toString())
//                            startBookInfoActivity(jsonObject)
//                        } else {
//                            showMessage(result.get("message").toString())
//                        }
//                    },
//                    { e ->
//                        showMessage("Get evaluation error!")
//                        println(e)
//                    }
//                )
//        disposables.add(subscription)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
//        recommendPresenter.dropView()
//
//        println("[추천][ONDESTROY] disposable 객체 해제 전 상태 : " + disposables.isDisposed)
//        println("[추천][ONDESTROY] disposable 객체 해제 전 크기 : " + disposables.size())
//
//        // Disposable 객체 전부 해제
//        if(!disposables.isDisposed){
//            disposables.dispose()
//        }
//
//        println("[추천][ONDESTROY] disposable 객체 해제 후 상태 : " + disposables.isDisposed)
//        println("[추천][ONDESTROY] disposable 객체 해제 후 크기 : " + disposables.size())
//    }
//
//    // setProressON :  공통으로 사용하는 Progress Bar의 시작을 정의하는 함수
//    override fun setProgressON(msg: String) {
//        progressON(msg)
//    }
//
//    // setProgressOFF() : 공통으로 사용하는 Progress Bar의 종료를 정의하는 함수
//    override fun setProgressOFF() {
//        progressOFF()
//    }
//
//    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
//    override fun showMessage(msg: String) {
//        Toast.makeText(this@RecommendActivity, msg, Toast.LENGTH_SHORT).show()
//    }
//
//    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
//    override fun executionLog(tag: String, msg: String) {
//        Log.e(tag, msg)
//    }
//}
