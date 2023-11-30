package com.example.chatappstarting.ui.login.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LoginScreen(
    uname: MutableState<String> = mutableStateOf(""),
    pass: MutableState<String> = mutableStateOf(""),
    isError: MutableState<Boolean> = mutableStateOf(false),
    unameChanged: (String) -> Unit = {},
    passChanged: (String) -> Unit = {},
    onLoginClicked: () -> Unit = {}
) {
    val isPassVisible = remember {
        mutableStateOf(false)
    }

    val icon = if (isPassVisible.value) painterResource(id = R.drawable.eye_invisble)
    else painterResource(id = R.drawable.eye_visible)

    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            Text(
                text = stringResource(id = R.string.login),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = colorResource(id = R.color.app_main),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uname.value,
                onValueChange = {
                    unameChanged(it)
                },
                shape = RoundedCornerShape(8.dp),
                label = {
                    Text(
                        text = stringResource(id = R.string.username),
                        color = colorResource(id = R.color.gray_light)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.app_main),
                    errorBorderColor = colorResource(id = R.color.error)
                ),
                isError = isError.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = pass.value,
                onValueChange = {
                    passChanged(it)
                },
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(onClick = { isPassVisible.value = !isPassVisible.value }) {
                        Icon(
                            painter = icon,
                            contentDescription = "pass toggle icon",
                            tint = colorResource(id = R.color.app_main),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                ) { isPassVisible.value = !isPassVisible.value }
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.password),
                        color = colorResource(id = R.color.gray_light)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.app_main),
                    errorBorderColor = colorResource(id = R.color.error)
                ),
                isError = isError.value,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = if (isPassVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onLoginClicked,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.app_main)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    color = colorResource(id = R.color.white),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}