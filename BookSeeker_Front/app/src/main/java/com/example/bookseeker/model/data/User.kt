package com.example.bookseeker.model.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable


// 회원가입 데이터 생성 객체
data class Register(
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("password") val password: String
) : Serializable

// 로그인 데이터 생성 객체
data class Login(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)