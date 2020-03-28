package com.example.bookseeker.contract

import android.content.Context
import android.os.Bundle
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.EvaluationCreate
import com.example.bookseeker.model.data.EvaluationPatch
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable


interface RatingContract {
    interface View : BaseView {
        // switchBottomNavigationView : RatingActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView()

        // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
        fun startBookInfoActivity(bookData: BookData)

        // setTabLayout : 평가할 도서 목록에 대한 Tab Layout을 초기화 및 정의하는 함수
        fun setTabLayout(savedInstanceState: Bundle?)

        // setSpinner : 평가할 도서 목록에 대한 Spinner를 초기화 및 정의하는 함수
        fun setSpinner(savedInstanceState: Bundle?)

        // setRecyclerView : 평가할 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
        fun setRecyclerView(savedInstanceState: Bundle?)

        // getCountGenreSubscribe : 관찰자에게서 장르별 도서 평가 개수를 가져오는 함수
        fun getCountGenreSubscribe()

        // getBooksSubscribe : 관찰자에게서 평가할 도서 목록을 가져오는 함수
        fun getBooksSubscribe()

        // createEvaluationSubscribe : 관찰자에게서 도서 평가 결과를 가져오는 함수
        fun createEvaluationSubscribe(bookData: BookData, position: Int, evaluationCreate: EvaluationCreate)

        // patchEvaluationSubscribe : 관찰자에게서 도서 평가 수정 결과를 가져오는 함수
        fun patchEvaluationSubscribe(bookData: BookData, position: Int, evaluationPatch: EvaluationPatch)

        // deleteEvaluationSubscribe : 관찰자에게서 도서 평가 삭제 결과를 가져오는 함수
        fun deleteEvaluationSubscribe(bookData: BookData, position: Int)
    }

    interface Presenter : BasePresenter<View> {
        // getCountGenreObservable : 장르별 도서 평가 개수 요청을 서버로 보내고 관찰하는 함수
        fun getCountGenreObservable(context: Context): Observable<JsonObject>

        // getBooksObservable : 평가할 도서 목록 제약조건을 서버로 보내고 관찰하는 함수
        fun getBooksObservable(context: Context, genre: String, filter: Int, page: Int, limit: Int): Observable<JsonObject>

        // createEvaluationObservable : 도서 평가 데이터를 서버로 보내고 관찰하는 함수
        fun createEvaluationObservable(context: Context, evaluationCreate: EvaluationCreate): Observable<JsonObject>

        // patchEvaluationObservable : 도서 평가 수정 데이터를 서버로 보내고 관찰하는 함수
        fun patchEvaluationObservable(context: Context, evaluationPatch: EvaluationPatch): Observable<JsonObject>

        // deleteEvaluationObservable : 도서 평가 삭제 데이터를 서버로 보내고 관찰하는 함수
        fun deleteEvaluationObservable(context: Context, bsin: String): Observable<JsonObject>
    }
}