package com.example.bookseeker.model.data

import android.os.Parcelable
import com.example.bookseeker.adapter.AdapterConstants
import com.example.bookseeker.adapter.ViewType
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

// @Parcelize Annotation 사용
//Android Kotlin Extension의 exmerimental 기능을 적용한 것
@Parcelize
data class BookList(
    // MutableList 쓰면 객체 하나하나에 접근 가능
    val results: ArrayList<BookData>) : Parcelable {}

@Parcelize
data class BookData(
    val bsin: String,
    val title: String,
    val author: String,
    val publisher: String,
    val introduction: String,
    val cover: String,
    val link: String,
    val keyword: String,
    val adult: String,
    val genre: String,
    val publication_date: String,
    val rating: Float
) : ViewType, Parcelable {
    override fun getViewType() = AdapterConstants.BOOKS
}



data class BooksSearch(
    @SerializedName("keyword") val keyword: String
) : Serializable