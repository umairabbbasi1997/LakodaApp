package com.fictivestudios.lakoda.apiManager.response

data class GetStoryResponse(
    val `data`: List<GetStoryData>,
    val message: String,
    val status: Int
)