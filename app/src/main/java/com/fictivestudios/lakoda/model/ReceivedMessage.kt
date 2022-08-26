package com.fictivestudios.lakoda.model

data class ReceivedMessage(
    val `data`: List<receivedMessageData>,
    val object_type: String
)