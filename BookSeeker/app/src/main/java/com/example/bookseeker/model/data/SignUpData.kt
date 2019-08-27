package com.example.bookseeker.model.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SignUpData(
    // @SerializedName Annotation :  JSON 응답에서 각각의 필드 구분 위해 사용
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String
) : Serializable

//data class SignUpData(var email: String, var name: String, var password: String)