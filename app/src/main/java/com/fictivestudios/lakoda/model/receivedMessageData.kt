package com.fictivestudios.lakoda.model

data class receivedMessageData(
    val conversation_id: Int,
    val created_at: String,
    val delete_convo: Int,
    val id: Int,
    val image: String,
    val message: String,
    val name: String,
    val read_at: String,
    val receiver_id: Int,
    val sender_id: Int,
    val status: Any,
    val type: String,
    val updated_at: String
)