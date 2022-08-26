package com.fictivestudios.lakoda.apiManager.response

data class ContentResponse(
    val `data`: ContentData,
    val message: String,
    val status: Int
)