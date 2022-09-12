package com.fictivestudios.lakoda.model

data class ReceivedLastMessage(
    val conversation_id: Int,
    val created_at: String,
    val delete_convo: Int,
    val device_token: String,
    val id: Int,
    val image: String,
    val message: String,
    val name: String,
    val read_at: Any,
    val receiver_id: Int,
    val sender_id: Int,
    val status: Any,
    val thumbnail: String,
    val type: String,
    val updated_at: String
)