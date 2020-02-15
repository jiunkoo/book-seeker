package com.example.bookseeker.network

import com.example.bookseeker.model.data.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @POST("/users/register")
    fun insertUserData(@Body registerRequest: RegisterRequest): Call<JsonObject>

    @POST("/users/login")
    fun loginCheck(@Body loginRequest: LoginRequest): Call<JsonObject>

    @GET("/book/BookInfo")
    fun selectAllSearchResultData(@Query("category") category: Int, @Query("page") page: Int,
                                  @Query("page_size") page_size: Int,
                                  @Query("searchWord") searchWord: String): Call<BookList>

    @GET("/book/ComicInfo")
    fun selectAllComicData(@Query("category") category: Int, @Query("page") page: Int,
                            @Query("page_size") page_size: Int): Call<BookList>

    @GET("/recommend/comic")
    fun selectAllComicRecommendData(@Query("userToken") userToken: String): Call<BookList>

    @GET("/book/RomanceInfo")
    fun selectAllRomanceData(@Query("page") page: Int, @Query("page_size") page_size: Int): Call<BookList>

    @GET("/book/FantasyInfo")
    fun selectOneFantasyDataByTitle(@Query("title") title: String): Call<List<BookData>>

    @GET("/book/FantasyInfo")
    fun selectAllFantasyData(@Query("page") page: Int, @Query("page_size") page_size: Int): Call<BookList>
}
