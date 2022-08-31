package com.fictivestudios.lakoda.apiManager.response

data class SocialLoginData(
    val city: String,
    val country_code: String,
    val created_at: String,
    val device_token: String,
    val device_type: String,
    val email: String,
    val id: Int,
    val id_card: Any,
    val image: String,
    val is_active: String,
    val is_forgot: String,
    val is_verified: String,
    val name: String,
    val phone_number: Any,
    val profile_complete: String,
    val social_token: String,
    val social_type: String,
    val verfied_code: Any
)