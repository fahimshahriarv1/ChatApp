package com.example.chatappstarting.presentation.navgraph

import com.example.chatappstarting.constants.MOBILE_NUMBER

sealed class Route(
    val route: String,
    vararg params: String
) {
    val fullRoute: String = if (params.isEmpty()) route else {
        val builder = StringBuilder(route)
        params.forEach { builder.append("/{${it}}") }
        builder.toString()
    }

    object AppAuth : Route(route = "appAuth")
    object AppSignUp : Route(route = "appSignUp")
    object AppMain : Route(route = "appMain")
    object HomeScreen : Route(route = "homeScreen")
    object LoginScreen : Route(route = "loginScreen")
    object SignUpMobileScreen : Route(route = "signUpMobileScreen")
    object SignUpOtpScreen : Route(route = "signUpOtpScreen", MOBILE_NUMBER) {
        operator fun invoke(mobileNumber: String) =
            route.appendParams(MOBILE_NUMBER to mobileNumber)
    }

    object SignUpPasswordScreen :
        Route(route = "signUpPasswordScreen", MOBILE_NUMBER) {
        operator fun invoke(mobileNumber: String) =
            route.appendParams(MOBILE_NUMBER to mobileNumber)
    }
}

internal fun String.appendParams(vararg params: Pair<String, Any?>): String {
    val builder = StringBuilder(this)

    params.forEach {
        it.second?.toString()?.let { arg ->
            builder.append("/$arg")
        }
    }

    return builder.toString()
}