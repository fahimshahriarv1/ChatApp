package com.fahimshahriarv1.mtom.presentation.navgraph

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.fahimshahriarv1.mtom.constants.MOBILE_NUMBER
import com.fahimshahriarv1.mtom.data.room.model.Argument
import com.fahimshahriarv1.mtom.presentation.ui.base.BaseComposable
import com.fahimshahriarv1.mtom.presentation.ui.base.BaseComposableWithLifeCycle
import com.fahimshahriarv1.mtom.presentation.ui.base.ComposableLifeCycleImpl
import com.fahimshahriarv1.mtom.presentation.ui.base.FlowObserver
import com.fahimshahriarv1.mtom.presentation.ui.home.HomeActivity
import com.fahimshahriarv1.mtom.presentation.ui.home.HomeViewModel
import com.fahimshahriarv1.mtom.presentation.ui.chat.ChatScreen
import com.fahimshahriarv1.mtom.presentation.ui.chat.ChatViewModel
import com.fahimshahriarv1.mtom.presentation.ui.home.views.HomeScreen
import com.fahimshahriarv1.mtom.presentation.ui.login.LoginViewModel
import com.fahimshahriarv1.mtom.presentation.ui.login.ui.LoginScreen
import com.fahimshahriarv1.mtom.presentation.ui.settings.SettingsScreen
import com.fahimshahriarv1.mtom.presentation.ui.settings.SettingsViewModel
import com.fahimshahriarv1.mtom.presentation.ui.signup.MobileNumberScreen
import com.fahimshahriarv1.mtom.presentation.ui.signup.OtpVerificationScreen
import com.fahimshahriarv1.mtom.presentation.ui.signup.PasswordScreen
import com.fahimshahriarv1.mtom.presentation.ui.signup.SignUpViewModel
import com.fahimshahriarv1.mtom.presentation.ui.splashScreen.SplashActivity
import com.fahimshahriarv1.mtom.presentation.ui.utils.showToastMessage
import com.fahimshahriarv1.mtom.presentation.utils.getActivity

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
            ) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Route.AppSignUp.route)
                }
                val vm: SignUpViewModel = hiltViewModel(parentEntry)
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
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Route.AppSignUp.route)
                }
                val vm: SignUpViewModel = hiltViewModel(parentEntry)
                val activity = LocalContext.current as Activity

                LaunchedEffect(Unit) {
                    vm.sendOtp(activity)
                }

                BaseComposable(
                    composable = {
                        OtpVerificationScreen(
                            otp = vm.otp,
                            isLoading = vm.loaderState.value,
                            phoneNumber = vm.getFullPhoneNumber(),
                            resendTimer = vm.resendTimer,
                            onOtpValueChanged = vm::onOtpChanged,
                            navigateBack = {
                                vm.resetOtpState()
                                navController.navigateUp()
                            },
                            onVerifyClicked = vm::verifyOtp,
                            onResendClicked = { vm.resendOtp(activity) }
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
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Route.AppSignUp.route)
                }
                val vm: SignUpViewModel = hiltViewModel(parentEntry)
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
                val chatList by vm.chatList.collectAsStateWithLifecycle(initialValue = listOf())
                val currentUser by vm.userName.collectAsStateWithLifecycle(initialValue = "")

                BaseComposableWithLifeCycle(
                    composable = {
                        HomeScreen(
                            name = name,
                            currentUserName = currentUser,
                            list = list,
                            chatList = chatList,
                            isLoading = vm.loaderState.value,
                            addUserText = vm.userAddText,
                            onSettingsClicked = {
                                navController.navigate(Route.SettingsScreen.route)
                            },
                            onStartChatClicked = vm::onAdduserClicked,
                            onUserChatClicked = { recipientName ->
                                val chatId = generateChatId(currentUser, recipientName)
                                navController.navigate(Route.ChatScreen(chatId, recipientName))
                            },
                            onChatItemClicked = { chatId, recipientName ->
                                navController.navigate(Route.ChatScreen(chatId, recipientName))
                            }
                        )
                    },
                    navController = navController,
                    navChannel = vm.navChannel,
                    composeLifeCycleImpl = {
                        ComposableLifeCycleImpl(
                            onPause = {
                                vm.setOffline()
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

                FlowObserver(flow = vm.navigateToChat) { (chatId, recipientName) ->
                    navController.navigate(Route.ChatScreen(chatId, recipientName))
                }
            }

            composable(
                route = Route.ChatScreen.route + "/{chat_id}/{recipient_name}",
                arguments = listOf(
                    navArgument("chat_id") { type = NavType.StringType },
                    navArgument("recipient_name") { type = NavType.StringType }
                )
            ) {
                val vm: ChatViewModel = hiltViewModel()
                val context = LocalContext.current
                val messages by vm.messages.collectAsStateWithLifecycle()
                val currentUser by vm.currentUserId.collectAsStateWithLifecycle()
                val isRecipientOnline by vm.isRecipientOnline.collectAsStateWithLifecycle()

                vm.startStatusObserver()

                BaseComposable(
                    composable = {
                        ChatScreen(
                            recipientName = vm.recipientName,
                            isRecipientOnline = isRecipientOnline,
                            messages = messages,
                            messageInput = vm.messageInput,
                            currentUserId = currentUser,
                            onMessageInputChanged = vm::onMessageInputChanged,
                            onSendClicked = vm::sendMessage,
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

            composable(route = Route.SettingsScreen.route) {
                val vm: SettingsViewModel = hiltViewModel()
                val context = LocalContext.current

                val name by vm.name.collectAsStateWithLifecycle(initialValue = "")
                val currentUser by vm.userName.collectAsStateWithLifecycle(initialValue = "")
                val isProcessing by vm.isProcessing.collectAsStateWithLifecycle()
                val lastLocalBackup by vm.lastLocalBackup.collectAsStateWithLifecycle()
                val lastDriveBackup by vm.lastDriveBackup.collectAsStateWithLifecycle()

                vm.getNames()

                SettingsScreen(
                    name = name,
                    userName = currentUser,
                    isProcessing = isProcessing,
                    lastLocalBackup = lastLocalBackup,
                    lastDriveBackup = lastDriveBackup,
                    onBackupLocal = { uri -> vm.backupToLocal(uri) },
                    onRestoreLocal = { uri -> vm.restoreFromLocal(uri) },
                    onBackupDrive = { account -> vm.backupToDrive(account) },
                    onRestoreDrive = { account -> vm.restoreFromDrive(account) },
                    onLogout = { vm.onLogout { gotoSplash(context) } },
                    navigateBack = { navController.navigateUp() }
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

private fun generateChatId(user1: String, user2: String): String {
    return if (user1 < user2) "${user1}_$user2" else "${user2}_$user1"
}