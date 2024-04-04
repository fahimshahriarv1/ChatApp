package com.example.chatappstarting.presentation.ui.signup

import android.animation.PropertyValuesHolder
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.presentation.navgraph.AppNavigator
import com.example.chatappstarting.presentation.navgraph.Route
import com.example.chatappstarting.presentation.ui.base.BaseViewModel
import com.example.chatappstarting.presentation.utils.isPhoneNumberValid
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    appNavigator: AppNavigator,
    val firebaseAuth: FirebaseAuth,
) : BaseViewModel(appNavigator) {
    private val _countryCode = mutableStateOf("+88")
    val countryCode: State<String> = _countryCode

    private val _mobileNumber = mutableStateOf("01712946542")
    val mobileNumber: State<String> = _mobileNumber

    private val _otp = mutableStateOf("")
    val otp: State<String> = _otp

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _reEnterPassword = mutableStateOf("")
    val reEnterPassword: State<String> = _reEnterPassword

    private val _isMobileNumberError = mutableStateOf(false)
    val isMobileNumberError: State<Boolean> = _isMobileNumberError

    private val _isPasswordMatched = mutableStateOf(true)
    val isPasswordMatched: State<Boolean> = _isPasswordMatched

    var verificationCode = ""
    lateinit var resendToken: ForceResendingToken
    var isResend = false

    init {
        showToast("yeppeeeee")
    }

    fun onCountryCodeSelected(code: String) {
        _countryCode.value = code
    }

    fun onMobileNumberChanged(number: String) {
        _mobileNumber.value = number

        _isMobileNumberError.value = !number.isPhoneNumberValid()
    }

    fun onOtpChanged(otp: String) {
        _otp.value = otp
    }

    fun onPasswordChanged(pass: String) {
        _password.value = pass
    }

    fun onReEnterPasswordChanged(pass: String) {
        _reEnterPassword.value = pass
        _isPasswordMatched.value = _reEnterPassword.value == _password.value
    }

    fun onSendOtpClicked(auth: PhoneAuthOptions.Builder) {
        navigateTo(Route.SignUpOtpScreen(mobileNumber = _mobileNumber.value))
        viewModelScope.launch {
            if (isResend)
                PhoneAuthProvider.verifyPhoneNumber(
                    auth.setForceResendingToken(resendToken).build()
                )
            else
                PhoneAuthProvider.verifyPhoneNumber(auth.build())
        }

    }

    fun onVerificationSuccess() {
        navigateTo(Route.SignUpOtpScreen(mobileNumber = _mobileNumber.value))
    }

    fun onPasswordOkClicked() {
        _isPasswordMatched.value =
            _reEnterPassword.value == _password.value && _password.value.isNotEmpty()
        if (_isPasswordMatched.value) {
            navigateTo(
                Route.AppMain.fullRoute,
                inclusive = true,
                popUpToRoute = Route.AppAuth,
                isSingleTop = true
            )
        }
    }

    fun onOtpOkClicked() {
        navigateTo(Route.SignUpPasswordScreen(mobileNumber = _mobileNumber.value))
    }
}