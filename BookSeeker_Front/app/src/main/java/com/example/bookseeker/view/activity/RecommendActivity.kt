package com.example.bookseeker.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View.*
import android.widget.Toast
import com.example.bookseeker.R
import com.example.bookseeker.adapter.RecommendCardvAdapter
import com.example.bookseeker.adapter.Utils
import com.example.bookseeker.contract.RecommendContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.presenter.RecommendPresenter
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeDirection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_recommend.*


class RecommendActivity : BaseActivity(), RecommendContract.View, RecommendCardvAdapter.Callback {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var recommendPresenter: RecommendPresenter
    //cardview를 셀 빈 데이터 객체를 초기화
    private var page = 0
    private var itemCount = 0 // 나눗셈을 위해 1 더해둠
    // Disposable 객체 지연 초기화
    private lateinit var disposables: CompositeDisposable
    // 서버에 보낼 객체 지정
    private var genre = "COMIC"
    // Category Change Flag 설정
    private var categoryFlag = false
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
        isToUndo = false
    }

    override fun onSwipeLeft() {
        recommend_layout_linear_category.visibility = INVISIBLE
        recommend_txtv_message.visibility = VISIBLE
        recommend_txtv_message.text = "읽는 중"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#80c783")) // mediumLime
        isToUndo = false
    }

    override fun onSwipeRight() {
        recommend_layout_linear_category.visibility = INVISIBLE
        recommend_txtv_message.visibility = VISIBLE
        recommend_txtv_message.text = "읽고 싶어요"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#f7b73c")) // mediumYellow
        isToUndo = false
    }

    override fun onSwipeBottom() {
        recommend_layout_linear_category.visibility = INVISIBLE
        recommend_txtv_message.visibility = VISIBLE
        recommend_txtv_message.text = "관심 없어요"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#e02947")) // mediumRed
        isToUndo = false
    }

    override fun onSwipeNone() {
        recommend_layout_linear_category.visibility = VISIBLE
        recommend_txtv_message.visibility = INVISIBLE
        recommend_txtv_message.text = "MESSAGE"
        recommend_cardview_category.setCardBackgroundColor(Color.parseColor("#ffffff")) // basicWhite
        isToUndo = true
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
                recommend_swipeview!!.removeAllViews()
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
                recommend_swipeview!!.removeAllViews()
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
                recommend_swipeview!!.removeAllViews()
                getRecommendSubscribe()
            }
        }
    }

    // setSwipeView : 검색한 도서 목록에 대한 SwipeView를 초기화 및 정의하는 함수
    fun setSwipeView(savedInstanceState: Bundle?, bottomMargin: Int, windowSize: Point) {
        val activityMargin = Utils.dpToPx(16)
        val topMargin = Utils.dpToPx(120)

        // setOnClickListener 설정
        recommend_cardview_category.setOnClickListener{
            recommend_swipeview!!.doSwipe(false)
        }
        recommend_btmnavview_menu.setOnClickListener{
            recommend_swipeview!!.doSwipe(false)
        }

        // Swipeview builder 지정
        recommend_swipeview!!.builder
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
        recommend_swipeview!!.addItemRemoveListener {
            if (isToUndo) {
                isToUndo = false
                recommend_swipeview!!.undoLastSwipe()
            } else {
                // 넘긴 개수가 가져온 아이템보다 작은 경우
                if(itemCount < page*10 -1){
                    // 개수 증가
                    itemCount++
                }
                // 만일 마지막 아이템까지 다 넘긴 경우
                else {
                    // 개수 증가 및 함수 호출
                    itemCount++
                    getRecommendSubscribe()
                }
            }
        }

        // 아무것도 없는 경우 함수 호출
        if (savedInstanceState == null) {
            getRecommendSubscribe()
        }
    }

    // getRecommendSubscribe : 관찰자에게서 발행된 데이터를 가져오는 함수
    private fun getRecommendSubscribe() {
//        setProgressON("추천을 진행중입니다...")

        val subscription =
            recommendPresenter
                .getRecommendObservable(this, genre, page, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
//                        setProgressOFF()

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
                                    jsonObject.get("introduction").toString(),
                                    jsonObject.get("cover").toString().replace("\"", ""),
                                    jsonObject.get("link").toString().replace("\"", ""),
                                    jsonObject.get("keyword").toString().replace("\"", ""),
                                    jsonObject.get("adult").toString().replace("\"", ""),
                                    jsonObject.get("genre").toString().replace("\"", ""),
                                    jsonObject.get("publication_date").toString().replace("\"", ""),
                                    jsonObject.get("expect_rating").toString().replace("\"", "").toFloat(),
                                    jsonObject.get("state").toString().replace("\"", "").toInt()
                                )
                                bookDataArray.add(bookData)

                                recommend_swipeview!!.addView(
                                    RecommendCardvAdapter(this, bookData, cardViewHolderSize, this)
                                )
                            }
                            // 페이지 증가
                            page += 1

                            println("페이지 증가 : ${page} 페이지를 불러왔습니다.")

                            // Category가 변경된 경우
                            if (categoryFlag == true) {
                                // (recyclerView.adapter as RecommendAdapter).clearAndAddBookList(bookList.results)
                                categoryFlag = false
                            } else {
                                // (recyclerView.adapter as RecommendAdapter).addBookList(bookList.results)
                            }
                        }
                        this.showMessage(result.get("message").toString())
                    },
                    { e ->
                        showMessage("Get Recommend Error!")
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
