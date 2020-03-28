package com.example.bookseeker.network

import android.content.Context
import android.content.SharedPreferences
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response


class AddCookiesInterceptor : Interceptor {
    internal lateinit var pref: SharedPreferences
    internal var context: Context

    constructor(context: Context) {
        this.context = context
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        pref = context.getSharedPreferences("shared_pref", 0)

        // Preference에서 cookies를 가져옴
        builder.addHeader("Cookie", "connect.sid=" + pref.getString("Cookie", "NONE")!!)
        builder.addHeader("Content-Type", "application/json")

        // Web/Android/iOS 구분을 위해 User-Agent 세팅
        builder.removeHeader("User-Agent").addHeader("User-Agent", "Android")
        return chain.proceed(builder.build())
    }
}