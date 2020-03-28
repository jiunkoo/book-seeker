package com.example.bookseeker.contract

import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView


interface SearchDetailContract {
    interface View : BaseView {
        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView ()

        // setEditTextEventListener : EditText 이벤트를 처리하는 함수
        fun setEditTextEventListener()

        // setButtonEventListener() : Button 이벤트를 처리하는 함수
        fun setButtonEventListener()

        // startSearchActivity : SearchActivity로 넘어가는 함수
        fun startSearchActivity()

        // startSearchResultActivity : SearchActivity로 넘어가는 함수
        fun startSearchResultActivity()
    }
    interface Presenter : BasePresenter<View> {}
}