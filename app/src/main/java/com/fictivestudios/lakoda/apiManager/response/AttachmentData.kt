package com.fictivestudios.lakoda.apiManager.response

data class AttachmentData(
    val message: String,
    val receiver_id: String,
    val sender_id: String,
    val thumbnail: String,
    val type: String
)