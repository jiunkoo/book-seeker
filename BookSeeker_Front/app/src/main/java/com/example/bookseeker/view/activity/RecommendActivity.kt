package com.example.bookseeker.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View.*
import android.widget.RatingBar
import android.widget.Toast
import com.example.bookseeker.R
import com.example.bookseeker.adapter.RecommendCardvAdapter
import com.example.bookseeker.adapter.Utils
import com.example.bookseeker.contract.RecommendContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.model.data.EvaluationCreate
import com.example.bookseeker.model.data.RecommendData
import com.example.bookseeker.presenter.RecommendPresenter
import com.google.gson.JsonObject
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeDirection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_recommend.*
import kotlinx.android.synthetic.main.item_cardv_recommend.view.*


class RecommendActivity : BaseActivity(), RecommendContract.View, RecommendCardvAdapter.Callback {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var recommendPresenter: RecommendPresenter
    // Disposable 객체 지연 초기화
    private lateinit var disposables: CompositeDisposable
    // 서버에 보낼 객체 지정
    private var genre = "COMIC"
    private var state = -1
    private var rating = -1f
    // Category Change Flag 설정
    private var categoryFlag = false
    //cardview를 세고 저장할 변수 초기화
    private var page = 0
    private var itemCount = 0
    private var recommendList = ArrayList<RecommendData>()
    // Card View Holder Size 지연 초기화
    private lateinit var cardViewHolderSize: Point
    // Swipe Tinder 설정
    private val animationDuration = 300
    private var isToUndo = false
    private var cardX = 0
    private var cardY = 0
    private var xMax = 0f
    private var yMax = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        recommendPresenter.takeView(this)

        // Disposable 객체 지정
        disposables = CompositeDisposable()

        // Tinder CardView 변수 설정
        val bottomMargin = Utils.dpToPx(60)
        val windowSize = Utils.getDisplaySize(windowManager)

        // cardViewHolderSize 지정
        cardViewHolderSize = Point(windowSize.x, windowSize.y - bottomMargin)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // Category 이벤트 처리
        setCategoryEventListener()

