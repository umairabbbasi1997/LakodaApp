package com.fictivestudios.lakoda.apiManager.response

data class ChatAttachmentResponse(
    val `data`: ChatAttachmentData,
    val message: String,
    val status: Int
)