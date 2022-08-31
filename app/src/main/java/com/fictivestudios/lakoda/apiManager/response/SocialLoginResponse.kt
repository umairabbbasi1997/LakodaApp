package com.fictivestudios.lakoda.apiManager.response

data class SocialLoginResponse(
    val bearer_token: String,
    val `data`: SocialLoginData,
    val message: String,
    val status: Int
)