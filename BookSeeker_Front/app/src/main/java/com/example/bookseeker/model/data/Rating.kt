package com.example.bookseeker.model.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// 평가 데이터 생성 객체
data class EvaluationCreate(
    @SerializedName("bsin") val bsin: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("state") val state: Int
) : Serializable

// 평가 데이터 수정 객체
data class EvaluationPatch(
    @SerializedName("bsin") val bsin: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("state") val state: Int
) : Serializable