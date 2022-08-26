package com.fictivestudios.lakoda.apiManager.response

data class ForgetPasswordResponse(
    val `data`: ForgetPassData,
    val message: String,
    val status: Int
)