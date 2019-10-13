package com.example.bookseeker.contract

import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView

interface SearchDetailContract {
    interface View : BaseView {
        // setEditTextEventListener : SearchDetailActivity에서 EditText Event를 처리하는 함수
        fun setEditTextEventListener()

        // switchBottomNavigationView : SearchDetailActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView ()
    }
    interface Presenter : BasePresenter<View> {
    }
}