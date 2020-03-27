package com.example.bookseeker.presenter

import android.content.Context
import android.util.Log
import com.example.bookseeker.contract.MyPreferenceContract
import com.example.bookseeker.contract.MypageContract
import com.example.bookseeker.network.RetrofitClient
import com.example.bookseeker.network.RetrofitClient.retrofitInterface
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.OkHttpClient

class MyPreferencePresenter : MyPreferenceContract.Presenter {
    private var myPreferenceView: MyPreferenceContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: MyPreferenceContract.View) {
        myPreferenceView = view
    }

    // getCountRatingObservable : MyPreferencePresenter에서 장르별 평가 개수를 요청하는 함수
    fun getCountRatingObservable(context: Context): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = retrofitInterface(client)

        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.getCountRating()
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

    // getKeywordObservable : MyPreferencePresenter에서 장르별 평가 개수를 요청하는 함수
    fun getKeywordObservable(context: Context, limit: Int): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = retrofitInterface(client)

        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.getKeyword(limit)
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
        myPreferenceView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}