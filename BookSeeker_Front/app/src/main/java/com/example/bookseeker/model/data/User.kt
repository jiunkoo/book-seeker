package com.example.bookseeker.model.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class Register(
    // @SerializedName Annotation :  JSON 응답에서 각각의 필드 구분 위해 사용
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("password") val password: String
) : Serializable

data class Login(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class WordCloud(
    @SerializedName("keyword") val keyword: String,
    @SerializedName("size") val size: String
)