package com.fictivestudios.lakoda.apiManager.response

data class LoginResponse(
    val `data`: VerifyOtpData,
    val message: String,
    val status: Int
)