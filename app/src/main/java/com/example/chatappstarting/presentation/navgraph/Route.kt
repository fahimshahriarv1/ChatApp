package com.example.chatappstarting.presentation.navgraph

sealed class Route(
    val route: String
) {
    object AppAuth : Route(route = "appAuth")
    object LoginScreen : Route(route = "loginScreen")
    object HomeScreen : Route(route = "homeScreen")
}