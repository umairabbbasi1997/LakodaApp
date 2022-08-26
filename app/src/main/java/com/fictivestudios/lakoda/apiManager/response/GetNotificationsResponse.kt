package com.fictivestudios.lakoda.apiManager.response

data class GetNotificationsResponse(
    val `data`: List<GetNotificationData>,
    val message: String,
    val status: Int
)