package com.example.chatappstarting.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.chatappstarting.presentation.ui.base.ComposableLieCycleImpl
import com.example.chatappstarting.presentation.ui.home.HomeViewModel
import com.example.chatappstarting.presentation.ui.home.views.HomeScreen
import com.example.chatappstarting.presentation.ui.login.LoginViewModel
import com.example.chatappstarting.presentation.ui.login.ui.LoginScreen
import com.example.chatappstarting.presentation.ui.signup.MobileNumberScreen
import com.example.chatappstarting.presentation.ui.signup.OtpVerificationScreen
import com.example.chatappstarting.presentation.ui.signup.PasswordScreen
import com.example.chatappstarting.presentation.ui.signup.SignUpViewModel

@Composable
fun NavGraph(
    startDest: String
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
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
                    onLoginClicked = vm::onLoginClicked,
                    onSignUoClicked = {
                        navigate(navController, Route.AppSignUp)
                    }
                )

                ComposableLieCycleImpl()
            }
        }

        navigation(
            route = Route.AppSignUp.route,
            startDestination = Route.SignUpMobileScreen.route
        ) {
            composable(route = Route.SignUpMobileScreen.route) {
                val vm: SignUpViewModel = hiltViewModel()
                MobileNumberScreen(
                    countryCode = vm.countryCode,
                    mobileNumber = vm.mobileNumber,
                    isError = vm.isMobileNumberError,
                    onCountryCodeClicked = vm::onCountryCodeSelected,
                    mobileNumberChanged = vm::onMobileNumberChanged,
                    onSendOtpClicked = {
                        navigate(navController, Route.SignUpOtpScreen)
                    },
                    navigateBack = {
                        navController.navigateUp()
                    }
                )

                ComposableLieCycleImpl()
            }
            composable(route = Route.SignUpOtpScreen.route) {
                val vm: SignUpViewModel = hiltViewModel()
                OtpVerificationScreen(
                    otp = vm.otp,
                    onOtpValueChanged = vm::onOtpChanged,
                    navigateBack = { navController.navigateUp() },
                    onOkClicked = {}
                )
            }

            composable(route = Route.SignUpPasswordScreen.route) {
                val vm: SignUpViewModel = hiltViewModel()
                PasswordScreen(
                )
            }
        }

        navigation(
            route = Route.AppMain.route,
            startDestination = Route.HomeScreen.route
        ) {
            composable(route = Route.HomeScreen.route) {
                val vm: HomeViewModel = hiltViewModel()
                HomeScreen(
                )
            }
        }
    }
}

private fun navigate(navController: NavController, route: Route) {
    navController.navigate(route = route.route) {
        navController.graph.startDestinationRoute?.let { routeStr ->
            restoreState = true
        }
    }
}

private fun navigateBack(navController: NavController) {
    navController.navigateUp()
}