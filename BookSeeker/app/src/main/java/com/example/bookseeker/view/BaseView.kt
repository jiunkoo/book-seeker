package com.example.bookseeker.view

interface BaseView{
    // setProgressON :  공통으로 사용하는 Progress Bar의 시작을 정의하는 함수
    fun setProgressON(msg: String)

    // setProgressOFF() : 공통으로 사용하는 Progress Bar의 종료를 정의하는 함수
    fun setProgressOFF()

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    fun showMessage(msg : String)

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    fun executionLog(tag: String, msg: String)
}