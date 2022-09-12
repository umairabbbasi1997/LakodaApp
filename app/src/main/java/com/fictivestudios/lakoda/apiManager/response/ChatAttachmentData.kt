package com.fictivestudios.lakoda.apiManager.response

data class ChatAttachmentData(
    val `data`: AttachmentData,
    val message: String,
    val status: Int
)