        // SwipeView 이벤트 처리
        setSwipeView(savedInstanceState, bottomMargin, windowSize)
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        cardX = recommend_swipeview.width
        cardY = recommend_swipeview.height
        xMax = recommend_swipeview.x
        yMax = recommend_swipeview.y
    }

    // RecommendCardvAdapter에서 Callback override
    override fun onSwipeDirection(
        direction: SwipeDirection,
        xStart: Float,
        yStart: Float,
        xCurrent: Float,
        yCurrent: Float
    ) {
//        val defaultDisplaySize = Point()
//        windowManager.defaultDisplay.getSize(defaultDisplaySize)
//        val xMax = defaultDisplaySize.x
//        val yMax = defaultDisplaySize.y

        if ((xStart <= xCurrent && xCurrent <= xStart + Utils.dpToPx(150).toFloat()) &&
            (yStart - Utils.dpToPx(150) <= yCurrent && yCurrent <= yStart)
        ) {
            onSwipeNone()
        } else {
            when (direction.name) {
                "TOP" -> {
                    onSwipeTop()
                }
                "LEFT" -> {
                    onSwipeLeft()
                }
                "RIGHT" -> {
                    onSwipeRight()
                }
                "BOTTOM" -> {
                    onSwipeBottom()
                }

                "LEFT_TOP" -> { // "a"
                    // 직선의 기울기 및 상수
                    var gradient = (yStart - Utils.dpToPx(150).toFloat()) / xStart
                    var constant = 0f

                    // 현재 좌표가 직선을 지나는 경우
                    if (yCurrent == (gradient * xCurrent + constant)) {
                        onSwipeNone()
                    }
                    // 현재 좌표가 직선 위를 지나는 경우
                    else if (yCurrent > (gradient * xCurrent + constant)) { // LEFT
                        onSwipeLeft()
                    }
                    // 현재 좌표가 직선 아래를 지나는 경우
                    else { // TOP
                        onSwipeTop()
                    }
                }

                "LEFT_BOTTOM" -> { // "-a"
                    // 직선의 기울기 및 상수
                    var gradient = (yMax - yStart) / (0 - xStart)
                    var constant = yMax

                    // 현재 좌표가 직선을 지나는 경우
                    if (yCurrent == (gradient * xCurrent + constant)) {
                        onSwipeNone()
                    }
                    // 현재 좌표가 직선 위를 지나는 경우
                    else if (yCurrent > (gradient * xCurrent + constant)) { // BOTTOM
                        onSwipeBottom()
                    }
                    // 현재 좌표가 직선 아래를 지나는 경우
                    else { // LEFT
                        onSwipeLeft()
                    }
                }

                "RIGHT_TOP" -> { // "-a"
                    // 직선의 기울기 및 상수
                    var gradient =
                        ((yStart - Utils.dpToPx(150).toFloat()) - 0) / ((xStart + Utils.dpToPx(150).toFloat()) - xMax)
                    var constant =
                        (yStart - Utils.dpToPx(150).toFloat()) - (gradient * (xStart + Utils.dpToPx(150).toFloat()))

                    // 현재 좌표가 직선을 지나는 경우
                    if (yCurrent == (gradient * xCurrent + constant)) {
                        onSwipeNone()
                    }
                    // 현재 좌표가 직선 위를 지나는 경우
                    else if (yCurrent > (gradient * xCurrent + constant)) { // RIGHT
                        onSwipeRight()
                    }
                    // 현재 좌표가 직선 아래를 지나는 경우
                    else { // TOP
                        onSwipeTop()
                    }
                }

                "RIGHT_BOTTOM" -> { // "a"
                    // 직선의 기울기 및 상수
                    var gradient = (yMax - yStart) / (xMax - (xStart + Utils.dpToPx(150).toFloat()))
                    var constant =
                        (yStart - Utils.dpToPx(0).toFloat()) - (gradient * (xStart + Utils.dpToPx(150).toFloat()))

                    // 현재 좌표가 직선을 지나는 경우
                    if (yCurrent == (gradient * xCurrent + constant)) {
                        onSwipeNone()
                    }
                    // 현재 좌표가 직선 위를 지나는 경우
                    else if (yCurrent > (gradient * xCurrent + constant)) { // BOTTOM
                        onSwipeBottom()
                    }
                    // 현재 좌표가 직선 아래를 지나는 경우
                    else { // RIGHT
                        onSwipeRight()
                    }
                }
            }
        }
    }

    override fun onSwipeTop() {
        recommend_layout_linear_category.visibility = INVISIBLE
        recommend_txtv_message.visibility = VISIBLE
        recommend_txtv_message.text = "읽었어요"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#03738c")) // mediumMint
        state = 3
        isToUndo = false
    }

    override fun onSwipeRight() {
        recommend_layout_linear_category.visibility = INVISIBLE
        recommend_txtv_message.visibility = VISIBLE
        recommend_txtv_message.text = "읽고 있어요"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#80c783")) // mediumLime
        state = 2
        isToUndo = false
    }

    override fun onSwipeLeft() {
        recommend_layout_linear_category.visibility = INVISIBLE
        recommend_txtv_message.visibility = VISIBLE
        recommend_txtv_message.text = "관심 있어요"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#f7b73c")) // mediumYellow
        state = 1
        isToUndo = false
    }

    override fun onSwipeBottom() {
        recommend_layout_linear_category.visibility = INVISIBLE
        recommend_txtv_message.visibility = VISIBLE
        recommend_txtv_message.text = "관심 없어요"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#e02947")) // mediumRed
        state = 0
        isToUndo = false
    }

    override fun onSwipeNone() {
        recommend_layout_linear_category.visibility = VISIBLE
        recommend_txtv_message.visibility = INVISIBLE
        recommend_txtv_message.text = "MESSAGE"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#ffffff")) // basicWhite
        state = -1
        isToUndo = true
    }

    // setCategoryEventListener : RatingBar Event를 처리하는 함수
    override fun onRatingBarChangeListener(ratingBar: RatingBar, float: Float, boolean: Boolean) {
        rating = float
    }

    // onItemSelected : Item Select Event를 처리하는 함수
    override fun onItemSelected(recommendData: RecommendData) {
        // 해당 도서 데이터 가져오기
        startBookInfoActivity(recommendData)
    }

    // setCategoryEventListener : EditText Event를 처리하는 함수
    fun setCategoryEventListener() {
        // Comic Category Event를 처리하는 함수
        recommend_layout_linear_comic.setOnClickListener {
            if (genre == "COMIC") {
                categoryFlag = false
            } else {
                genre = "COMIC"
                categoryFlag = true
                page = 0
                itemCount = 0

                // 버튼 색상 변경
                // 만화
                recommend_layout_linear_comic.setBackgroundColor(Color.parseColor("#273238"))
                recommend_imgv_comic.setColorFilter(Color.parseColor("#ffffff"))
                recommend_txtv_comic.setTextColor(Color.parseColor("#ffffff"))
                // 로맨스
                recommend_layout_linear_romance.setBackgroundColor(Color.parseColor("#ffffff"))
                recommend_imgv_romance.setColorFilter(Color.parseColor("#273238"))
                recommend_txtv_romance.setTextColor(Color.parseColor("#273238"))
                // 판타지
                recommend_layout_linear_fantasy.setBackgroundColor(Color.parseColor("#ffffff"))
                recommend_imgv_fantasy.setColorFilter(Color.parseColor("#273238"))
                recommend_txtv_fantasy.setTextColor(Color.parseColor("#273238"))

                // 모든 카드 삭제
                recommend_swipeview.removeAllViews()
                getRecommendSubscribe()
            }
        }
        // Romance Category Event를 처리하는 함수
        recommend_layout_linear_romance.setOnClickListener {
            if (genre == "ROMANCE") {
                categoryFlag = false
            } else {
                genre = "ROMANCE"
                categoryFlag = true
                page = 0
                itemCount = 0

                // 버튼 색상 변경
                // 만화
                recommend_layout_linear_comic.setBackgroundColor(Color.parseColor("#ffffff"))
                recommend_imgv_comic.setColorFilter(Color.parseColor("#273238"))
                recommend_txtv_comic.setTextColor(Color.parseColor("#273238"))
                // 로맨스
                recommend_layout_linear_romance.setBackgroundColor(Color.parseColor("#273238"))
                recommend_imgv_romance.setColorFilter(Color.parseColor("#ffffff"))
                recommend_txtv_romance.setTextColor(Color.parseColor("#ffffff"))
                // 판타지
                recommend_layout_linear_fantasy.setBackgroundColor(Color.parseColor("#ffffff"))
                recommend_imgv_fantasy.setColorFilter(Color.parseColor("#273238"))
                recommend_txtv_fantasy.setTextColor(Color.parseColor("#273238"))

                // 모든 카드 삭제
                recommend_swipeview.removeAllViews()
                getRecommendSubscribe()
            }
        }
        // Fantasy Category Event를 처리하는 함수
        recommend_layout_linear_fantasy.setOnClickListener {
            if (genre == "FANTASY") {
                categoryFlag = false
            } else {
                genre = "FANTASY"
                categoryFlag = true
                page = 0
                itemCount = 0

                // 버튼 색상 변경
                // 만화
                recommend_layout_linear_comic.setBackgroundColor(Color.parseColor("#ffffff"))
                recommend_imgv_comic.setColorFilter(Color.parseColor("#273238"))
                recommend_txtv_comic.setTextColor(Color.parseColor("#273238"))
                // 로맨스
                recommend_layout_linear_romance.setBackgroundColor(Color.parseColor("#ffffff"))
                recommend_imgv_romance.setColorFilter(Color.parseColor("#273238"))
                recommend_txtv_romance.setTextColor(Color.parseColor("#273238"))
                // 판타지
                recommend_layout_linear_fantasy.setBackgroundColor(Color.parseColor("#273238"))
                recommend_imgv_fantasy.setColorFilter(Color.parseColor("#ffffff"))
                recommend_txtv_fantasy.setTextColor(Color.parseColor("#ffffff"))

                // 모든 카드 삭제
                recommend_swipeview.removeAllViews()
                getRecommendSubscribe()
            }
        }
    }

    // setSwipeView : 검색한 도서 목록에 대한 SwipeView를 초기화 및 정의하는 함수
    fun setSwipeView(savedInstanceState: Bundle?, bottomMargin: Int, windowSize: Point) {
        val activityMargin = Utils.dpToPx(16)
        val topMargin = Utils.dpToPx(120)

        // Swipeview builder 지정
        recommend_swipeview!!
            .builder
            .setDisplayViewCount(3)
            .setIsUndoEnabled(true)
            .setSwipeVerticalThreshold(Utils.dpToPx(150))
            .setSwipeHorizontalThreshold(Utils.dpToPx(150))
            .setHeightSwipeDistFactor(10f)
            .setWidthSwipeDistFactor(5f)
            .setSwipeDecor(
                SwipeDecor()
                    .setViewWidth(windowSize.x - activityMargin * 2)
                    .setViewHeight(windowSize.y - (topMargin + bottomMargin + activityMargin * 5))
                    .setViewGravity(Gravity.TOP)
                    .setPaddingTop(20)
                    .setSwipeAnimTime(animationDuration)
                    .setRelativeScale(0.01f)
            )

        // removeListener 지정
        recommend_swipeview.addItemRemoveListener {
            // true인 경우 다시 원래 카드로 돌아감
            if (isToUndo) {
                isToUndo = false
                recommend_swipeview.undoLastSwipe()
            }
            // false인 경우 아이템 삭제
            else {
                // 카테고리 원래대로 복구
                recommend_layout_linear_category.visibility = VISIBLE
                recommend_txtv_message.visibility = INVISIBLE
                recommend_txtv_message.text = "MESSAGE"
                recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#ffffff")) // basicWhite

                // 넘긴 개수가 가져온 아이템보다 작은 경우
                if(itemCount < page*10-1){
                    // 개수 증가
                    itemCount++

                    // 도서 (상태) 평가
                    // '읽었어요'인 경우 평점을 반드시 선택해야함
                    if(state == 3) {
                        // 별점이 0점 초과인 경우
                        if(rating > 0.0f) {
                            var evaluationCreate = EvaluationCreate(recommendList[(itemCount%10)-1].bsin, genre, rating, state)
                            createEvaluationSubscribe(evaluationCreate)
                        }
                        else {
                            // 개수 감소
                            itemCount--
                            showMessage("완독한 도서는 별점을 입력해주세요!")
                            recommend_swipeview!!.undoLastSwipe()
                        }
                    }
                    // '읽었어요' 이외의 경우 평점 체크 여부는 상관없음
                    else {
                        var evaluationCreate = EvaluationCreate(recommendList[(itemCount%10)-1].bsin, genre, rating, state)
                        createEvaluationSubscribe(evaluationCreate)
                    }
                }
                // 만일 마지막 아이템까지 다 넘긴 경우
                else {
                    // 도서 (상태) 평가
                    // '읽었어요'인 경우 평점을 반드시 선택해야함
                    if(state == 3) {
                        // 별점이 0점 이상인 경우
                        if(rating >= 0.0f) {
                            var evaluationCreate = EvaluationCreate(recommendList[itemCount%10].bsin, genre, rating, state)
                            createEvaluationSubscribe(evaluationCreate)
                        }
                        else {
                            showMessage("완독한 도서는 별점을 입력해주세요!")
                            recommend_swipeview.undoLastSwipe()
                        }
                    }
                    // '읽었어요' 이외의 경우 평점 체크 여부는 상관없음
                    else {
                        var evaluationCreate = EvaluationCreate(recommendList[itemCount%10].bsin, genre, rating, state)
                        createEvaluationSubscribe(evaluationCreate)
                    }
                    // 개수 증가
                    itemCount++

                    // 추천 함수 호출
                    getRecommendSubscribe()
                }
            }

            // setOnClickListener 설정
            recommend_cardview_category.setOnClickListener{
                recommend_swipeview.doSwipe(false)
            }
            recommend_btmnavview_menu.setOnClickListener{
                recommend_swipeview.doSwipe(false)
            }
        }

        // 아무것도 없는 경우 함수 호출
        if (savedInstanceState == null) {
            getRecommendSubscribe()
        }
    }

    // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
    fun startBookInfoActivity(recommendData: RecommendData) {
        val nextIntent = Intent(this, BookInfoActivity::class.java)
        nextIntent.putExtra("bsin", recommendData.bsin)
        nextIntent.putExtra("genre", recommendData.genre)
        nextIntent.putExtra("link", recommendData.link)
        startActivity(nextIntent)
    }

    // getRecommendSubscribe : 관찰자에게서 발행된 데이터를 가져오는 함수
    private fun getRecommendSubscribe() {
        val subscription =
            recommendPresenter
                .getRecommendObservable(this, genre, page, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            var recommendDataArray = ArrayList<RecommendData>()
                            // 반복문을 돌려 서버에서 응답받은 데이터를 저장
                            var jsonArray = (result.get("data")).asJsonArray

                            for (i in 0 until jsonArray.size()) {
                                var jsonObject = jsonArray[i].asJsonObject
                                println(jsonObject.get("title").toString().replace("\"", ""))
                                // 데이터 가공 처리(큰따옴표 제거)
                                var recommendData = RecommendData(
                                    jsonObject.get("bsin").toString().replace("\"", ""),
                                    jsonObject.get("title").toString().replace("\"", ""),
                                    jsonObject.get("author").toString().replace("\"", ""),
                                    jsonObject.get("publisher").toString().replace("\"", ""),
                                    jsonObject.get("introduction").toString(),
                                    jsonObject.get("cover").toString().replace("\"", ""),
                                    jsonObject.get("link").toString().replace("\"", ""),
                                    jsonObject.get("keyword").toString().replace("\"", ""),
                                    jsonObject.get("adult").toString().replace("\"", ""),
                                    jsonObject.get("genre").toString().replace("\"", ""),
                                    jsonObject.get("publication_date").toString().replace("\"", ""),
                                    jsonObject.get("rating").toString().replace("\"", "").toFloat(),
                                    jsonObject.get("state").toString().replace("\"", "").toInt(),
                                    jsonObject.get("expect_rating").toString().replace("\"", "").toFloat()
                                )
                                recommendDataArray.add(recommendData)

                                recommend_swipeview.addView(
                                    RecommendCardvAdapter(this, recommendData, cardViewHolderSize, this)
                                )
                            }
                            // 페이지 증가
                            page += 1

                            // recommendList 내부 데이터 전부 삭제 후 저장(카드 삭제하니까)
                            recommendList.clear()
                            recommendList.addAll(recommendDataArray)
                        }
                        this.showMessage(result.get("message").toString())
                    },
                    { e ->
                        showMessage("Get Recommend Error!")
                    }
                )
        disposables.add(subscription)
    }

    // createEvaluationSubscribe : 하나의 평가 데이터 생성 관찰자를 구독하는 함수
    private fun createEvaluationSubscribe(evaluationCreate: EvaluationCreate) {
        val subscription =
            recommendPresenter
                .createEvaluationObservable(this, evaluationCreate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        // 사용자 평점 초기화
                        rating = -1f

                        // 설정 끝낸 후 메세지 띄우기
                        showMessage(result.get("message").toString())
                    },
                    { e ->
                        showMessage("Create evaluation error!")
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
