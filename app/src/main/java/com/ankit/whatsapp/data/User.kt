package com.ankit.whatsapp.data

data class User(
    var uid: String = "",
    var email: String = "",
    var displayName: String = "",
    var onlineStatus: Boolean = false,
)
