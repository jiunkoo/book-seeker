package com.example.bookseeker.contract

import com.example.bookseeker.presenter.BasePresenter
import com.example.bookseeker.view.BaseView

interface RecommendContract {
    interface View : BaseView {
        // switchBottomNavigationView : RecommendActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
        fun switchBottomNavigationView ()
    }
    interface Presenter : BasePresenter<View>{
        // Model에서 recyclerview에 뿌릴 데이터를 가져오는 함수가 있어야함
    }
}