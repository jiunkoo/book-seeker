package com.example.bookseeker.network

import com.example.bookseeker.model.data.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    // 회원가입
    @POST("/users/register")
    fun register(@Body register: Register): Call<JsonObject>

    // 로그인
    @POST("/users/login")
    fun login(@Body login: Login): Call<JsonObject>

    // 도서 검색
    @POST("/books/search/{filter}/{page}/{limit}")
    fun booksSearch(@Body booksSearch: BooksSearch, @Path("filter") filter: Int, @Path("page") page: Int, @Path("limit") limit: Int): Call<JsonObject>

    // 모든 도서 조회
    @GET("/books/{genre}/{filter}/{page}/{limit}")
    fun getBooks(@Path("genre") genre: String, @Path("filter") filter: Int, @Path("page") page: Int, @Path("limit") limit: Int): Call<JsonObject>

    // 하나의 평가 데이터 생성
    @POST("/evaluation")
    fun createEvaluation(@Body evaluationCreate: EvaluationCreate): Call<JsonObject>

    // 모든 평가 데이터 조회
    @GET("evaluation/{genre}/{filter}/{page}/{limit}")
    fun getEvaluations(@Path("genre") genre: String, @Path("filter") filter: Int, @Path("page") page: Int, @Path("limit") limit: Int): Call<JsonObject>

    // 하나의 평가 데이터 조회
    @GET("/evaluation/{bsin}")
    fun getEvaluation(@Path("bsin") bsin: String): Call<JsonObject>

    // 하나의 평가 데이터 수정
    @PATCH("/evaluation")
    fun patchEvaluation(@Body evaluationPatch: EvaluationPatch): Call<JsonObject>

    // 하나의 평가 데이터 삭제
    @DELETE("/evaluation/{bsin}")
    fun deleteEvaluation(@Path("bsin") bsin: String): Call<JsonObject>
}
