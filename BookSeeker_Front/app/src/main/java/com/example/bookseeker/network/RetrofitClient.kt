package com.example.bookseeker.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    const val BASE_URL = "https://hexanovem.com"

    // Http 요청 및 응답 로그
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Retrofit Client에 필요한 Interceptor 추가
    fun getClient(context: Context, type: String): OkHttpClient {
        // 서버에 요청할 때 쿠키를 보내는 경우
        if (type.equals("addCookie")) {
            val addCookiesInterceptor = AddCookiesInterceptor(context)
            val client = OkHttpClient.Builder()
                .addInterceptor(addCookiesInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
            return client
        }
        // 서버에 응답받은 쿠키를 저장하는 경우
        else if (type.equals("receiveCookie")) {
            val receivedCookiesInterceptor = ReceivedCookiesInterceptor(context)
            val client = OkHttpClient.Builder()
                .addInterceptor(receivedCookiesInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
            return client
        }
        // 쿠키를 주고받지 않는 경우
        else {
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            return client
        }

    }

    //retrofit 선언
    fun retrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // RetrofitInterface와 연결하여 Retrofit 생성
    fun retrofitInterface (client: OkHttpClient): RetrofitInterface =
        retrofit(client).create(RetrofitInterface::class.java)
}