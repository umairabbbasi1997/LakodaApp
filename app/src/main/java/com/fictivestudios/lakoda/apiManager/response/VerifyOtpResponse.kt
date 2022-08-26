package com.fictivestudios.lakoda.apiManager.response

data class VerifyOtpResponse(
    val `data`: VerifyOtpData,
    val message: String,
    val status: Int
)