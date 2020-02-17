package com.example.bookseeker.presenter

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.bookseeker.contract.SearchDetailContract
import com.example.bookseeker.model.data.SearchRequest
import com.example.bookseeker.network.RetrofitClient
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.OkHttpClient

class SearchDetailPresenter : SearchDetailContract.Presenter {
    private var searchView: SearchDetailContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: SearchDetailContract.View) {
        searchView = view
    }

    // fragment전환
    fun replaceFragment(fragment: Fragment){
        searchView!!.replaceFragment(fragment)
    }

    // searchBooks : SearchDetailPresenter에서 모든 Search Result 데이터를 가져오는 함수
    fun searchBooks(context: Context, searchRequest: SearchRequest, filter: Int, page: Int, limit: Int): Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = RetrofitClient.retrofitInterface(client)

        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.searchBooks(searchRequest, filter, page, limit)
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
        searchView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}