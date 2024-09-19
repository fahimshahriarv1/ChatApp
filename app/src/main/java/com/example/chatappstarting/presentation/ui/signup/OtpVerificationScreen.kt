package com.example.chatappstarting.presentation.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatappstarting.R
import com.example.chatappstarting.constants.DEFAULT_BOTTOM_PADDING
import com.example.chatappstarting.constants.DEFAULT_WIDTH_PERCENT
import com.example.chatappstarting.presentation.ui.common.DefaultButton
import com.example.chatappstarting.presentation.ui.common.DefaultNavIconAppAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    otp: State<String> = mutableStateOf(""),
    onOtpValueChanged: (String) -> Unit = {},
    onOkClicked: () -> Unit = {},
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
                .padding(top = padding.calculateTopPadding() + DEFAULT_BOTTOM_PADDING)
                .background(color = colorResource(id = R.color.white))
        ) {
            OutlinedTextField(
                value = otp.value,
                onValueChange = onOtpValueChanged,
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
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
                label = {
                    Text(
                        text = stringResource(id = R.string.enter_your_otp),
                        color = colorResource(id = R.color.gray_light),
                        textAlign = TextAlign.Center
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                    .align(Alignment.TopCenter)
            )

            DefaultButton(
                oncClick = onOkClicked,
                buttonTxt = stringResource(id = R.string.ok),
                modifier = Modifier
                    .fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = DEFAULT_BOTTOM_PADDING)
            )
        }
    }
}