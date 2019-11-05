package com.example.bookseeker.network

import com.example.bookseeker.model.data.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
//    @GET("/SignUpInfo")
//    fun selectOneSignUpDataByEmail(@Query("email") email: String): Call<List<SignUpData>>

    @POST("/account/api/auth/register")
    fun insertUserData(@Body UserData: UserData): Call<ResponseBody>

    @POST("/account/api/auth/login")
    fun loginCheck(@Body loginData: LoginData): Call<LoginResult>

    @GET("/book/BookInfo")
    fun selectAllSearchResultData(@Query("category") category: Int, @Query("page") page: Int,
                                  @Query("page_size") page_size: Int,
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
