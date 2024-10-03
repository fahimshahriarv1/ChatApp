package com.example.chatappstarting.presentation.ui.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.chatappstarting.R
import com.example.chatappstarting.presentation.ui.common.DefaultButton
import com.example.chatappstarting.presentation.ui.common.DefaultOutlinedTextField

@Composable
fun AddUserDialog(
    text: MutableState<String> = mutableStateOf(""),
    isLoading: Boolean,
    onDismiss: () -> Unit = {},
    onOkClicked: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp)
                .background(
                    color = colorResource(id = R.color.white),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.gray_light),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
            ) {

                Text(
                    text = "x",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(
                            Alignment.CenterEnd
                        )
                        .clickable {
                            onDismiss()
                        }
                )
            }

            DefaultOutlinedTextField(
                value = text.value,
                labelString = "Username/Mobile number",
                fonSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                onValueChange = {
                    text.value = it
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            DefaultButton(
                buttonTxt = stringResource(id = R.string.add_user),
                oncClick = onOkClicked,
                enabled = text.value.isNotEmpty() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}