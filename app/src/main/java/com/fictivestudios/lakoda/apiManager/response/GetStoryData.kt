package com.fictivestudios.lakoda.apiManager.response

data class GetStoryData(
    val caption: String,
    val created_at: String,
    val id: Int,
    val image: String,
    val updated_at: String,
    val user_id: Int,
    val user: StoryUser
)