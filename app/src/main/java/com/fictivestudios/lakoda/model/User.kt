package com.fictivestudios.lakoda.model

data class User(
    val created_at: String,
    val device_token: String,
    val device_type: String,
    val email: String,
    val id: Int,
    val id_card: String,
    val image: String,
    val is_active: String,
    val is_forgot: String,
    val is_verified: String,
    val name: String,
    val phone_number: String,
    val country_code: String,
    val city: String,
    val profile_complete: String,
    val social_token: Any,
    val social_type: Any,
    val verfied_code: Any
)