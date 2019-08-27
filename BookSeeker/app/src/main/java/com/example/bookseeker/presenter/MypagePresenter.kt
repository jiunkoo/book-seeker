package com.example.bookseeker.presenter

import android.util.Log
import com.example.bookseeker.contract.MypageContract

class MypagePresenter : MypageContract.Presenter {
    private var mypageView: MypageContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: MypageContract.View) {
        mypageView = view
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        mypageView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}