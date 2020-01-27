package com.example.bookseeker.presenter

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.bookseeker.contract.SearchDetailContract
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.network.RetrofitClient.retrofitInterface
import io.reactivex.Observable

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

    // getAllSearchResultBookList : SearchDetailPresenter에서 모든 Search Result 데이터를 가져오는 함수
    fun getAllSearchResultBookList(category: Int, page: Int, page_size: Int, searchWord: String): Observable<BookList> {
        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.selectAllSearchResultData(category, page, page_size, searchWord)
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
        searchView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}