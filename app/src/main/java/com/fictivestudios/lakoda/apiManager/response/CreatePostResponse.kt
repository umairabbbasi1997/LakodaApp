package com.fictivestudios.lakoda.apiManager.response

data class CreatePostResponse(
    val `data`: Post,
    val message: String,
    val status: Int
)