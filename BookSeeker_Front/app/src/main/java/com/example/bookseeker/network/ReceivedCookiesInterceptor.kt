package com.example.bookseeker.network

import android.content.Context
import android.content.SharedPreferences
import java.io.IOException
import java.util.HashSet
import okhttp3.Interceptor
import okhttp3.Response

class ReceivedCookiesInterceptor : Interceptor {
    internal lateinit var pref: SharedPreferences
    internal var context: Context

    constructor(context: Context) {
        this.context = context
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = HashSet<String>()
            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }
            // ShardPreference에 쿠키 값 집어넣기
            val splitCookieHeader = cookies.toString().split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val splitCookieValue = splitCookieHeader[0].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            pref = context.getSharedPreferences("shared_pref", 0)
            val editor = pref.edit()
            editor.putString("Cookie", splitCookieValue[1])
            editor.apply() // editor.commit()

            println("sharedPreference에 저장된 쿠키 값은 : " + pref.getString("Cookie", "NONE"))
        }
        return originalResponse
    }
}