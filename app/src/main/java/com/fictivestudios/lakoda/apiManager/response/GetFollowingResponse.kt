package com.fictivestudios.lakoda.apiManager.response

data class GetFollowingResponse(
    val `data`: List<GetFollowingData>,
    val message: String,
    val status: Int
)