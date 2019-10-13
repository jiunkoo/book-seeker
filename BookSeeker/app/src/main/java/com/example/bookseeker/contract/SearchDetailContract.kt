package com.example.bookseeker.contract

import androidx.fragment.app.Fragment
import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView

interface SearchDetailContract {
    interface View : BaseView {
        // setEditTextEventListener : EditText Event를 처리하는 함수
        fun setEditTextEventListener()

        // replaceFragment : Fragment Event를 처리하는 함수
        fun replaceFragment(fragment: Fragment)

        // switchBottomNavigationView : BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView ()
    }
    interface Presenter : BasePresenter<View> {
    }
}