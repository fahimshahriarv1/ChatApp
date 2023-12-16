package com.example.chatappstarting.presentation.ui.signup

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatappstarting.R
import com.example.chatappstarting.constants.DEFAULT_BOTTOM_PADDING
import com.example.chatappstarting.constants.DEFAULT_WIDTH_PERCENT
import com.example.chatappstarting.presentation.ui.common.DefaultButton
import com.example.chatappstarting.presentation.ui.common.DefaultNavIconAppAuth
import com.example.chatappstarting.presentation.ui.common.DefaultOutlinedTextField

@Preview
@Composable
fun PasswordScreenPreview() {
    PasswordScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    passwordValue: State<String> = mutableStateOf(""),
    reEnterPasswordValue: State<String> = mutableStateOf(""),
    isPasswordMatched: State<Boolean> = mutableStateOf(true),
    onPasswordValueChanged: (String) -> Unit = {},
    onReEnterPasswordValueChanged: (String) -> Unit = {},
    onOkClicked: () -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    val showWarningText = remember {
        mutableStateOf(false)
    }

    val rePassVisible = remember {
        mutableStateOf(false)
    }

    val icon = if (rePassVisible.value) painterResource(id = R.drawable.eye_invisible)
    else painterResource(id = R.drawable.eye_visible)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    DefaultNavIconAppAuth { navigateBack() }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = paddingValues.calculateTopPadding())
                .background(color = colorResource(id = R.color.white))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp + paddingValues.calculateTopPadding())
            ) {

                Text(
                    text = stringResource(id = R.string.enter_password_for_your_account),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    color = colorResource(id = R.color.app_main),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                )

                Spacer(modifier = Modifier.height(24.dp))
                DisableSelection {
                    DefaultOutlinedTextField(
                        value = passwordValue.value,
                        onValueChange = onPasswordValueChanged,
                        labelString = stringResource(id = R.string.enter_password),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        isError = !isPasswordMatched.value,
                        modifier = Modifier.fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    DefaultOutlinedTextField(
                        value = reEnterPasswordValue.value,
                        onValueChange = {
                            onReEnterPasswordValueChanged(it)
                            showWarningText.value = true
                        },
                        isError = !isPasswordMatched.value,
                        labelString = stringResource(id = R.string.re_enter_password),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        trailingIcon = {
                            if (reEnterPasswordValue.value.isNotEmpty()) {
                                IconButton(onClick = {
                                    rePassVisible.value = !rePassVisible.value
                                }) {
                                    Icon(
                                        painter = icon,
                                        contentDescription = "pass toggle icon",
                                        tint = colorResource(id = R.color.app_main),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable(
                                                interactionSource = MutableInteractionSource(),
                                                indication = null
                                            ) { rePassVisible.value = !rePassVisible.value }
                                    )
                                }
                            } else
                                rePassVisible.value = false
                        },
                        visualTransformation = if (rePassVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (showWarningText.value)
                    Text(
                        text = if (passwordValue.value.isEmpty() || reEnterPasswordValue.value.isEmpty())
                            stringResource(id = R.string.password_cannot_be_empty)
                        else if (isPasswordMatched.value)
                            stringResource(id = R.string.password_matched)
                        else
                            stringResource(id = R.string.password_mismatched),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = if (isPasswordMatched.value)
                            colorResource(id = R.color.app_main)
                        else
                            colorResource(id = R.color.error),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                    )
            }

            DefaultButton(
                buttonTxt = stringResource(id = R.string.ok),
                oncClick = {
                    onOkClicked()
                    showWarningText.value = true
                },
                enabled = isPasswordMatched.value,
                modifier = Modifier
                    .fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = DEFAULT_BOTTOM_PADDING)
            )
        }
    }
}