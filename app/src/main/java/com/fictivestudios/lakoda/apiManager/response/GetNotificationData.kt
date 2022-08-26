package com.fictivestudios.lakoda.apiManager.response

data class GetNotificationData(
    val created_at: String,
    val id: Int,
    val message: String,
    val post_id: Int,
    val receiver_id: Int,
    val sender: SenderX,
    val sender_id: Int,
    val share_id: Int,
    val type: String,
    val updated_at: String
)