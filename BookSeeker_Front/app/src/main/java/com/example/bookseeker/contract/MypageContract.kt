package com.example.bookseeker.contract

import android.content.Context
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable


interface MypageContract {
    interface View : BaseView {
        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView()

        // setCardviewEventListener : Cardview 이벤트를 처리하는 함수
        fun setCardviewEventListener()

        // startMyEvaluationActivity : MyEvaluationActivity로 넘어가는 함수
        fun startMyEvaluationActivity()

        // startMyPreferenceActivity : MyPreferenceActivity로 넘어가는 함수
        fun startMyPreferenceActivity()

        // getMineSubscribe : 관찰자에게서 내 정보를 가져오는 함수
        fun getMineSubscribe()

        // getCountGenreSubscribe : 관찰자에게서 장르별 도서 평가 개수를 가져오는 함수
        fun getCountGenreSubscribe()

        // getCountStateSubscribe : 관찰자에게서 상태별 도서 평가 개수를 가져오는 함수
        fun getCountStateSubscribe()
    }
    interface Presenter : BasePresenter<View> {
        // getMineObservable : 내 정보 요청을 서버로 보내고 관찰하는 함수
        fun getMineObservable(context: Context): Observable<JsonObject>

        // getCountGenreObservable : 장르별 도서 평가 개수 요청을 서버로 보내고 관찰하는 함수
        fun getCountGenreObservable(context: Context): Observable<JsonObject>

        // getCountStateObservable : 상태별 도서 평가 개수 요청을 서버로 보내고 관찰하는 함수
        fun getCountStateObservable(context: Context): Observable<JsonObject>
    }
}