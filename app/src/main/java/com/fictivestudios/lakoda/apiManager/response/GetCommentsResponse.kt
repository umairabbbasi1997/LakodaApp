package com.fictivestudios.lakoda.apiManager.response

data class GetCommentsResponse(
    val `data`: List<GetCommentsData>,
    val message: String,
    val status: Int
)