package com.fictivestudios.lakoda.apiManager.response

data class HomePostResponse(
    val `data`: List<HomePostData>,
    val message: String,
    val status: Int
)