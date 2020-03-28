package com.example.bookseeker.model.data

import android.os.Parcelable
import com.example.bookseeker.adapter.AdapterConstants
import com.example.bookseeker.adapter.ViewType
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


// 도서 목록 데이터 생성 객체
@Parcelize
data class BookList(
    var page: Int,
    val results: ArrayList<BookData>) : Parcelable

// 도서 데이터 생성 객체
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
    var rating: Float,
    var state: Int
) : ViewType, Parcelable {
    override fun getViewType() = AdapterConstants.BOOKS
}

// 도서 검색 데이터 생성 객체
data class BooksSearch(
    @SerializedName("keyword") val keyword: String
) : Serializable