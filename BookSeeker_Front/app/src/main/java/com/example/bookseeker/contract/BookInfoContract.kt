package com.example.bookseeker.contract

import android.content.Context
import com.example.bookseeker.model.data.EvaluationCreate
import com.example.bookseeker.model.data.EvaluationPatch
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable


interface BookInfoContract {
    interface View : BaseView {
        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView ()

        // setButtonEventListener : Button 이벤트를 처리하는 함수
        fun setButtonEventListener(bsin: String, genre: String, link: String)

        // setRatingbarEventListener : Ratingbar 이벤트를 처리하는 함수
        fun setRatingbarEventListener(bsin: String, genre: String)

        // setEvaluation : 변경된 도서 평점을 화면에 적용하는 함수
        fun setEvaluation(rating: Float, state: Int)

        // getBookSubscribe : 관찰자에게서 하나의 도서 데이터를 가져오는 함수
        fun getBookSubscribe(bsin: String)

        // createEvaluationSubscribe : 관찰자에게서 도서 평가 결과를 가져오는 함수
        fun createEvaluationSubscribe(evaluationCreate: EvaluationCreate)

        // patchEvaluationSubscribe : 관찰자에게서 도서 평가 수정 결과를 가져오는 함수
        fun patchEvaluationSubscribe(evaluationPatch: EvaluationPatch)

        // deleteEvaluationSubscribe : 관찰자에게서 도서 평가 삭제 결과를 가져오는 함수
        fun deleteEvaluationSubscribe()
    }
    interface Presenter : BasePresenter<View> {
        // getBookObservable : 하나의 도서 bsin을 서버로 보내고 관찰하는 함수
        fun getBookObservable(context: Context, bsin: String): Observable<JsonObject>

        // createEvaluationObservable : 도서 평가 데이터를 서버로 보내고 관찰하는 함수
        fun createEvaluationObservable(context: Context, evaluationCreate: EvaluationCreate): Observable<JsonObject>

        // patchEvaluationObservable : 도서 평가 수정 데이터를 서버로 보내고 관찰하는 함수
        fun patchEvaluationObservable(context: Context, evaluationPatch: EvaluationPatch): Observable<JsonObject>

        // deleteEvaluationObservable : 도서 평가 삭제 데이터를 서버로 보내고 관찰하는 함수
        fun deleteEvaluationObservable(context: Context, bsin: String): Observable<JsonObject>
    }
}