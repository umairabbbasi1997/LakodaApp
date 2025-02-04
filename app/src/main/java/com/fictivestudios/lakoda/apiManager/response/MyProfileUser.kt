package com.fictivestudios.lakoda.apiManager.response

data class MyProfileUser(
    val city: String,
    val country_code: Any,
    val created_at: String,
    val device_token: String,
    val device_type: String,
    val email: String,
    val follower_count: Int,
    val following_count: Int,
    val id: Int,
    val id_card: String,
    val image: String,
    val is_active: String,
    val is_blocked: String,
    val is_forgot: String,
    val is_verified: String,
    val name: String,
    val phone_number: String,
    val post_count: Int,
    val share_count: Int,
    val posts: List<HomePostData>,
    val profile_complete: String,
    val social_token: String,
    val social_type: String,
    val updated_at: String,
    val verified_code: String,
    val is_followed: Int,
    val follow_status:String,
    val post_post_count :Int
)