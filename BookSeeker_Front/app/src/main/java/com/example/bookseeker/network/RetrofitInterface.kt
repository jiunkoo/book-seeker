package com.example.bookseeker.network

import com.example.bookseeker.model.data.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    // 회원가입
    @POST("/users/register")
    fun register(@Body registerRequest: RegisterRequest): Call<JsonObject>

    // 로그인
    @POST("/users/login")
    fun login(@Body loginRequest: LoginRequest): Call<JsonObject>

    // 도서 검색
    @POST("/books/search/{filter}/{page}/{limit}")
    fun booksSearch(@Body searchRequest: SearchRequest, @Path("filter") filter: Int, @Path("page") page: Int, @Path("limit") limit: Int): Call<JsonObject>

    // 도서 목록 조회
    @GET("/books/{genre}/{filter}/{page}/{limit}")
    fun getBooks(@Path("genre") genre: String, @Path("filter") filter: Int, @Path("page") page: Int, @Path("limit") limit: Int): Call<JsonObject>
}
