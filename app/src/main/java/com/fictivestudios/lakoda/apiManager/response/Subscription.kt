package com.fictivestudios.lakoda.apiManager.response

data class Subscription(
    val bundle_id: Int,
    val created_at: String,
    val expiry_date: String,
    val id: Int,
    val post_id: String,
    val receipt: String,
    val source: String,
    val updated_at: String,
    val user_id: Int
)