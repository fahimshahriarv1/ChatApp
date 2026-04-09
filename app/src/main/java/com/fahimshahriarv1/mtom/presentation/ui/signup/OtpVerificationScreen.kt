package com.fahimshahriarv1.mtom.presentation.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fahimshahriarv1.mtom.R
import com.fahimshahriarv1.mtom.constants.DEFAULT_BOTTOM_PADDING
import com.fahimshahriarv1.mtom.constants.DEFAULT_WIDTH_PERCENT
import com.fahimshahriarv1.mtom.presentation.ui.common.DefaultButton
import com.fahimshahriarv1.mtom.presentation.ui.common.DefaultNavIconAppAuth

@Preview
@Composable
fun OtpVerificationScreenPreview() {
    OtpVerificationScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    otp: State<String> = mutableStateOf(""),
    isLoading: Boolean = false,
    phoneNumber: String = "",
    resendTimer: State<Int> = mutableIntStateOf(0),
    onOtpValueChanged: (String) -> Unit = {},
    onVerifyClicked: () -> Unit = {},
    onResendClicked: () -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    DefaultNavIconAppAuth { navigateBack() }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = padding.calculateTopPadding())
                .background(color = colorResource(id = R.color.white))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.enter_your_otp),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    color = colorResource(id = R.color.app_main),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (phoneNumber.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.otp_sent_to, phoneNumber),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.gray_light)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = otp.value,
                    onValueChange = onOtpValueChanged,
                    textStyle = TextStyle(
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = 8.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.app_main),
                        errorBorderColor = colorResource(id = R.color.error),
                        unfocusedBorderColor = colorResource(id = R.color.gray_light)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = {
                        Text(
                            text = "------",
                            color = colorResource(id = R.color.gray_light),
                            textAlign = TextAlign.Center,
                            fontSize = 28.sp,
                            letterSpacing = 8.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (resendTimer.value > 0) {
                    Text(
                        text = stringResource(id = R.string.resend_in, resendTimer.value),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.gray_light)
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.resend_otp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.app_main),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = !isLoading
                        ) {
                            onResendClicked()
                        }
                    )
                }
            }

            DefaultButton(
                oncClick = onVerifyClicked,
                buttonTxt = stringResource(id = R.string.verify),
                enabled = otp.value.length == 6 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = DEFAULT_BOTTOM_PADDING)
            )
        }
    }
}
