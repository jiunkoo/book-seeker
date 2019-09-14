package com.example.bookseeker.model.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RecommendData(
    // @SerializedName Annotation :  JSON 응답에서 각각의 필드 구분 위해 사용
    @SerializedName("bookImageUrl") val bookImageUrl: String,
    @SerializedName("bookTitle") val bookTitle: String,
    @SerializedName("bookAuthor") val bookAuthor: String,
    @SerializedName("bookPublisher") val bookPublisher: String,
    @SerializedName("bookRating") val bookRating: Float
) : Serializable

//data class RecommendData(var bookImage: Int, var bookTitle: String, var bookAuthor: String, var bookPublisher: String, var bookRating: Float)