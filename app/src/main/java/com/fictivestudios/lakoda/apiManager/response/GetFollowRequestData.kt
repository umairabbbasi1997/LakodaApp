package com.fictivestudios.lakoda.apiManager.response

data class GetFollowRequestData(
    val accept: String,
    val created_at: String,
    val follower: FollowerUser,
    val id: Int
)