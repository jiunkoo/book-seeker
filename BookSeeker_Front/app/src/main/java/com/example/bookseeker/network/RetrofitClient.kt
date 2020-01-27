package com.example.bookseeker.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val retrofitInterface: RetrofitInterface
    //    companion object { val API_URL = "https://366f8b18.ngrok.io/" }
    const val BASE_URL = "https://34884112.ngrok.io"

    init {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)

        var retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()) // @Body Annotation을 사용하기 위해 필요
            .build()
        retrofitInterface = retrofit.create(RetrofitInterface::class.java)
    }
}