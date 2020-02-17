package com.example.bookseeker.model.data

import android.os.Parcelable
import com.example.bookseeker.adapter.AdapterConstants
import com.example.bookseeker.adapter.ViewType
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

// @SerealizedName Annotation 사용
/*
data class BookData(
    // @SerializedName Annotation :  JSON 응답에서 각각의 필드 구분 위해 사용
    @SerializedName("title") @Expose var title: String,
    @SerializedName("author") @Expose var author: String,
    @SerializedName("publisher") @Expose var publisher: String,
    @SerializedName("cover") @Expose var cover: String
    @SerializedName("rating") @Expose var bookRating: Float
) : Serializable
*/

// Parcelable 사용
/*
data class BookList(
    val page: Int,
    val results: List<BookData>?) : Parcelable {
    companion object {
        @Suppress("unused")
        @JvmField val CREATOR: Parcelable.Creator<BookList> = object : Parcelable.Creator<BookList> {
            override fun createFromParcel(source: Parcel): BookList = BookList(source)
            override fun newArray(size: Int): Array<BookList?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readInt(), source.createTypedArrayList(BookData.CREATOR))

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(page)
        dest?.writeTypedList(results)
    }
}

data class BookData(
    val title: String?,
    val author: String?,
    val publisher: String?,
    val cover: String?,
    val rating: Float
) : ViewType, Parcelable {

    override fun getViewType() = AdapterConstants.BOOKS

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<BookData> = object : Parcelable.Creator<BookData> {
            override fun createFromParcel(source: Parcel): BookData = BookData(source)
            override fun newArray(size: Int): Array<BookData?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readString(), source.readString(), source.readString(), source.readString(), source.readFloat())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeString(author)
        dest?.writeString(publisher)
        dest?.writeString(cover)
        dest?.writeFloat(rating)
    }
}
*/

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
data class SearchRequest(
    @SerializedName("keyword") val keyword: String
) : Serializable