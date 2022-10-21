package com.fictivestudios.lakoda.apiManager.response

data class BuyBundleResponse(
    val `data`: BuyBundleData,
    val message: String,
    val status: Int
)