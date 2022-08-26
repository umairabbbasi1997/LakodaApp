package com.fictivestudios.lakoda.apiManager.response

data class HomePostData(
    val comment_count: Int,
    val created_at: String,
    val description: String,
    val id: Int,
    val is_post: Int,
    val like_count: Int,
    val share_count: Int,
    val shared_by: SharedBy,
    val title: String,
    val type: String,
    val user: User,
    val video_image: String,
    val is_liked: Int
)