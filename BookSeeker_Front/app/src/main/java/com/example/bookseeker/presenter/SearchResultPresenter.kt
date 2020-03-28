package com.example.bookseeker.presenter

import android.content.Context
import android.util.Log
import com.example.bookseeker.contract.SearchResultContract
import com.example.bookseeker.model.data.BooksSearch
import com.example.bookseeker.network.RetrofitClient
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.OkHttpClient


class SearchResultPresenter : SearchResultContract.Presenter {
    private var searchResultView: SearchResultContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: SearchResultContract.View) {
        searchResultView = view
    }

    // booksSearchObservable : 검색어 및 도서 목록 제약조건을 서버로 보내고 관찰하는 함수
    override fun booksSearchObservable(context: Context, booksSearch: BooksSearch, filter: Int, page: Int, limit: Int):
            Observable<JsonObject> {
        val client: OkHttpClient = RetrofitClient.getClient(context, "addCookie")
        val retrofitInterface = RetrofitClient.retrofitInterface(client)

        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.booksSearch(booksSearch, filter, page, limit)
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
        searchResultView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}