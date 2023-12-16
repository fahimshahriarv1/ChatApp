package com.example.chatappstarting.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatappstarting.constants.MOBILE_NUMBER
import com.example.chatappstarting.presentation.ui.base.BaseComposable
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
                BaseComposable(
                    composable = {
                        LoginScreen(
                            uname = vm.uname,
                            pass = vm.pass,
                            isError = vm.isError,
                            unameChanged = vm::onUnameChanged,
                            passChanged = vm::onPassChanged,
                            onLoginClicked = vm::onLoginClicked,
                            onSignUoClicked = vm::onSignUoClicked
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )
            }
        }

        navigation(
            route = Route.AppSignUp.route,
            startDestination = Route.SignUpMobileScreen.route
        ) {
            composable(route = Route.SignUpMobileScreen.route) {
                val vm: SignUpViewModel = hiltViewModel()
                BaseComposable(
                    composable = {
                        MobileNumberScreen(
                            countryCode = vm.countryCode,
                            mobileNumber = vm.mobileNumber,
                            isError = vm.isMobileNumberError,
                            onCountryCodeClicked = vm::onCountryCodeSelected,
                            mobileNumberChanged = vm::onMobileNumberChanged,
                            onSendOtpClicked = vm::onSendOtpClicked,
                            navigateBack = {
                                navController.navigateUp()
                            }
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )
            }
            composable(
                route = Route.SignUpOtpScreen.route + "/{$MOBILE_NUMBER}",
                arguments = listOf(navArgument(MOBILE_NUMBER) {
                    type = NavType.StringType
                    nullable = true
                })
            ) {
                val vm: SignUpViewModel = hiltViewModel()
                BaseComposable(
                    composable = {
                        OtpVerificationScreen(
                            otp = vm.otp,
                            onOtpValueChanged = vm::onOtpChanged,
                            navigateBack = { navController.navigateUp() },
                            onOkClicked = vm::onOtpOkClicked
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )
            }

            composable(
                route = Route.SignUpPasswordScreen.route + "/{$MOBILE_NUMBER}",
                arguments = listOf(navArgument(MOBILE_NUMBER) {
                    type = NavType.StringType
                    nullable = true
                })
            ) {
                val vm: SignUpViewModel = hiltViewModel()
                BaseComposable(
                    composable = {
                        PasswordScreen(
                            passwordValue = vm.password,
                            reEnterPasswordValue = vm.reEnterPassword,
                            isPasswordMatched = vm.isPasswordMatched,
                            onPasswordValueChanged = vm::onPasswordChanged,
                            onReEnterPasswordValueChanged = vm::onReEnterPasswordChanged,
                            onOkClicked = vm::onPasswordOkClicked,
                            navigateBack = { navController.navigateUp() }
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )
            }
        }

        navigation(
            route = Route.AppMain.route,
            startDestination = Route.HomeScreen.route
        ) {
            composable(route = Route.HomeScreen.route) {
                val vm: HomeViewModel = hiltViewModel()
                BaseComposable(
                    composable = {
                        HomeScreen()
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )
            }
        }
    }
}

private fun navigateUp(navController: NavController) {
    navController.navigateUp()
}