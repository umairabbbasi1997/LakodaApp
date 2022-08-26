package com.fictivestudios.lakoda.apiManager.response

data class GetFollowRequest(
    val `data`: List<GetFollowRequestData>,
    val message: String,
    val status: Int
)