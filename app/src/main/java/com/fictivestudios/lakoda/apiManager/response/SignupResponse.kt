package com.fictivestudios.lakoda.apiManager.response

data class SignupResponse(
    val `data`: SignupData,
    val message: String,
    val status: Int
)