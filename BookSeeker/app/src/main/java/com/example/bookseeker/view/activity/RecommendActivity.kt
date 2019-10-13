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
import com.example.bookseeker.presenter.RecommendPresenter
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeDirection
import kotlinx.android.synthetic.main.activity_recommend.*


class RecommendActivity : BaseActivity(), RecommendContract.View, RecommendCardvAdapter.Callback {
    // RatingActivity와 함께 생성될 RatingPresenter를 지연 초기화
    private lateinit var recommendPresenter: RecommendPresenter

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

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // Tinder CardView 이벤트 처리
        val activityMargin = Utils.dpToPx(16)
        val topMargin = Utils.dpToPx(120)
        val bottomMargin = Utils.dpToPx(60)
        val windowSize = Utils.getDisplaySize(windowManager)

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

        val cardViewHolderSize = Point(windowSize.x, windowSize.y - bottomMargin)


        for (recommendData in Utils.loadRecommendData(applicationContext)) {
            recommend_swipeview!!.addView(RecommendCardvAdapter(applicationContext, recommendData, cardViewHolderSize, this))
        }

        recommend_cardview_category.setOnClickListener({
            recommend_swipeview!!.doSwipe(false)
        })
        recommend_btmnavview_menu.setOnClickListener({
            recommend_swipeview!!.doSwipe(false)
        })

        recommend_swipeview!!.addItemRemoveListener {
            if (isToUndo) {
                isToUndo = false
                recommend_swipeview!!.undoLastSwipe()
            }
        }
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

        if((xStart <= xCurrent && xCurrent <= xStart + Utils.dpToPx(150).toFloat()) &&
            ( yStart - Utils.dpToPx(150) <= yCurrent && yCurrent <= yStart )){
            onSwipeNone()
        }
        else {
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

//    override fun onSwipeCoordinate(xStart: Float, yStart: Float, xCurrent: Float, yCurrent: Float): String {
//        var setDirection = "NONE"
//        return setDirection
//    }

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
