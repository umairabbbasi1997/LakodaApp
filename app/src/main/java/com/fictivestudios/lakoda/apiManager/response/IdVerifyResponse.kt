package com.fictivestudios.lakoda.apiManager.response

data class IdVerifyResponse(
    val `data`: idVerifyData,
    val message: String,
    val status: Int
)