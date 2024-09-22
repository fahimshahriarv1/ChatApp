package com.example.chatappstarting.presentation.ui.signup

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.data.firebase.FireBaseClient
import com.example.chatappstarting.domain.usecases.SaveNameUseCase
import com.example.chatappstarting.domain.usecases.SaveTokenUseCase
import com.example.chatappstarting.domain.usecases.SaveUserNameUseCase
import com.example.chatappstarting.presentation.navgraph.Route
import com.example.chatappstarting.presentation.ui.base.BaseViewModel
import com.example.chatappstarting.presentation.utils.isPhoneNumberValid
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val client: FireBaseClient,
    private val setNameUseCase: SaveNameUseCase,
    private val setUnameUseCase: SaveUserNameUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) : BaseViewModel() {
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

    var verificationID = ""
    lateinit var resendToken: ForceResendingToken
    var isResend = false

    private val auth by lazy {
        PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(countryCode.value + mobileNumber.value)
            .setTimeout(60, TimeUnit.SECONDS)
            .setCallbacks(
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        //onSendOtpClicked()
                        showToast("Verification Success")
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        showToast("Unable to verify")
                        p0.printStackTrace()
                    }

                    override fun onCodeSent(
                        p0: String,
                        p1: ForceResendingToken
                    ) {
                        super.onCodeSent(p0, p1)
                        loaderState.value = false
                        Log.d("OTP sent", "$p0 $p1")
                        verificationID = p0
                        resendToken = p1
                        isResend = true
                        showToast("Otp sent")
                    }
                }
            )
    }

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

    fun onSendOtpClicked(context: Activity) {
        auth.setActivity(context)
        loaderState.value = true
        viewModelScope.launch {
            if (isResend)
                PhoneAuthProvider.verifyPhoneNumber(
                    auth.setForceResendingToken(resendToken).build()
                )
            else
                PhoneAuthProvider.verifyPhoneNumber(auth.build())
        }
    }

    fun verifyOtp() {
        val credential = PhoneAuthProvider.getCredential(verificationID, otp.value)
        loaderState.value = true
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener {
            loaderState.value = false
            onOtpVerified()
        }.addOnFailureListener {
            loaderState.value = false
            showToast("wrong otp")
        }
    }

    fun onPasswordOkClicked() {
        _isPasswordMatched.value =
            _reEnterPassword.value == _password.value && _password.value.isNotEmpty()

        if (_isPasswordMatched.value)
            loaderState.value = true
        client.createUser(
            mobileNumber.value,
            password.value,
            onSuccess = {
                loaderState.value = false

                viewModelScope.launch {
                    setNameUseCase.saveName(mobileNumber.value)
                    setUnameUseCase.saveUserName(mobileNumber.value)
                    saveTokenUseCase.saveToken(mobileNumber.value)
                    navigateTo(
                        Route.AppMain.fullRoute,
                        inclusive = true,
                        popUpToRoute = Route.AppAuth,
                        isSingleTop = true
                    )
                }
            },
            onFailure = {
                showToast("Something went wrong")
            }
        )
    }

    private fun onOtpVerified() {
        navigateTo(Route.SignUpPasswordScreen(mobileNumber = _mobileNumber.value))
    }
}