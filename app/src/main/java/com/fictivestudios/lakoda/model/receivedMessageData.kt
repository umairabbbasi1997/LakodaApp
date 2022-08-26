package com.fictivestudios.lakoda.model

data class receivedMessageData(
    val conversation_id: Int,
    val created_at: Any,
    val delete_convo: Int,
    val id: Int,
    val image: String,
    val message: String,
    val name: String,
    val read_at: Any,
    val receiver_id: Int,
    val sender_id: Int,
    val status: Any,
    val type: String,
    val updated_at: Any
)