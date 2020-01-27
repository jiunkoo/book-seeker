package com.example.bookseeker.contract

import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView

interface SearchContract {
    interface View : BaseView {
        // switchBottomNavigationView : SearchActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView ()

        // setTextViewEventListener : SearchActivity에서 TextView Event를 처리하는 함수
        fun setTextViewEventListener()

        // startSearchDetailActivity : SearchActivity에서 SearchDetailActivity로 넘어가는 함수
        fun startSearchDetailActivity()
    }
    interface Presenter : BasePresenter<View> {}
}