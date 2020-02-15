package com.example.bookseeker.presenter

import android.util.Log
import com.example.bookseeker.contract.RecommendContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.network.RetrofitClient.retrofitInterface
import io.reactivex.Observable

class RecommendPresenter : RecommendContract.Presenter{
    private var recommendView: RecommendContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: RecommendContract.View) {
        recommendView = view
    }

    fun getAllRecommendBookList(page: Int, userToken: String): Observable<BookList> {
        return Observable.create{ subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.selectAllComicRecommendData(userToken)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val bookListResults: List<BookData> = response.body()!!.results

                if (bookListResults != null) {
                    val responsePage = page + 1 // 페이지 증가
                    val bookList = BookList(responsePage, bookListResults) // page + results
                    subscriber.onNext(bookList) // 구독자(관찰자)에게 데이터의 발행을 알림
                }
                subscriber.onComplete() // 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        recommendView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}