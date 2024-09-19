package com.example.chatappstarting.presentation.navgraph

import android.app.Activity
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
import com.example.chatappstarting.presentation.ui.base.FlowObserver
import com.example.chatappstarting.presentation.ui.home.HomeViewModel
import com.example.chatappstarting.presentation.ui.home.views.HomeScreen
import com.example.chatappstarting.presentation.ui.login.LoginViewModel
import com.example.chatappstarting.presentation.ui.login.ui.LoginScreen
import com.example.chatappstarting.presentation.ui.signup.MobileNumberScreen
import com.example.chatappstarting.presentation.ui.signup.OtpVerificationScreen
import com.example.chatappstarting.presentation.ui.signup.PasswordScreen
import com.example.chatappstarting.presentation.ui.signup.SignUpViewModel
import com.example.chatappstarting.presentation.ui.utils.showToastMessage
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

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
                            onLoginClicked = vm::onLoginClicked,
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
                val auth = PhoneAuthOptions.newBuilder(vm.firebaseAuth)
                    .setPhoneNumber(vm.countryCode.value + vm.mobileNumber.value)
                    .setTimeout(60, TimeUnit.SECONDS)
                    .setActivity(LocalContext.current as Activity)
                    .setCallbacks(
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                                //onSendOtpClicked()
                                vm.showToast("Verification Success")
                            }

                            override fun onVerificationFailed(p0: FirebaseException) {
                                vm.showToast("Unable to verify")
                                p0.printStackTrace()
                            }

                            override fun onCodeSent(
                                p0: String,
                                p1: PhoneAuthProvider.ForceResendingToken
                            ) {
                                super.onCodeSent(p0, p1)
                                vm.verificationCode = p0
                                vm.resendToken = p1
                                vm.isResend = true
                                vm.showToast("Otp sent")
                            }
                        }
                    )

                BaseComposable(
                    composable = {
                        MobileNumberScreen(
                            countryCode = vm.countryCode,
                            mobileNumber = vm.mobileNumber,
                            isError = vm.isMobileNumberError,
                            onCountryCodeClicked = vm::onCountryCodeSelected,
                            mobileNumberChanged = vm::onMobileNumberChanged,
                            onSendOtpClicked = {
                                vm.onSendOtpClicked(auth)
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

                val context = LocalContext.current
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

                BaseComposable(
                    composable = {
                        HomeScreen(list,vm::logout)
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
    }
}

private fun navigateUp(navController: NavController) {
    navController.navigateUp()
}