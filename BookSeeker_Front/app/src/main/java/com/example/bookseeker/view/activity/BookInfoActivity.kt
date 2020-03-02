package com.example.bookseeker.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.contract.BookInfoContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.EvaluationCreate
import com.example.bookseeker.model.data.EvaluationPatch
import com.example.bookseeker.presenter.BookInfoPresenter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bookinfo.*
import java.io.Serializable


class BookInfoActivity : BaseActivity(), BookInfoContract.View, Serializable {
    // Activity와 함께 생성될 Presenter를 지연 초기화
    private lateinit var bookInfoPresenter: BookInfoPresenter
    // Disposable 객체 지정
    private var subscriptions = CompositeDisposable()
    // 변경 전 평점
    private var preRating = 0.0f
    // 변경 전 상태
    private var preState = -1

    // 가져올 도서 정보
    private lateinit var bookData: BookData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookinfo)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        bookInfoPresenter.takeView(this)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // SearchResultActivity에서 데이터 받아오기
        val intent = intent
        bookData = intent.getParcelableExtra("bookData") as BookData

        // 화면에 도서 정보 뿌리기
        setBookData()

        // Button Event 처리
        setButtonEventListener()

        // Rating bar Event 처리
        setRatingbarEventListener()

        // 평균 별점 및 평가 개수
        getEvaluationSubscribe()
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
        // BookInfo Link Button Event를 처리하는 함수
        bookinfo_btn_link.setOnClickListener {
            // 해당 도서 구매 페이지로 연결
            var combinationUri = "https://ridibooks.com" + bookData.link
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(combinationUri))
            startActivity(webIntent)
        }
        // BookInfo State Button Event를 처리하는 함수
        // '관심 없어요' 버튼
        bookinfo_btn_boring.setOnClickListener {
            var rating = bookinfo_ratingbar_bookrating.rating
            // 별점이 없는 경우 평가 생성
            if (rating == 0.0f) {
                var evaluationCreate = EvaluationCreate(bookData.bsin, bookData.genre, rating, 0)
                createEvaluationSubscribe(evaluationCreate)
            }
            // 별점이 있는 경우 평가 수정
            else {
                var evaluationPatch = EvaluationPatch(bookData.bsin, rating, 0)
                patchEvaluationSubscribe(evaluationPatch)
            }
        }
        // '관심 있어요' 버튼
        bookinfo_btn_interesting.setOnClickListener {
            var rating = bookinfo_ratingbar_bookrating.rating
            // 별점이 없는 경우 평가 생성
            if (rating == 0.0f) {
                var evaluationCreate = EvaluationCreate(bookData.bsin, bookData.genre, rating, 1)
                createEvaluationSubscribe(evaluationCreate)
            }
            // 별점이 있는 경우 평가 수정
            else {
                var evaluationPatch = EvaluationPatch(bookData.bsin, rating, 1)
                patchEvaluationSubscribe(evaluationPatch)
            }
        }
        // '읽고 있어요' 버튼
        bookinfo_btn_reading.setOnClickListener {
            var rating = bookinfo_ratingbar_bookrating.rating
            // 별점이 없는 경우 평가 생성
            if (rating == 0.0f) {
                var evaluationCreate = EvaluationCreate(bookData.bsin, bookData.genre, rating, 2)
                createEvaluationSubscribe(evaluationCreate)
            }
            // 별점이 있는 경우 평가 수정
            else {
                var evaluationPatch = EvaluationPatch(bookData.bsin, rating, 2)
                patchEvaluationSubscribe(evaluationPatch)
            }
        }
        // '읽었어요' 버튼
        bookinfo_btn_read.setOnClickListener {
            // 별점이 없는 경우 평가 수정 불가
            var rating = bookinfo_ratingbar_bookrating.rating
            if(rating == 0.0f){
                showMessage("도서 평가가 필요합니다!")
            }
            // 별점이 있는 경우 평가 수정
            else {
                var evaluationPatch = EvaluationPatch(bookData.bsin, rating, 3)
                patchEvaluationSubscribe(evaluationPatch)
            }
        }
    }

    // setRatingbarEventListener : BookInfoActivity에서 Ratingbar Event를 처리하는 함수
    fun setRatingbarEventListener() {
        //Ratingbar Event를 처리하는 함수
        bookinfo_ratingbar_bookrating.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener()
        { ratingBar: RatingBar, postRating: Float, boolean: Boolean ->
            // 변경 전 평점 == 0 && 0 < 변경 후 평점 <= 5
            // 평가 데이터 생성
            if (preRating == 0.0f && (postRating > 0.0f && postRating <= 5.0f)) {
                var evaluationCreate = EvaluationCreate(bookData.bsin, bookData.genre, postRating, preState)
                createEvaluationSubscribe(evaluationCreate)
            }
            // 0 < 변경 전(후) 평점 <= 5
            // 평가 데이터 수정
            else if ((preRating > 0.0f && preRating <= 5.0f) && (postRating > 0.0f && postRating <= 5.0f)) {
                var evaluationPatch = EvaluationPatch(bookData.bsin, postRating, preState)
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
        subscriptions.add(subscription)
    }

    // getEvaluationSubscribe : 하나의 평가 데이터 조회 관찰자를 구독하는 함수
    private fun getEvaluationSubscribe() {
        val subscription =
            bookInfoPresenter.getEvaluationObservable(this, bookData.bsin)
                .subscribeOn(Schedulers.io()).subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            // 서버에서 응답받은 데이터를 화면에 적용
                            var jsonObject = (result.get("data")).asJsonObject
                            var allAverage = jsonObject.get("all_average").toString().replace("\"", "").toFloat()
                            var allCount = jsonObject.get("all_count").toString().replace("\"", "").toInt()
                            var myRating = jsonObject.get("my_rating").toString().replace("\"", "").toFloat()
                            var myState = jsonObject.get("my_state").toString().replace("\"", "").toInt()

                            // 평가 개수 & 평균 별점 설정
                            bookinfo_txtv_averagestar.text = "평균 ★ " + allAverage + " (" + allCount + "명)"

                            // 내 평점 설정
                            setEvaluation(myRating, myState)
                        }
                        // 설정 끝낸 후 프로그래스 바 종료
                        Looper.prepare()
                        setProgressOFF()
                        showMessage(result.get("message").toString())
                        Looper.loop()
                    },
                    { e ->
                        Looper.prepare()
                        showMessage("Get evaluation error!")
                        Looper.loop()
                    }
                )
        subscriptions.add(subscription)
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
                        Looper.loop()
                    }
                )
        subscriptions.add(subscription)
    }

    // deleteEvaluationSubscribe : 하나의 평가 데이터 삭제 관찰자를 구독하는 함수
    private fun deleteEvaluationSubscribe() {
        val subscription =
            bookInfoPresenter.deleteEvaluationObservable(this, bookData.bsin)
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
        subscriptions.add(subscription)
    }

    // 화면에 도서 정보를 나타내는 함수
    private fun setBookData() {
        var splitUrl = bookData.cover.split("/")
        var coverUrl: String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/xxlarge"
        Glide.with(this).load(coverUrl).into(bookinfo_imgv_book)
        bookinfo_txtv_booktitle.text = bookData.title
        bookinfo_txtv_author.text = bookData.author
        bookinfo_txtv_publisher.text = bookData.publisher
        bookinfo_txtv_date.text = bookData.publication_date
        bookinfo_txtv_introduction.text = bookData.introduction
    }

    // 화면에 도서 평점을 나타내는 함수
    private fun setEvaluation(rating: Float, state: Int) {
        bookinfo_ratingbar_bookrating.rating = rating

        // 이미 누른 버튼은 다시 누르지 못하도록 상태 변경
        when (state) {
            -1 -> {
                bookinfo_btn_boring.isEnabled = true
                bookinfo_btn_interesting.isEnabled = true
                bookinfo_btn_reading.isEnabled = true
                bookinfo_btn_read.isEnabled = true
            }
            0 -> {
                bookinfo_btn_boring.isEnabled = false
                bookinfo_btn_interesting.isEnabled = true
                bookinfo_btn_reading.isEnabled = true
                bookinfo_btn_read.isEnabled = true
            }
            1 -> {
                bookinfo_btn_boring.isEnabled = true
                bookinfo_btn_interesting.isEnabled = false
                bookinfo_btn_reading.isEnabled = true
                bookinfo_btn_read.isEnabled = true
            }
            2 -> {
                bookinfo_btn_boring.isEnabled = true
                bookinfo_btn_interesting.isEnabled = true
                bookinfo_btn_reading.isEnabled = false
                bookinfo_btn_read.isEnabled = true
            }
            3 -> {
                bookinfo_btn_boring.isEnabled = true
                bookinfo_btn_interesting.isEnabled = true
                bookinfo_btn_reading.isEnabled = true
                bookinfo_btn_read.isEnabled = false
            }
        }

        // 변경된 평점, 상태 정보 반영
        preRating = rating
        preState = state
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