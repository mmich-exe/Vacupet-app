package com.example.vacupet.data.model

data class LostPetPost(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val title: String = "",
    val description: String = "",
    val breedName: String = "",
    val localPhotoUri: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
