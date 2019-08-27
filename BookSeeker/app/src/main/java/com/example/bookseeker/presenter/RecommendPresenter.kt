package com.example.bookseeker.presenter

import android.util.Log
import com.example.bookseeker.contract.RecommendContract

class RecommendPresenter : RecommendContract.Presenter{
    private var recommendView: RecommendContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: RecommendContract.View) {
        recommendView = view
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