package com.example.chatappstarting.presentation.navgraph

sealed class Route(
    val route: String
) {
    object AppAuth : Route(route = "appAuth")
    object AppSignUp : Route(route = "appSignUp")
    object AppMain : Route(route = "appMain")
    object HomeScreen : Route(route = "homeScreen")
    object LoginScreen : Route(route = "loginScreen")
    object SignUpMobileScreen : Route(route = "signUpMobileScreen")
    object SignUpOtpScreen : Route(route = "signUpOtpScreen")
    object SignUpPasswordScreen : Route(route = "signUpPasswordScreen")
}