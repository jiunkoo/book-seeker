package com.example.bookseeker.presenter

import android.content.Context
import android.util.Log
import com.example.bookseeker.contract.BookInfoContract
import com.example.bookseeker.model.data.EvaluationCreate
import com.example.bookseeker.model.data.EvaluationPatch
import com.example.bookseeker.network.RetrofitClient
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.OkHttpClient

class BookInfoPresenter : BookInfoContract.Presenter {
    private var bookInfoView: BookInfoContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: BookInfoContract.View) {
        bookInfoView = view
    }

    // createEvaluationObservable : 하나의 평가 데이터 생성 요청을 관찰하는 함수
    fun createEvaluationObservable(context: Context, evaluationCreate: EvaluationCreate): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = RetrofitClient.retrofitInterface(client)

        bookInfoView?.setProgressON("도서 평가 중입니다...")

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
        val retrofitInterface = RetrofitClient.retrofitInterface(client)

        bookInfoView?.setProgressON("도서 평가 수정 중입니다...")

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
        val retrofitInterface = RetrofitClient.retrofitInterface(client)

        bookInfoView?.setProgressON("도서 평가 삭제 중입니다...")

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
        bookInfoView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}