package com.fictivestudios.lakoda.apiManager.response

data class GetLikesData(
    val created_at: String,
    val id: Int,
    val post_id: Int,
    val share_id: Any,
    val updated_at: String,
    val user: User,
    val user_id: Int
)