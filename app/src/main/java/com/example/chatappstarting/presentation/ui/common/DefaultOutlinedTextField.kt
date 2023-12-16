package com.example.chatappstarting.presentation.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatappstarting.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    labelString: String = "",
    label: @Composable (() -> Unit) = {
        Text(
            text = labelString,
            color = colorResource(id = R.color.gray_light),
            textAlign = TextAlign.Center
        )
    },
    shape: Shape = RoundedCornerShape(8.dp),
    isError: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = colorResource(id = R.color.app_main),
        errorBorderColor = colorResource(id = R.color.error),
        unfocusedBorderColor = colorResource(id = R.color.gray_light)
    ),
    singleLine: Boolean = true,
    textStyle: TextStyle = TextStyle(
        fontSize = 24.sp,
        textAlign = TextAlign.Center
    ),
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Next
    ),
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        colors = colors,
        shape = shape,
        label = label,
        isError = isError,
        singleLine = singleLine,
        modifier = modifier
    )
}