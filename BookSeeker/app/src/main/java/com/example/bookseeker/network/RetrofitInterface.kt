package com.example.bookseeker.network

import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookDataResponse
import com.example.bookseeker.model.data.BookListResponse
import com.example.bookseeker.model.data.SignUpData
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitInterface {

    @GET("/SignUpInfo")
    fun selectOneSignUpDataByEmail(@Query("email") email: String): Call<List<SignUpData>>
//    fun checkEmailDuplication(@Query("email") email: String): Call<SignUpData>

    @POST("/SignUpInfo/")
    fun insertSignUpData(@Body signUpData: SignUpData): Call<ResponseBody>

    @GET("/RomanceInfo")
    fun selectOneRomanceDataByTitle(@Query("title") title: String): Call<List<BookData>>

    @GET("/RomanceInfo")
    //    fun selectAllRomanceData(@Query("after") after: String, @Query("limit") limit: String): Call<BookListResponse>
    fun selectAllRomanceData(@Query("page") page: Int, @Query("page_size") page_size: Int): Call<BookListResponse>

    @GET("/FantasyInfo")
    fun selectOneFantasyDataByTitle(@Query("title") title: String): Call<List<BookData>>

    @GET("/ComicInfo")
    fun selectOneComicDataByTitle(@Query("title") title: String): Call<List<BookData>>
}
