package com.example.bookseeker.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val retrofitInterface: RetrofitInterface
    //    companion object { val API_URL = "https://366f8b18.ngrok.io/" }
    const val BASE_URL = "https://896c0ceb.ngrok.io"

    init {
        var retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // @Body Annotation을 사용하기 위해 필요
            .build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // RxJava 사용을 위해 필요한 Factory인듯
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
        retrofitInterface = retrofit.create(RetrofitInterface::class.java)
    }
}