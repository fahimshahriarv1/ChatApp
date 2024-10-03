package com.example.chatappstarting.presentation.navgraph

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatappstarting.constants.MOBILE_NUMBER
import com.example.chatappstarting.data.room.model.Argument
import com.example.chatappstarting.presentation.ui.base.BaseComposable
import com.example.chatappstarting.presentation.ui.base.BaseComposableWithLifeCycle
import com.example.chatappstarting.presentation.ui.base.ComposableLifeCycleImpl
import com.example.chatappstarting.presentation.ui.base.FlowObserver
import com.example.chatappstarting.presentation.ui.home.HomeActivity
import com.example.chatappstarting.presentation.ui.home.HomeViewModel
import com.example.chatappstarting.presentation.ui.home.views.HomeScreen
import com.example.chatappstarting.presentation.ui.login.LoginViewModel
import com.example.chatappstarting.presentation.ui.login.ui.LoginScreen
import com.example.chatappstarting.presentation.ui.signup.MobileNumberScreen
import com.example.chatappstarting.presentation.ui.signup.OtpVerificationScreen
import com.example.chatappstarting.presentation.ui.signup.PasswordScreen
import com.example.chatappstarting.presentation.ui.signup.SignUpViewModel
import com.example.chatappstarting.presentation.ui.splashScreen.SplashActivity
import com.example.chatappstarting.presentation.ui.utils.showToastMessage
import com.example.chatappstarting.presentation.utils.getActivity

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
                val context = LocalContext.current
                BaseComposable(
                    composable = {
                        LoginScreen(
                            uname = vm.uname,
                            pass = vm.pass,
                            isError = vm.isError,
                            isLoading = vm.loaderState.value,
                            unameChanged = vm::onUnameChanged,
                            passChanged = vm::onPassChanged,
                            onLoginClicked = { vm.onLoginClicked { gotoHome(context) } },
                            onSignUpClicked = vm::onSignUpClicked
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )

                FlowObserver(flow = vm.showToast) {
                    if (it != null) {
                        showToastMessage(it, context)
                    }
                }
            }
        }

        navigation(
            route = Route.AppSignUp.route,
            startDestination = Route.SignUpMobileScreen.route
        ) {
            composable(
                route = Route.SignUpMobileScreen.route,
                arguments = listOf(
                    navArgument(
                        Route.SignUpMobileScreen.route,
                        builder = {
                            type = NavType.ParcelableArrayType(type = Argument::class.java)
                        }
                    )
                )
            ) {
                val vm: SignUpViewModel = hiltViewModel()
                BaseComposable(
                    composable = {
                        MobileNumberScreen(
                            countryCode = vm.countryCode,
                            mobileNumber = vm.mobileNumber,
                            isError = vm.isMobileNumberError,
                            onCountryCodeClicked = vm::onCountryCodeSelected,
                            mobileNumberChanged = vm::onMobileNumberChanged,
                            isLoading = vm.loaderState.value,
                            onSendOtpClicked = {
                                vm.checkUserExistOrNot {
                                    navController.navigate(Route.SignUpOtpScreen(mobileNumber = vm.mobileNumber.value))
                                }
                            },
                            navigateBack = {
                                navController.navigateUp()
                            }
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )

                val context = LocalContext.current
                FlowObserver(flow = vm.showToast) {
                    if (it != null) {
                        showToastMessage(it, context)
                    }
                }
            }

            composable(
                route = Route.SignUpOtpScreen.route + "/{$MOBILE_NUMBER}",
                arguments = listOf(navArgument(MOBILE_NUMBER) {
                    type = NavType.StringType
                    nullable = true
                })
            ) {
                val vm: SignUpViewModel = hiltViewModel()

                vm.isResend = false

                vm.onSendOtpClicked(LocalContext.current as Activity)

                BaseComposable(
                    composable = {
                        OtpVerificationScreen(
                            otp = vm.otp,
                            isLoading = vm.loaderState.value,
                            onOtpValueChanged = vm::onOtpChanged,
                            navigateBack = { navController.navigateUp() },
                            onOkClicked = vm::verifyOtp
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )

                val context = LocalContext.current
                FlowObserver(flow = vm.showToast) {
                    if (it != null) {
                        showToastMessage(it, context)
                    }
                }
            }

            composable(
                route = Route.SignUpPasswordScreen.route + "/{$MOBILE_NUMBER}",
                arguments = listOf(navArgument(MOBILE_NUMBER) {
                    type = NavType.StringType
                    nullable = true
                })
            ) {
                val vm: SignUpViewModel = hiltViewModel()
                val context = LocalContext.current

                BaseComposable(
                    composable = {
                        PasswordScreen(
                            passwordValue = vm.password,
                            reEnterPasswordValue = vm.reEnterPassword,
                            isPasswordMatched = vm.isPasswordMatched,
                            isLoading = vm.loaderState.value,
                            onPasswordValueChanged = vm::onPasswordChanged,
                            onReEnterPasswordValueChanged = vm::onReEnterPasswordChanged,
                            onOkClicked = { vm.onPasswordOkClicked { gotoHome(context) } },
                            navigateBack = { navController.navigateUp() }
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel
                )

                FlowObserver(flow = vm.showToast) {
                    if (it != null) {
                        showToastMessage(it, context)
                    }
                }
            }
        }

        navigation(
            route = Route.AppMain.route,
            startDestination = Route.HomeScreen.route
        ) {
            composable(route = Route.HomeScreen.route) {
                val vm: HomeViewModel = hiltViewModel()
                val context = LocalContext.current

                val list by vm.userList.collectAsStateWithLifecycle(initialValue = listOf())
                val name by vm.name.collectAsStateWithLifecycle(initialValue = "")

                BaseComposableWithLifeCycle(
                    composable = {
                        HomeScreen(
                            name = name,
                            list = list,
                            isLoading = vm.loaderState.value,
                            addUserText = vm.userAddText,
                            onLogoutClicked = {
                                vm.onLogout { gotoSplash(context) }
                            },
                            onAdduserClicked = vm::onAdduserClicked
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel,
                    composeLifeCycleImpl = {
                        ComposableLifeCycleImpl(
                            onPause = {
                                vm.removeListeners()
                            },
                            onResume = {
                                vm.startListeners()
                            }
                        )
                    }
                )

                vm.getNames()

                FlowObserver(flow = vm.showToast) {
                    if (it != null) {
                        showToastMessage(it, context)
                    }
                }
            }
        }
    }
}

private fun navigateUp(navController: NavController) {
    navController.navigateUp()
}

private fun gotoSplash(context: Context) {
    val intent = Intent(context, SplashActivity::class.java)
    context.startActivity(intent)
    context.getActivity()?.finish()
}

private fun gotoHome(context: Context) {
    val intent = Intent(context, HomeActivity::class.java)
    context.startActivity(intent)
    context.getActivity()?.finish()
}