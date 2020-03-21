package com.example.bookseeker.presenter

import android.content.Context
import android.util.Log
import com.example.bookseeker.contract.RatingContract
import com.example.bookseeker.model.data.EvaluationCreate
import com.example.bookseeker.model.data.EvaluationPatch
import com.example.bookseeker.network.RetrofitClient
import com.example.bookseeker.network.RetrofitClient.retrofitInterface
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.OkHttpClient

class RatingPresenter : RatingContract.Presenter {
    private var ratingView: RatingContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: RatingContract.View) {
        ratingView = view
    }

    // getBooksObservable : RatingPresenter에서 모든 검색 결과를 요청하는 함수
    fun getBooksObservable(context: Context, genre: String, filter: Int, page: Int, limit: Int): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = RetrofitClient.retrofitInterface(client)

        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.getBooks(genre, filter, page, limit)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val result = response.body()!!
                subscriber.onNext(result)
                subscriber.onComplete() // 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    // createEvaluationObservable : 하나의 평가 데이터 생성 요청을 관찰하는 함수
    fun createEvaluationObservable(context: Context, evaluationCreate: EvaluationCreate): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = retrofitInterface(client)

        // 데이터 생성을 위한 Create
        return Observable.create { subscriber ->
            val callResponse = retrofitInterface.createEvaluation(evaluationCreate)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val result = response.body()!!
                subscriber.onNext(result)
                subscriber.onComplete() // 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    // patchEvaluationObservable : 하나의 평가 데이터 수정 요청을 관찰하는 함수
    fun patchEvaluationObservable(context: Context, evaluationPatch: EvaluationPatch): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = retrofitInterface(client)

        // 데이터 생성을 위한 Create
        return Observable.create { subscriber ->
            val callResponse = retrofitInterface.patchEvaluation(evaluationPatch)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val result = response.body()!!
                subscriber.onNext(result)
                subscriber.onComplete() // 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    // deleteEvaluationObservable : 하나의 평가 데이터 삭제 요청을 관찰하는 함수
    fun deleteEvaluationObservable(context: Context, bsin: String): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = retrofitInterface(client)

        // 데이터 생성을 위한 Create
        return Observable.create { subscriber ->
            val callResponse = retrofitInterface.deleteEvaluation(bsin)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val result = response.body()!!
                subscriber.onNext(result)
                subscriber.onComplete() // 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        ratingView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}