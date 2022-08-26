package com.fictivestudios.lakoda.apiManager.response

data class GetLikesResponse(
    val `data`: List<GetLikesData>,
    val message: String,
    val status: Int
)