package com.example.bookseeker.presenter

import android.util.Log
import com.example.bookseeker.contract.SlashContract

class SlashPresenter : SlashContract.Presenter {
    private var slashView: SlashContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: SlashContract.View) {
        slashView = view
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        slashView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
}