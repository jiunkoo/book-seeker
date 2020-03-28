package com.example.bookseeker.contract

import android.content.Context
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Observable


interface MyPreferenceContract {
    interface View : BaseView {
        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView()

        // setBarChart : 서버에서 받아온 평점별 도서 평가 개수를 막대 그래프에 적용하는 함수
        fun setBarChart(jsonObject: JsonObject)

        // setWordCloud : 서버에서 받아온 도서 키워드를 word cloud에 적용하는 함수
        fun setWordCloud(jsonArray: JsonArray)

        // getCountRatingSubscribe : 관찰자에게서 평점별 도서 평가 개수를 가져오는 함수
        fun getCountRatingSubscribe()

        // getKeywordSubscribe : 관찰자에게서 도서 키워드를 가져오는 함수
        fun getKeywordSubscribe()
    }
    interface Presenter : BasePresenter<View> {
        // getCountRatingObservable : 평점별 도서 평가 개수 요청을 서버로 보내고 관찰하는 함수
        fun getCountRatingObservable(context: Context): Observable<JsonObject>

        // getKeywordObservable :  내가 평가한 도서 키워드 요청을 서버로 보내고 관찰하는 함수
        fun getKeywordObservable(context: Context, limit: Int): Observable<JsonObject>
    }
}