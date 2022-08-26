package com.fictivestudios.lakoda.apiManager.response

import com.fictivestudios.lakoda.model.User

data class VerifyOtpData(
    val bearer_token: String,
    val user: User
)