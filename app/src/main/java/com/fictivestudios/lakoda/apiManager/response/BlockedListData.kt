package com.fictivestudios.lakoda.apiManager.response

data class BlockedListData(
    val blocked_user_id: Int,
    val blockuser: Blockuser,
    val created_at: String,
    val id: Int,
    val updated_at: String,
    val user_id: Int
)