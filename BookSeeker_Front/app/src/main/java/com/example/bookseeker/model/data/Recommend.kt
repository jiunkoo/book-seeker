package com.example.bookseeker.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecommendData(
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
    val rating: Float,
    var state: Int,
    val expect_rating: Float
) : Parcelable