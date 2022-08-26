package com.fictivestudios.lakoda.apiManager.response

data class UpdateProfileResponse(
    val `data`: UpdateProfileData,
    val message: String,
    val status: Int
)