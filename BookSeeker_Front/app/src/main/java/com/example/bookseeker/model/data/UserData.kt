package com.example.bookseeker.model.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class UserData(
    // @SerializedName Annotation :  JSON 응답에서 각각의 필드 구분 위해 사용
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
) : Serializable

data class LoginData(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

@Parcelize
data class LoginResult(
    val status: String?,
    val loginUser: LoginUser,
    val token: String
): Parcelable {}

@Parcelize
data class LoginUser(
    val id: String,
    val email: String,
    val username: String
) : Parcelable{}