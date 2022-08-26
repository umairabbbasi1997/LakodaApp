package com.fictivestudios.lakoda.apiManager.response

data class GetCommentsData(
    val attachment: String,
    val comment: String,
    val created_at: String,
    val id: Int,
    val post_id: Int,
    val share_id: Any,
    val updated_at: String,
    val user: GetCommmentUser,
    val user_id: Int
)