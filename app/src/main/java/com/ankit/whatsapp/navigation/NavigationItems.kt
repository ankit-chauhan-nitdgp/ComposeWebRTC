package com.ankit.whatsapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import com.ankit.whatsapp.utils.PagesRouteNames

val navigationItems = listOf(
    NavigationItem(
        title = "Chat",
        icon = Icons.AutoMirrored.Filled.List,
        route = PagesRouteNames.CHAT_PAGE
    ),
    NavigationItem(
        title = "Call",
        icon = Icons.Default.Call,
        route = PagesRouteNames.CALL_PAGE
    ),
    NavigationItem(
        title = "Profile",
        icon = Icons.Default.Person,
        route = PagesRouteNames.PROFILE_PAGE
    )
)