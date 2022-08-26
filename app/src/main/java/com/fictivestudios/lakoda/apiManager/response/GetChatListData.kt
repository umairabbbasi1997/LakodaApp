package com.fictivestudios.lakoda.apiManager.response

data class GetChatListData(
    val id: Int,
    val last_message: String,
    val user: ChatUser
)