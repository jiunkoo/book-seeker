package com.example.bookseeker.contract

import android.content.Context
import android.os.Bundle
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable


interface MyEvaluationContract {
    interface View : BaseView {
        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView()

        // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
        fun startBookInfoActivity(bookData: BookData)

        // setTabLayout : 내가 평가한 도서 목록에 대한 Tab Layout을 초기화 및 정의하는 함수
        fun setTabLayout(savedInstanceState: Bundle?)

        // setSpinner : 내가 평가한 도서 목록에 대한 Spinner를 초기화 및 정의하는 함수
        fun setSpinner(savedInstanceState: Bundle?)

        // setRecyclerView : 내가 평가한 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
        fun setRecyclerView(savedInstanceState: Bundle?)

        // getCountGenreSubscribe : 관찰자에게서 장르별 도서 평가 개수를 가져오는 함수
        fun getCountGenreSubscribe()

        // getEvaluationsSubscribe : 관찰자에게서 내가 평가한 도서 목록을 가져오는 함수
        fun getEvaluationsSubscribe()
    }
    interface Presenter : BasePresenter<View> {
        // getCountGenreObservable : 장르별 도서 평가 개수 요청을 서버로 보내고 관찰하는 함수
        fun getCountGenreObservable(context: Context): Observable<JsonObject>

        // getEvaluationsObservable : 내가 평가한 도서 목록 제약조건을 서버로 보내고 관찰하는 함수
        fun getEvaluationsObservable(context: Context, genre: String, state: Int, page: Int, limit: Int): Observable<JsonObject>
    }
}