package com.example.bookseeker.view.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.contract.BookInfoContract
import com.example.bookseeker.model.data.EvaluationCreate
import com.example.bookseeker.model.data.EvaluationPatch
import com.example.bookseeker.presenter.BookInfoPresenter
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bookinfo.*
import java.io.Serializable
import com.google.gson.JsonParser






class BookInfoActivity : BaseActivity(), BookInfoContract.View, Serializable {
    // Activity와 함께 생성될 Presenter를 지연 초기화
    private lateinit var bookInfoPresenter: BookInfoPresenter
    // Disposable 객체 지정
    internal val disposables = CompositeDisposable()
    // 변경 전 평점
    private var preRating = 0.0f
    // 변경 전 상태
    private var preState = -1
    // 색상
    val lightRed = "#ffc8d2"
    val lightYellow = "#ffebc8"
    val lightLime = "#dcf0dc"
    val lightMint = "#bee6eb"
    val mediumRed = "#e02947"
    val mediumYellow = "#ffbe50"
    val mediumLime = "#80c783"
    val mediumMint = "#03738c"

    // 가져올 도서 정보 객체
    private lateinit var jsonObject: JsonObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookinfo)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        bookInfoPresenter.takeView(this)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // SearchResultActivity에서 데이터 받아오기
        val intent = intent
        var bookData = intent.getStringExtra("bookData")
        jsonObject = JsonParser().parse(bookData).asJsonObject

        // 화면에 도서 정보 뿌리기
        setBookData()

        // Button Event 처리
        setButtonEventListener()

        // Rating bar Event 처리
        setRatingbarEventListener()
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

    // switchBottomNavigationView : BookInfoActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
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

    // setButtonEventListener : BookInfoActivity에서 Button Event를 처리하는 함수
    fun setButtonEventListener() {
        var link = jsonObject.get("link").toString().replace("\"", "")
        var bsin = jsonObject.get("bsin").toString().replace("\"", "")
        var genre = jsonObject.get("genre").toString().replace("\"", "")

        // BookInfo Link Button Event를 처리하는 함수
        bookinfo_btn_link.setOnClickListener {
            // 해당 도서 구매 페이지로 연결
            var combinationUri = "https://ridibooks.com" + link
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(combinationUri))
            startActivity(webIntent)
        }
        // BookInfo State Button Event를 처리하는 함수
        // '관심 없어요' 버튼
        bookinfo_btn_boring.setOnClickListener {
            var rating = bookinfo_ratingbar_bookrating.rating
            // 별점이 없는 경우 평가 생성
            if (rating == 0.0f) {
                var evaluationCreate = EvaluationCreate(bsin, genre, rating, 0)
                createEvaluationSubscribe(evaluationCreate)
            }
            // 별점이 있는 경우 평가 수정
            else {
                // 이미 해당 버튼을 선택한 경우
                if(preState == 0){
                    var evaluationPatch = EvaluationPatch(bsin, rating, -1)
                    patchEvaluationSubscribe(evaluationPatch)
                    bookinfo_btn_boring.setBackgroundColor(Color.parseColor(lightRed))
                    bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(lightYellow))
                    bookinfo_btn_reading.setBackgroundColor(Color.parseColor(lightLime))
                    bookinfo_btn_read.setBackgroundColor(Color.parseColor(lightMint))
                }
                // 해당 버튼을 선택하지 않은 경우
                else {
                    var evaluationPatch = EvaluationPatch(bsin, rating, 0)
                    patchEvaluationSubscribe(evaluationPatch)
                    bookinfo_btn_boring.setBackgroundColor(Color.parseColor(mediumRed))
                    bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(lightYellow))
                    bookinfo_btn_reading.setBackgroundColor(Color.parseColor(lightLime))
                    bookinfo_btn_read.setBackgroundColor(Color.parseColor(lightMint))
                }
            }
        }
        // '관심 있어요' 버튼
        bookinfo_btn_interesting.setOnClickListener {
            var rating = bookinfo_ratingbar_bookrating.rating
            // 별점이 없는 경우 평가 생성
            if (rating == 0.0f) {
                var evaluationCreate = EvaluationCreate(bsin, genre, rating, 1)
                createEvaluationSubscribe(evaluationCreate)
            }
            // 별점이 있는 경우 평가 수정
            else {
                // 이미 해당 버튼을 선택한 경우
                if(preState == 1){
                    var evaluationPatch = EvaluationPatch(bsin, rating, -1)
                    patchEvaluationSubscribe(evaluationPatch)
                    bookinfo_btn_boring.setBackgroundColor(Color.parseColor(lightRed))
                    bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(lightYellow))
                    bookinfo_btn_reading.setBackgroundColor(Color.parseColor(lightLime))
                    bookinfo_btn_read.setBackgroundColor(Color.parseColor(lightMint))
                }
                // 해당 버튼을 선택하지 않은 경우
                else {
                    var evaluationPatch = EvaluationPatch(bsin, rating, 1)
                    patchEvaluationSubscribe(evaluationPatch)
                    bookinfo_btn_boring.setBackgroundColor(Color.parseColor(lightRed))
                    bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(mediumYellow))
                    bookinfo_btn_reading.setBackgroundColor(Color.parseColor(lightLime))
                    bookinfo_btn_read.setBackgroundColor(Color.parseColor(lightMint))
                }
            }
        }
        // '읽고 있어요' 버튼
        bookinfo_btn_reading.setOnClickListener {
            var rating = bookinfo_ratingbar_bookrating.rating
            // 별점이 없는 경우 평가 생성
            if (rating == 0.0f) {
                var evaluationCreate = EvaluationCreate(bsin, genre, rating, 2)
                createEvaluationSubscribe(evaluationCreate)
            }
            // 별점이 있는 경우 평가 수정
            else {
                // 이미 해당 버튼을 선택한 경우
                if(preState == 2){
                    var evaluationPatch = EvaluationPatch(bsin, rating, -1)
                    patchEvaluationSubscribe(evaluationPatch)
                    bookinfo_btn_boring.setBackgroundColor(Color.parseColor(lightRed))
                    bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(lightYellow))
                    bookinfo_btn_reading.setBackgroundColor(Color.parseColor(lightLime))
                    bookinfo_btn_read.setBackgroundColor(Color.parseColor(lightMint))
                }
                // 해당 버튼을 선택하지 않은 경우
                else {
                    var evaluationPatch = EvaluationPatch(bsin, rating, 2)
                    patchEvaluationSubscribe(evaluationPatch)
                    bookinfo_btn_boring.setBackgroundColor(Color.parseColor(lightRed))
                    bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(lightYellow))
                    bookinfo_btn_reading.setBackgroundColor(Color.parseColor(mediumLime))
                    bookinfo_btn_read.setBackgroundColor(Color.parseColor(lightMint))
                }
            }
        }
        // '읽었어요' 버튼
        bookinfo_btn_read.setOnClickListener {
            // 별점이 없는 경우 평가 수정 불가
            var rating = bookinfo_ratingbar_bookrating.rating
            if (rating == 0.0f) {
                showMessage("도서 평가가 필요합니다!")
            }
            // 별점이 있는 경우 평가 수정
            else {
                // 이미 해당 버튼을 선택한 경우
                if(preState == 3){
                    var evaluationPatch = EvaluationPatch(bsin, rating, -1)
                    patchEvaluationSubscribe(evaluationPatch)
                    bookinfo_btn_boring.setBackgroundColor(Color.parseColor(lightRed))
                    bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(lightYellow))
                    bookinfo_btn_reading.setBackgroundColor(Color.parseColor(lightLime))
                    bookinfo_btn_read.setBackgroundColor(Color.parseColor(lightMint))
                }
                // 해당 버튼을 선택하지 않은 경우
                else {
                    var evaluationPatch = EvaluationPatch(bsin, rating, 3)
                    patchEvaluationSubscribe(evaluationPatch)
                    bookinfo_btn_boring.setBackgroundColor(Color.parseColor(lightRed))
                    bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(lightYellow))
                    bookinfo_btn_reading.setBackgroundColor(Color.parseColor(lightLime))
                    bookinfo_btn_read.setBackgroundColor(Color.parseColor(mediumMint))
                }
            }
        }
    }

    // setRatingbarEventListener : BookInfoActivity에서 Ratingbar Event를 처리하는 함수
    fun setRatingbarEventListener() {
        var bsin = jsonObject.get("bsin").toString().replace("\"", "")
        var genre = jsonObject.get("genre").toString().replace("\"", "")

        //Ratingbar Event를 처리하는 함수
        bookinfo_ratingbar_bookrating.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener()
        { ratingBar: RatingBar, postRating: Float, boolean: Boolean ->
            // 변경 전 평점 == 0 && 0 < 변경 후 평점 <= 5
            // 평가 데이터 생성
            if (preRating == 0.0f && (postRating > 0.0f && postRating <= 5.0f)) {
                var evaluationCreate = EvaluationCreate(bsin, genre, postRating, preState)
                createEvaluationSubscribe(evaluationCreate)
            }
            // 0 < 변경 전(후) 평점 <= 5
            // 평가 데이터 수정
            else if ((preRating > 0.0f && preRating <= 5.0f) && (postRating > 0.0f && postRating <= 5.0f)) {
                var evaluationPatch = EvaluationPatch(bsin, postRating, preState)
                patchEvaluationSubscribe(evaluationPatch)
            }
            // 0 < 변경 전 평점 <= 5 && 변경 후 평점 == 0
            // 평가 데이터 삭제
            else if ((preRating > 0.0f && preRating <= 5.0f) && postRating == 0.0f) {
                deleteEvaluationSubscribe()
            }
        }
    }

    // createEvaluationSubscribe : 하나의 평가 데이터 생성 관찰자를 구독하는 함수
    private fun createEvaluationSubscribe(evaluationCreate: EvaluationCreate) {
        val subscription =
            bookInfoPresenter.createEvaluationObservable(this, evaluationCreate)
                .subscribeOn(Schedulers.io()).subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            var jsonObject = (result.get("data")).asJsonObject
                            var rating = jsonObject.get("rating").toString().replace("\"", "").toFloat()
                            var state = jsonObject.get("state").toString().replace("\"", "").toInt()

                            // 변경된 평점 반영
                            setEvaluation(rating, state)
                        }
                        // 설정 끝낸 후 프로그래스 바 종료
                        Looper.prepare()
                        setProgressOFF()
                        showMessage(result.get("message").toString())
                        Looper.loop()
                    },
                    { e ->
                        Looper.prepare()
                        showMessage("Create evaluation error!")
                        Looper.loop()
                    }
                )
        disposables.add(subscription)
    }

    // patchEvaluationSubscribe : 하나의 평가 데이터 수정 관찰자를 구독하는 함수
    private fun patchEvaluationSubscribe(evaluationPatch: EvaluationPatch) {
        val subscription =
            bookInfoPresenter.patchEvaluationObservable(this, evaluationPatch)
                .subscribeOn(Schedulers.io()).subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            var jsonObject = (result.get("data")).asJsonObject
                            var rating = jsonObject.get("rating").toString().replace("\"", "").toFloat()
                            var state = jsonObject.get("state").toString().replace("\"", "").toInt()

                            // 변경된 평점 반영
                            setEvaluation(rating, state)
                        }
                        // 설정 끝낸 후 프로그래스 바 종료
                        Looper.prepare()
                        setProgressOFF()
                        showMessage(result.get("message").toString())
                        Looper.loop()
                    },
                    { e ->
                        Looper.prepare()
                        showMessage("Patch evaluation error!")
                        println(e)
                        Looper.loop()
                    }
                )
        disposables.add(subscription)
    }

    // deleteEvaluationSubscribe : 하나의 평가 데이터 삭제 관찰자를 구독하는 함수
    private fun deleteEvaluationSubscribe() {
        var bsin = jsonObject.get("bsin").toString().replace("\"", "")
        val subscription =
            bookInfoPresenter.deleteEvaluationObservable(this, bsin)
                .subscribeOn(Schedulers.io()).subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            // 삭제의 경우 반환값이 bsin이므로 특별한 반영 없음
                            var rating = 0.0f
                            var state = -1

                            // 변경된 평점 반영
                            setEvaluation(rating, state)
                        }
                        // 설정 끝낸 후 프로그래스 바 종료
                        Looper.prepare()
                        setProgressOFF()
                        showMessage(result.get("message").toString())
                        Looper.loop()
                    },
                    { e ->
                        Looper.prepare()
                        showMessage("Delete evaluation error!")
                        Looper.loop()
                    }
                )
        disposables.add(subscription)
    }

    // 화면에 도서 정보를 나타내는 함수
    private fun setBookData() {
        var cover = jsonObject.get("cover").toString().replace("\"", "")
        var title = jsonObject.get("title").toString().replace("\"", "")
        var author = jsonObject.get("author").toString().replace("\"", "")
        var publisher = jsonObject.get("publisher").toString().replace("\"", "")
        var publication_date = jsonObject.get("publication_date").toString().replace("\"", "")
        var introduction = jsonObject.get("introduction").toString().replace("\"", "")
        var allAverage = jsonObject.get("all_average").toString().replace("\"", "").toFloat()
        var allCount = jsonObject.get("all_count").toString().replace("\"", "").toInt()
        var myRating = jsonObject.get("my_rating").toString().replace("\"", "").toFloat()
        var myState = jsonObject.get("my_state").toString().replace("\"", "").toInt()

        var splitUrl = cover.split("/")
        var coverUrl: String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/xxlarge"
        Glide.with(this).load(coverUrl).into(bookinfo_imgv_book)
        bookinfo_txtv_booktitle.text = title
        bookinfo_txtv_author.text = author
        bookinfo_txtv_publisher.text = publisher
        bookinfo_txtv_date.text = publication_date
        bookinfo_txtv_introduction.text = introduction

        // 평가 개수 & 평균 별점 설정
        bookinfo_txtv_averagestar.text = "평균 ★ " + allAverage + " (" + allCount + "명)"

        when (myState) {
            0 -> {
                bookinfo_btn_boring.setBackgroundColor(Color.parseColor(mediumRed))
            }
            1 -> {
                bookinfo_btn_interesting.setBackgroundColor(Color.parseColor(mediumYellow))
            }
            2 -> {
                bookinfo_btn_reading.setBackgroundColor(Color.parseColor(mediumLime))
            }
            3 -> {
                bookinfo_btn_read.setBackgroundColor(Color.parseColor(mediumMint))
            }
        }

        // 평점 반영
        setEvaluation(myRating, myState)
    }

    // 화면에 도서 평점을 나타내는 함수
    private fun setEvaluation(rating: Float, state: Int) {
        bookinfo_ratingbar_bookrating.rating = rating

        preRating = rating
        preState = state
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        bookInfoPresenter.dropView()

        // 관리하고 있던 disposable 객체 전부 해제
        disposables.clear()
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