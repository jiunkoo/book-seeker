package com.example.bookseeker.contract

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import com.example.bookseeker.model.data.EvaluationCreate
import com.example.bookseeker.model.data.RecommendData
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable


interface RecommendContract {
    interface View : BaseView {
        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView()

        // setSwipeView : 추천 도서 목록에 대한 SwipeView를 초기화 및 정의하는 함수
        fun setSwipeView(savedInstanceState: Bundle?, bottomMargin: Int, windowSize: Point)

        // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
        fun startBookInfoActivity(recommendData: RecommendData)

        // getRecommendSubscribe : 관찰자에게서 추천 도서 목록을 가져오는 함수
        fun getRecommendSubscribe()

        // createEvaluationSubscribe : 관찰자에게서 도서 평가 결과를 가져오는 함수
        fun createEvaluationSubscribe(evaluationCreate: EvaluationCreate)
    }

    interface Presenter : BasePresenter<View> {
        // getRecommendObservable : 추천 받을 도서 목록 제약 조건을 서버로 보내고 관찰하는 함수
        fun getRecommendObservable(context: Context, genre: String, page: Int, limit: Int): Observable<JsonObject>

        // createEvaluationObservable : 도서 평가 데이터를 서버로 보내고 관찰하는 함수
        fun createEvaluationObservable(context: Context, evaluationCreate: EvaluationCreate): Observable<JsonObject>
    }
}