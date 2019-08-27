package com.example.bookseeker.network

import com.example.bookseeker.model.data.SignUpData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @GET("/SignUpInfo")
    fun selectOneSignUpDataByEmail(@Query("email") email: String): Call<List<SignUpData>>
//    fun checkEmailDuplication(@Query("email") email: String): Call<SignUpData>

    @POST("/SignUpInfo/")
    fun insertSignUpData(@Body signUpData: SignUpData): Call<ResponseBody>
}
