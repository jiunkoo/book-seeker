package com.example.bookseeker.presenter

interface BasePresenter<T>{
    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    fun takeView(view: T)
    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    fun dropView()
}