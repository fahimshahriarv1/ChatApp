package com.example.chatappstarting.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.chatappstarting.presentation.ui.login.LoginViewModel
import com.example.chatappstarting.presentation.ui.login.ui.LoginScreen

@Composable
fun NavGraph(
    startDest: String
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDest) {
        navigation(
            route = Route.AppAuth.route,
            startDestination = Route.LoginScreen.route
        ) {
            composable(route = Route.LoginScreen.route) {
                val vm: LoginViewModel = hiltViewModel()
                LoginScreen(
                    uname = vm.uname,
                    pass = vm.pass,
                    isError = vm.isError,
                    unameChanged = vm::onUnameChanged,
                    passChanged = vm::onPassChanged,
                    onLoginClicked = vm::onLoginClicked
                )
            }
        }
    }
}