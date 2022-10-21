package com.fictivestudios.lakoda.apiManager.response

data class BundleResponse(
    val `data`: List<DataX>,
    val message: String,
    val status: Int
)