package com.fahimshahriarv1.mtom.presentation.ui.forgotpassword

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.mtom.data.firebase.FireBaseClient
import com.fahimshahriarv1.mtom.data.firebase.logApiError
import com.fahimshahriarv1.mtom.data.firebase.logApiRequest
import com.fahimshahriarv1.mtom.data.firebase.logApiResponse
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
class ForgotPasswordViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val client: FireBaseClient
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

    private var verificationID = ""
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

    /**
     * Unlike sign up, the account must already exist before an OTP is sent.
     */
    fun checkUserExist(onSuccess: () -> Unit) {
        loaderState.value = true
        client.checkUserExistence(
            mobileNumber.value,
            existOrNot = {
                if (it)
                    onSuccess()
                else
                    showToast("No account found with this number")

                loaderState.value = false
            },
            onFailed = { e ->
                e.printStackTrace()
                showToast("Error: ${e.message}")
                loaderState.value = false
            }
        )
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

        logApiRequest(
            "verifyPhoneNumber",
            "phone=${countryCode.value + mobileNumber.value}, isResend=$isResend"
        )
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private val phoneAuthCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                logApiResponse("verifyPhoneNumber", "auto-retrieved, provider=${credential.provider}")
                loaderState.value = false
                showToast("Verification Success")
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                logApiError("verifyPhoneNumber", exception)
                loaderState.value = false
                hasSentOtp = false
                showToast("Unable to verify: ${exception.message}")
                exception.printStackTrace()
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                logApiResponse("verifyPhoneNumber", "auto-retrieval timed out, enter OTP manually")
                super.onCodeAutoRetrievalTimeOut(verificationId)
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                logApiResponse("verifyPhoneNumber", "code sent, verificationId=$verificationId")
                loaderState.value = false
                Log.d("OTP sent", "$verificationId $token")
                verificationID = verificationId
                resendToken = token
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
        logApiRequest("signInWithCredential", "otpLength=${otp.value.length}")
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener {
            logApiResponse("signInWithCredential", "uid=${it.user?.uid}")
            loaderState.value = false
            onOtpVerified()
        }.addOnFailureListener {
            logApiError("signInWithCredential", it)
            loaderState.value = false
            showToast("Wrong OTP, please try again")
            it.printStackTrace()
        }
    }

    private fun onOtpVerified() {
        navigateTo(Route.ResetPasswordScreen(mobileNumber = _mobileNumber.value))
    }

    fun onResetPasswordClicked(onSuccess: () -> Unit = {}) {
        _isPasswordMatched.value =
            _reEnterPassword.value == _password.value && _password.value.isNotEmpty()

        if (!_isPasswordMatched.value) return

        loaderState.value = true
        client.updatePassword(
            uname = mobileNumber.value,
            newPassword = password.value,
            onSuccess = {
                loaderState.value = false
                resetState()
                // The success message is shown by the caller — this screen is
                // popped right away, so a flow based toast would be missed.
                onSuccess()
            },
            onFailure = {
                loaderState.value = false
                showToast("Something went wrong")
            }
        )
    }

    fun resetOtpState() {
        hasSentOtp = false
        _otp.value = ""
        verificationID = ""
        timerJob?.cancel()
        _resendTimer.intValue = 0
    }

    private fun resetState() {
        resetOtpState()
        _password.value = ""
        _reEnterPassword.value = ""
        _isPasswordMatched.value = true
    }

    fun getFullPhoneNumber(): String {
        return countryCode.value + mobileNumber.value
    }
}
