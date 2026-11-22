package com.example.vacupet.data.model

import com.google.gson.annotations.SerializedName

data class Breed(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("temperament") val temperament: String?,
    @SerializedName("origin") val origin: String?,
    @SerializedName("life_span") val lifeSpan: String?,
    @SerializedName("bred_for") val bredFor: String?,
    @SerializedName("breed_group") val breedGroup: String?,
    @SerializedName("weight") val weight: MetricValue?,
    @SerializedName("height") val height: MetricValue?,
    @SerializedName("image") val image: BreedImage?
)

data class MetricValue(
    @SerializedName("imperial") val imperial: String?,
    @SerializedName("metric") val metric: String?
)

data class BreedImage(
    @SerializedName("id") val id: String?,
    @SerializedName("url") val url: String?
)
