package com.fictivestudios.lakoda.apiManager.response

data class GetMyProfileResponse(
    val `data`: MyProfileData,
    val message: String,
    val status: Int
)