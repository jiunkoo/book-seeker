package com.example.bookseeker.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.model.data.RecommendData
import com.mindorks.placeholderview.SwipeDirection
import com.mindorks.placeholderview.annotations.*
import com.mindorks.placeholderview.annotations.swipe.*
import kotlin.math.sqrt

@Layout(R.layout.item_cardv_recommend)
class RecommendCardvAdapter(
    private val context: Context,
    private val recommendData: RecommendData,
    private val cardViewHolderSize: Point,
    private val callback: Callback
) {
    private var xStart = 0f
    private var yStart = 0f
    private var xCurrent = 0f
    private var yCurrent = 0f

    @View(R.id.cardv_recommend_item_imgv_image)
    lateinit var image: ImageView

    @View(R.id.cardv_recommend_item_txtv_title)
    lateinit var title: TextView

    @View(R.id.cardv_recommend_item_txtv_author)
    lateinit var author: TextView

    @View(R.id.cardv_recommend_item_txtv_publisher)
    lateinit var publisher: TextView

//    @View(R.id.cardv_recommend_item_txtv_expect_rating)
//    lateinit var expectRating: TextView

    @View(R.id.cardv_recommend_item_cardview_star)
    lateinit var cardviewStar: CardView

    @View(R.id.cardv_recommend_item_imgv_star)
    lateinit var star: ImageView

    @View(R.id.cardv_recommend_item_ratingbar)
    lateinit var ratingBar: RatingBar

    @SwipeView
    lateinit var swipeView: android.view.View

    @JvmField
    @Position
    var position: Int = 0

    @Resolve
    fun onResolved() {
        swipeView.alpha = 1f

        var splitUrl = recommendData.cover.split("/")
        var coverUrl: String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/xlarge"

        Glide.with(context).load(coverUrl).into(image)
        title.text = recommendData.title
        author.text = recommendData.author
        publisher.text = recommendData.publisher
//        expectRating.text = recommendData.expect_rating.toString()
        ratingBar.rating = recommendData.rating

        when (recommendData.state) {
            // '관심 없어요'인 경우
            0 -> {
                cardviewStar.visibility = android.view.View.VISIBLE
                star.visibility = android.view.View.VISIBLE
                star.setColorFilter(Color.parseColor("#e02947"))
            }
            // '관심 있어요'인 경우
            1 -> {
                cardviewStar.visibility = android.view.View.VISIBLE
                star.visibility = android.view.View.VISIBLE
                star.setColorFilter(Color.parseColor("#f7b73c"))
            }
            // '읽고 있어요'인 경우
            2 -> {
                cardviewStar.visibility = android.view.View.VISIBLE
                star.visibility = android.view.View.VISIBLE
                star.setColorFilter(Color.parseColor("#80c783"))
            }
            // '읽었어요'인 경우
            3 -> {
                cardviewStar.visibility = android.view.View.VISIBLE
                star.visibility = android.view.View.VISIBLE
                star.setColorFilter(Color.parseColor("#03738c"))
            }
        }

        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, float, boolean ->
                callback.onRatingBarChangeListener(ratingBar, float, boolean)
            }
    }

    @Click(R.id.cardv_recommend_item_imgv_image)
    fun onClick() {
        Log.d("EVENT", "imageView click")
        callback.onItemSelected(recommendData)
    }

    // 왼쪽 or 맨위
    @SwipeOutDirectional
    fun onSwipeOutDirectional(direction: SwipeDirection) {
        Log.d("DEBUG", "SwipeOutDirectional " + direction.name)
    }

    @SwipeCancelState
    fun onSwipeCancelState() {
        Log.d("DEBUG", "onSwipeCancelState")
        swipeView.alpha = 1f
    }

    // 오른쪽 or 아래
    @SwipeInDirectional
    fun onSwipeInDirectional(direction: SwipeDirection) {
        Log.d("DEBUG", "SwipeInDirectional " + direction.name)
    }

    @SwipingDirection
    fun onSwipingDirection(direction: SwipeDirection) {
        Log.d("DEBUG", "SwipingDirection " + direction.name)
        callback.onSwipeDirection(direction, xStart, yStart, xCurrent, yCurrent)
    }

    @SwipeTouch
    fun onSwipeTouch(xStart: Float, yStart: Float, xCurrent: Float, yCurrent: Float) {
        this.xStart = xStart
        this.yStart = yStart
        this.xCurrent = xCurrent
        this.yCurrent = yCurrent

        val cardHolderDiagonalLength =
            sqrt(
                Math.pow(cardViewHolderSize.x.toDouble(), 2.0)
                        + (Math.pow(cardViewHolderSize.y.toDouble(), 2.0))
            )
        val distance = sqrt(
            Math.pow(xCurrent.toDouble() - xStart.toDouble(), 2.0)
                    + (Math.pow(yCurrent.toDouble() - yStart, 2.0))
        )
        val alpha = 1 - distance / cardHolderDiagonalLength

        Log.d(
            "DEBUG", "onSwipeTouch "
                    + " xStart : " + xStart
                    + " yStart : " + yStart
                    + " xCurrent : " + xCurrent
                    + " yCurrent : " + yCurrent
                    + " distance : " + distance
                    + " TotalLength : " + cardHolderDiagonalLength
                    + " alpha : " + alpha
        )

        swipeView.alpha = alpha.toFloat()
    }

    interface Callback {
        fun onSwipeDirection(direction: SwipeDirection, xStart: Float, yStart: Float, xCurrent: Float, yCurrent: Float)
        fun onSwipeTop()
        fun onSwipeLeft()
        fun onSwipeRight()
        fun onSwipeBottom()
        fun onSwipeNone()
        fun onItemSelected(recommendData: RecommendData)
        fun onRatingBarChangeListener(ratingBar: RatingBar, float: Float, boolean: Boolean)
    }
}