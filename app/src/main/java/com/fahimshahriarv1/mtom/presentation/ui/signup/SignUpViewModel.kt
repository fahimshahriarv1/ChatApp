package com.fahimshahriarv1.mtom.presentation.ui.signup

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.mtom.data.firebase.FireBaseClient
import com.fahimshahriarv1.mtom.data.firebase.FirebaseMessageManager
import com.fahimshahriarv1.mtom.domain.usecases.SaveNameUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveTokenUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveUserNameUseCase
import com.fahimshahriarv1.mtom.presentation.navgraph.Route
import com.fahimshahriarv1.mtom.presentation.ui.base.BaseViewModel
import com.fahimshahriarv1.mtom.presentation.utils.isPhoneNumberValid
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val client: FireBaseClient,
    private val setNameUseCase: SaveNameUseCase,
    private val setUnameUseCase: SaveUserNameUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val firebaseMessageManager: FirebaseMessageManager
) : BaseViewModel() {
    private val _countryCode = mutableStateOf("+88")
    val countryCode: State<String> = _countryCode

    private val _mobileNumber = mutableStateOf("")
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

    private val _resendTimer = mutableIntStateOf(0)
    val resendTimer: State<Int> = _resendTimer

    private val _otpSent = mutableStateOf(false)
    val otpSent: State<Boolean> = _otpSent

    var verificationID = ""
    private var resendToken: ForceResendingToken? = null
    private var hasSentOtp = false
    private var timerJob: Job? = null

    fun onCountryCodeSelected(code: String) {
        _countryCode.value = code
    }

    fun onMobileNumberChanged(number: String) {
        _mobileNumber.value = number
        _isMobileNumberError.value = !number.isPhoneNumberValid()
    }

    fun onOtpChanged(otp: String) {
        if (otp.length <= 6) {
            _otp.value = otp
        }
    }

    fun onPasswordChanged(pass: String) {
        _password.value = pass
    }

    fun onReEnterPasswordChanged(pass: String) {
        _reEnterPassword.value = pass
        _isPasswordMatched.value = _reEnterPassword.value == _password.value
    }

    fun sendOtp(activity: Activity) {
        if (hasSentOtp) return
        hasSentOtp = true
        doSendOtp(activity, isResend = false)
    }

    fun resendOtp(activity: Activity) {
        if (_resendTimer.intValue > 0) return
        _otp.value = ""
        doSendOtp(activity, isResend = true)
    }

    private fun doSendOtp(activity: Activity, isResend: Boolean) {
        loaderState.value = true
        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(countryCode.value + mobileNumber.value)
            .setTimeout(120, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(phoneAuthCallbacks)

        if (isResend && resendToken != null) {
            optionsBuilder.setForceResendingToken(resendToken!!)
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private val phoneAuthCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                loaderState.value = false
                showToast("Verification Success")
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                loaderState.value = false
                hasSentOtp = false
                showToast("Unable to verify: ${exception.message}")
                exception.printStackTrace()
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                super.onCodeAutoRetrievalTimeOut(verificationId)
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                loaderState.value = false
                Log.d("OTP sent", "$verificationId $token")
                verificationID = verificationId
                resendToken = token
                _otpSent.value = true
                showToast("OTP sent")
                startResendTimer()
            }
        }

    private fun startResendTimer() {
        timerJob?.cancel()
        _resendTimer.intValue = 60
        timerJob = viewModelScope.launch {
            while (_resendTimer.intValue > 0) {
                delay(1000)
                _resendTimer.intValue--
            }
        }
    }

    fun verifyOtp() {
        if (_otp.value.length != 6) {
            showToast("Please enter a valid 6-digit OTP")
            return
        }
        if (verificationID.isEmpty()) {
            showToast("Please wait for OTP to be sent")
            return
        }
        val credential = PhoneAuthProvider.getCredential(verificationID, otp.value)
        loaderState.value = true
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener {
            loaderState.value = false
            onOtpVerified()
        }.addOnFailureListener {
            loaderState.value = false
            showToast("Wrong OTP, please try again")
            it.printStackTrace()
        }
    }

    fun onPasswordOkClicked(onSuccess: () -> Unit = {}) {
        _isPasswordMatched.value =
            _reEnterPassword.value == _password.value && _password.value.isNotEmpty()

        if (_isPasswordMatched.value)
            loaderState.value = true
        client.createUser(
            mobileNumber.value,
            password.value,

            onSuccess = { token ->
                firebaseMessageManager.registerUser(mobileNumber.value)
                viewModelScope.launch {
                    setNameUseCase.saveName(mobileNumber.value)
                    setUnameUseCase.saveUserName(mobileNumber.value)
                    saveTokenUseCase.saveToken(token)

                    loaderState.value = false
                    onSuccess()
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

    fun checkUserExistOrNot(onSuccess: () -> Unit) {
        loaderState.value = true
        client.checkUserExistence(
            mobileNumber.value,
            existOrNot = {
                if (!it)
                    onSuccess()
                else
                    showToast("User Already Exist, Please Login")

                loaderState.value = false
            },
            onFailed = { e ->
                e.printStackTrace()
                showToast("Error: ${e.message}")
                loaderState.value = false
            }
        )
    }

    fun getFullPhoneNumber(): String {
        return countryCode.value + mobileNumber.value
    }

    fun resetOtpState() {
        hasSentOtp = false
        _otpSent.value = false
        _otp.value = ""
        verificationID = ""
    }
}
