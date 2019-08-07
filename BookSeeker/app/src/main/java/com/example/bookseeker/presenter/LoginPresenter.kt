package com.example.bookseeker.presenter

import com.example.bookseeker.contract.LoginContract

class LoginPresenter : LoginContract.Presenter {
    private var loginView: LoginContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: LoginContract.View) {
        loginView = view
    }

    // loginCheck : View에서 Email과 Password를 받아와 일치 여부를 비교하는 함수
    override fun loginCheck(inputEmail: String?, inputPassword: String?): Int {
        var inputMatchConfirm = -1

        println("inputEmail: $inputEmail and inputPassword: $inputPassword")
        // 이메일과 비밀번호가 둘 다 일치하지 않을 경우
        if (inputEmail != "admin" && inputPassword != "1111") {
            inputMatchConfirm = 0
            println("둘 다 불일치")
        }
        // 이메일과 비밀번호 중 하나가 일치하는 경우
        else if (inputEmail == "admin" && inputPassword != "1111" || inputEmail != "admin" && inputPassword == "1111") {
            inputMatchConfirm = 1
            println("하나는 일치")
        } 
        // 이메일과 비밀번호 둘 다 일치하는 경우
        else if (inputEmail == "admin" && inputPassword == "1111"){
            inputMatchConfirm = 2
            println("둘 다 일치")
        } 
        // 그 외의 경우
        else {
            inputMatchConfirm = -1
        }
        return inputMatchConfirm
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        loginView = null
    }
}