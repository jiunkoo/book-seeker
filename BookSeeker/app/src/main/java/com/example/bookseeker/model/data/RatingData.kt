package com.example.bookseeker.model.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//data class RatingData(
//    // @SerializedName Annotation :  JSON 응답에서 각각의 필드 구분 위해 사용
//    @SerializedName("title") @Expose var title: String,
//    @SerializedName("author") @Expose var author: String,
//    @SerializedName("publisher") @Expose var publisher: String,
//    @SerializedName("image") @Expose var image: String
////    @SerializedName("rating") @Expose var bookRating: Float
//) : Serializable

class BookListResponse(
    var page: Int,
    var page_size: Int,
    val results: List<BookDataResponse>
)

class BookDataResponse(
    val title: String,
    val author: String,
    val publisher: String,
    val image: String?,
    val rating: Float
)