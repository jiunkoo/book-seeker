package com.example.bookseeker.view

interface BaseView{
<<<<<<< HEAD
    // setProgressON :  공통으로 사용하는 Progress Bar의 시작을 정의하는 함수
    fun setProgressON(msg: String)

    // setProgressOFF() : 공통으로 사용하는 Progress Bar의 종료를 정의하는 함수
    fun setProgressOFF()

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    fun showMessage(msg : String)

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    fun executionLog(tag: String, msg: String)
=======
    // showError : 공통으로 쓰이는 error 출력 부분을 생성하는 함수
    fun showError(error : String)
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
}