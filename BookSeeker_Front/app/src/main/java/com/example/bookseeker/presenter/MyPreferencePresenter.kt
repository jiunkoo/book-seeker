package com.example.bookseeker.presenter

import android.util.Log
import com.example.bookseeker.contract.MyPreferenceContract
import com.example.bookseeker.contract.MypageContract

class MyPreferencePresenter : MyPreferenceContract.Presenter {
    private var myPreferenceView: MyPreferenceContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: MyPreferenceContract.View) {
        myPreferenceView = view
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        myPreferenceView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}