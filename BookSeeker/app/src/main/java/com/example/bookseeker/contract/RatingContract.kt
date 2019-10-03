package com.example.bookseeker.contract

import android.os.Bundle
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView

interface RatingContract {
    interface View : BaseView {
        // switchBottomNavigationView : RatingActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView()

        // setRecyclerView : RatingActivity에서 평가할 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
        fun setRecyclerView(savedInstanceState: Bundle?)
    }

    interface Presenter : BasePresenter<View> {
        // Model에서 recyclerview에 뿌릴 데이터를 가져오는 함수가 있어야함

//        // getAllRomanceBookList : RatingPresenter에서 모든 Romance Book 데이터를 가져오는 함수
//        fun getAllRomanceBookList(bookData: ArrayList<BookData>)
    }
}