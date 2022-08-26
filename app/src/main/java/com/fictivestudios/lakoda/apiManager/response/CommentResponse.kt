package com.fictivestudios.lakoda.apiManager.response

data class CommentResponse(
    val `data`: CommentData,
    val message: String,
    val status: Int
)