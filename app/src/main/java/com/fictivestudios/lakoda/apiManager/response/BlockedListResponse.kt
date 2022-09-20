package com.fictivestudios.lakoda.apiManager.response

data class BlockedListResponse(
    val `data`: List<BlockedListData>,
    val message: String,
    val status: Int
)