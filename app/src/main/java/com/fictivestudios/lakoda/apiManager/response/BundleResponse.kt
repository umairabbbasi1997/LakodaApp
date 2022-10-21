package com.fictivestudios.lakoda.apiManager.response

data class BundleResponse(
    val `data`: List<BundleData>,
    val message: String,
    val status: Int
)