package com.example.bookseeker.adapter

import android.content.Context
import android.graphics.Point
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.model.data.RecommendData
import com.mindorks.placeholderview.SwipeDirection
import com.mindorks.placeholderview.annotations.*
import com.mindorks.placeholderview.annotations.swipe.*
import java.lang.Math.sqrt

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

    @View(R.id.cardv_recommend_item_imgv_bookimage)
    lateinit var bookImage: ImageView

    @View(R.id.cardv_recommend_item_txtv_booktitle)
    lateinit var bookTitle: TextView

    @View(R.id.cardv_recommend_item_txtv_bookauthor)
    lateinit var bookAuthor: TextView

    @View(R.id.cardv_recommend_item_txtv_bookpublisher)
    lateinit var bookPublisher: TextView

    @View(R.id.cardv_recommend_item_ratingbar_bookrating)
    lateinit var bookRating: RatingBar

    @SwipeView
    lateinit var swipeView: android.view.View

    @JvmField
    @Position
    var position: Int = 0

    @Resolve
    fun onResolved() {
        Glide.with(context).load(recommendData.bookImageUrl).into(bookImage)
        bookTitle!!.text = recommendData.bookTitle
        bookAuthor!!.text = recommendData.bookAuthor
        bookPublisher!!.text = recommendData.bookPublisher
        bookRating!!.rating = recommendData.bookRating
        swipeView.alpha = 1f
    }

    @Click(R.id.cardv_recommend_item_imgv_bookimage)
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
//        callback.onSwipeCoordinate(xStart, yStart, xCurrent, yCurrent)
    }

    interface Callback {
        fun onSwipeDirection(direction: SwipeDirection, xStart: Float, yStart: Float, xCurrent: Float, yCurrent: Float)
        fun onSwipeTop()
        fun onSwipeLeft()
        fun onSwipeRight()
        fun onSwipeBottom()
        fun onSwipeNone()
//        fun onSwipeCoordinate(xStart: Float, yStart: Float, xCurrent: Float, yCurrent: Float): String
    }
}