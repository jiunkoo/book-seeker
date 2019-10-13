package com.example.bookseeker.network

import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.model.data.BookList
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

    @GET("/BookInfo")
    fun selectAllSearchResultData(@Query("category") category: Int, @Query("page") page: Int,
                                  @Query("searchWord") searchWord: String): Call<BookList>

    @GET("/ComicInfo")
    fun selectAllComicData(@Query("category") category: Int, @Query("page") page: Int,
                            @Query("page_size") page_size: Int): Call<BookList>

    @GET("/RomanceInfo")
    fun selectAllRomanceData(@Query("page") page: Int, @Query("page_size") page_size: Int): Call<BookList>

    @GET("/FantasyInfo")
    fun selectOneFantasyDataByTitle(@Query("title") title: String): Call<List<BookData>>

    @GET("/FantasyInfo")
    fun selectAllFantasyData(@Query("page") page: Int, @Query("page_size") page_size: Int): Call<BookList>
}
