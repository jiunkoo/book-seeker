package com.example.bookseeker.model.data

import android.os.Parcel
import android.os.Parcelable
import com.example.bookseeker.adapter.AdapterConstants
import com.example.bookseeker.adapter.ViewType

//data class BookData(
//    // @SerializedName Annotation :  JSON 응답에서 각각의 필드 구분 위해 사용
//    @SerializedName("title") @Expose var title: String,
//    @SerializedName("author") @Expose var author: String,
//    @SerializedName("publisher") @Expose var publisher: String,
//    @SerializedName("image") @Expose var image: String
////    @SerializedName("rating") @Expose var bookRating: Float
//) : Serializable

data class Book(
    val after: String?,
    val before: String?,
    val book: List<BookData>?) : Parcelable {
    companion object {
        @Suppress("unused")
        @JvmField val CREATOR: Parcelable.Creator<Book> = object : Parcelable.Creator<Book> {
            override fun createFromParcel(source: Parcel): Book = Book(source)
            override fun newArray(size: Int): Array<Book?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readString(), source.readString(), source.createTypedArrayList(BookData.CREATOR))

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(after)
        dest?.writeString(before)
        dest?.writeTypedList(book)
    }
}

data class BookData(
    val title: String?,
    val author: String?,
    val publisher: String?,
    val image: String?,
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
        dest?.writeString(image)
        dest?.writeFloat(rating)
    }
}