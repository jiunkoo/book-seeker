package com.example.bookseeker.network

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    val retrofitInterface: RetrofitInterface
    //    companion object { val API_URL = "https://366f8b18.ngrok.io/" }
    const val BASE_URL = "https://429cb439.ngrok.io"

    init {
        var retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()) // @Body Annotation을 사용하기 위해 필요
            .build()
        retrofitInterface = retrofit.create(RetrofitInterface::class.java)
    }
}