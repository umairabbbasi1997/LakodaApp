package com.fictivestudios.lakoda.apiManager.response

data class GetChatListResponse(
    val `data`: List<GetChatListData>,
    val message: String,
    val status: Int
)