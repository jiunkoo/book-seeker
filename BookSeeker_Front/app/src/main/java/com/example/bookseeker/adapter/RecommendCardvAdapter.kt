package com.example.bookseeker.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.model.data.BookData
import com.mindorks.placeholderview.SwipeDirection
import com.mindorks.placeholderview.annotations.*
import com.mindorks.placeholderview.annotations.swipe.*
import kotlin.math.sqrt

@Layout(R.layout.item_cardv_recommend)
class RecommendCardvAdapter(
    private val context: Context,
    private val bookData: BookData,
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

    @View(R.id.cardv_recommend_item_txtv_rating)
    lateinit var rating: TextView

    @View(R.id.cardv_recommend_item_ibtn_boring)
    lateinit var boring: ImageButton

    @View(R.id.cardv_recommend_item_ibtn_interesting)
    lateinit var interesting: ImageButton

    @SwipeView
    lateinit var swipeView: android.view.View

    @JvmField
    @Position
    var position: Int = 0

    @Resolve
    fun onResolved() {
        var splitUrl = bookData.cover.split("/")
        var coverUrl: String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/xlarge"

        Glide.with(context).load(coverUrl).into(image)
        title!!.text = bookData.title
        author!!.text = bookData.author
        publisher!!.text = bookData.publisher
        rating!!.text = bookData.rating.toString()

        when(bookData.state){
                // 아무것도 아닌 경우
                -2 -> {
                    boring.setColorFilter(Color.parseColor("#ffffff"))
                    interesting.setImageResource(R.drawable.icon_interesting_border)
                    interesting.setColorFilter(Color.parseColor("#ffffff"))
                }
                // '관심 없어요'인 경우
                0 -> {
                    boring.setColorFilter(Color.parseColor("#e02947"))
                    interesting.setImageResource(R.drawable.icon_interesting_border)
                    interesting.setColorFilter(Color.parseColor("#ffffff"))
                }
                // '관심 있어요'인 경우
                1 -> {
                    boring.setColorFilter(Color.parseColor("#ffffff"))
                    interesting.setImageResource(R.drawable.icon_interesting)
                    interesting.setColorFilter(Color.parseColor("#e02947"))
                }
            }
        swipeView.alpha = 1f
    }

    @Click(R.id.cardv_recommend_item_imgv_image)
    fun onClick() {
        Log.d("EVENT", "imageView click")
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
    }
}