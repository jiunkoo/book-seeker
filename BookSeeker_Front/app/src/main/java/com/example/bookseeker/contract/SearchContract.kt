package com.example.bookseeker.contract

import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView


interface SearchContract {
    interface View : BaseView {
        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView ()

        // setTextViewEventListener : TextView 이벤트를 처리하는 함수
        fun setTextViewEventListener()

        // startSearchDetailActivity : SearchDetailActivity로 넘어가는 함수
        fun startSearchDetailActivity()
    }
    interface Presenter : BasePresenter<View> {}
}