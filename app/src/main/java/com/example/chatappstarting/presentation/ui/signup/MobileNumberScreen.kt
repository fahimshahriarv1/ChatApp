package com.example.chatappstarting.presentation.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatappstarting.R
import com.example.chatappstarting.constants.DEFAULT_BOTTOM_PADDING
import com.example.chatappstarting.constants.DEFAULT_WIDTH_PERCENT
import com.example.chatappstarting.presentation.ui.common.DefaultNavIconAppAuth
import com.example.chatappstarting.presentation.utils.countryCodes

@Preview
@Composable
fun MobileNumberScreenPreview() {
    MobileNumberScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileNumberScreen(
    mobileNumber: State<String> = mutableStateOf(""),
    countryCode: State<String> = mutableStateOf("+88"),
    isError: State<Boolean> = mutableStateOf(false),
    mobileNumberChanged: (String) -> Unit = {},
    onCountryCodeClicked: (String) -> Unit = {},
    onSendOtpClicked: () -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colorResource(id = R.color.white)
                ),
                navigationIcon = {
                    DefaultNavIconAppAuth { navigateBack() }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.TopCenter)
                ) {

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = stringResource(id = R.string.enter_mobile_number),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        color = colorResource(id = R.color.app_main),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(DEFAULT_WIDTH_PERCENT),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier
                                .weight(weight = .2f)
                                .wrapContentWidth()
                                .focusable(true)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .clickable(
                                        interactionSource = MutableInteractionSource(),
                                        indication = null
                                    ) {
                                        expanded = true
                                    }
                            ) {
                                Text(
                                    text = countryCode.value,
                                    textAlign = TextAlign.Center,
                                    color = colorResource(id = R.color.black),
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                )

                                Icon(
                                    painterResource(id = R.drawable.drop_down_arrow),
                                    contentDescription = "dslknds",
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                )
                            }

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = colorResource(id = R.color.white))
                                    .exposedDropdownSize(true)
                            ) {
                                val items = countryCodes.toList().sortedBy { it.second }
                                items.forEach { source ->
                                    Text(
                                        text = source.second,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .border(
                                                width = 1.dp,
                                                color = colorResource(id = R.color.gray_light),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                expanded = false
                                                onCountryCodeClicked(source.first)
                                            }
                                    )

                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                            }
                        }

                        OutlinedTextField(
                            value = mobileNumber.value,
                            onValueChange = {
                                mobileNumberChanged(it)
                            },
                            shape = RoundedCornerShape(8.dp),
                            label = {
                                Text(
                                    text = stringResource(id = R.string.mobile_number),
                                    color = colorResource(id = R.color.gray_light)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.app_main),
                                errorBorderColor = colorResource(id = R.color.error),
                                unfocusedBorderColor = colorResource(id = R.color.gray_light)
                            ),
                            textStyle = TextStyle(fontSize = 24.sp),
                            singleLine = true,
                            isError = isError.value,
                            modifier = Modifier
                                .weight(.8f)
                        )
                    }

                }

                Button(
                    onClick = onSendOtpClicked,
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isError.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.app_main)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(DEFAULT_WIDTH_PERCENT)
                        .padding(bottom = DEFAULT_BOTTOM_PADDING)
                        .align(alignment = Alignment.BottomCenter)
                ) {
                    Text(
                        text = stringResource(id = R.string.sent_otp),
                        color = colorResource(id = R.color.white),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    )
}