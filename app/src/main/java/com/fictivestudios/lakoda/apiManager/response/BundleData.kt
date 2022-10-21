package com.fictivestudios.lakoda.apiManager.response

data class DataX(
    val cost: Int,
    val created_at: String,
    val description: String,
    val duration: String,
    val id: Int,
    val name: String,
    val promotion: PromotionX,
    val promotion_id: Int,
    val total: Int,
    val updated_at: String
)