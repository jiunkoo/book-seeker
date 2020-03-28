package com.example.bookseeker.contract

import android.content.Context
import android.os.Bundle
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BooksSearch
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView
import com.google.gson.JsonObject
import io.reactivex.Observable


interface SearchResultContract {
    interface View : BaseView {
        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView ()

        // setButtonEventListener : Button 이벤트를 처리하는 함수
        fun setButtonEventListener()

        // setEditTextEventListener : EditText 이벤트를 처리하는 함수
        fun setEditTextEventListener(keyword: String)

        // startSearchDetailActivity : SearchDetailActivity로 넘어가는 함수
        fun startSearchDetailActivity()

        // startBookInfoActivity : bookInfoActivity로 넘어가는 함수
        fun startBookInfoActivity(bookData: BookData)

        // setSpinner : 검색한 도서 목록에 대한 Spinner를 초기화 및 정의하는 함수
        fun setSpinner(keyword: String, spinner: Spinner, recyclerView: RecyclerView, savedInstanceState: Bundle?)

        // setRecyclerView : 검색한 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
        fun setRecyclerView(keyword: String, recyclerView: RecyclerView, savedInstanceState: Bundle?)

        // booksSearchSubscribe : 관찰자에게서 검색한 도서 목록을 가져오는 함수
        fun booksSearchSubscribe(keyword: String, recyclerView: RecyclerView)
    }
    interface Presenter : BasePresenter<View> {
        // booksSearchObservable : 검색어 및 도서 목록 제약조건을 서버로 보내고 관찰하는 함수
        fun booksSearchObservable(context: Context, booksSearch: BooksSearch, filter: Int, page: Int, limit: Int):
                Observable<JsonObject>
    }
}