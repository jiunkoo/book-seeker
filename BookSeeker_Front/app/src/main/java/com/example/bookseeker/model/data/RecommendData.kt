package com.example.bookseeker.model.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RecommendData(
    // @SerializedName Annotation :  JSON 응답에서 각각의 필드 구분 위해 사용
    @SerializedName("bookImageUrl") @Expose var bookImageUrl: String,
    @SerializedName("bookTitle") @Expose var bookTitle: String,
    @SerializedName("bookAuthor") @Expose var bookAuthor: String,
    @SerializedName("bookPublisher") @Expose var bookPublisher: String,
    @SerializedName("bookRating") @Expose var bookRating: Float
) : Serializable

//data class RecommendData(var bookImage: Int, var bookTitle: String, var bookAuthor: String, var bookPublisher: String, var bookRating: Float)