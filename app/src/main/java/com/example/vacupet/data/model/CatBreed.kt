package com.example.vacupet.data.model

import com.google.gson.annotations.SerializedName

data class CatBreed(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("temperament") val temperament: String?,
    @SerializedName("origin") val origin: String?,
    @SerializedName("life_span") val lifeSpan: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("weight") val weight: CatMetricValue?,
    @SerializedName("image") val image: CatBreedImage?
)

data class CatMetricValue(
    @SerializedName("imperial") val imperial: String?,
    @SerializedName("metric") val metric: String?
)

data class CatBreedImage(
    @SerializedName("id") val id: String?,
    @SerializedName("url") val url: String?
)
