package com.example.bookseeker.view

interface BaseView{
    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    fun showMessage(msg : String)

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    fun executionLog(tag: String, msg: String)
}