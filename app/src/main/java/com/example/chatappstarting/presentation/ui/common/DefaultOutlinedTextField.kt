package com.example.chatappstarting.presentation.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatappstarting.R

@Composable
fun DefaultOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    labelString: String = "",
    label: @Composable (() -> Unit) = {
        Text(
            text = labelString,
            color = colorResource(id = R.color.gray_light),
            textAlign = TextAlign.Start
        )
    },
    shape: Shape = RoundedCornerShape(8.dp),
    isError: Boolean = false,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colorResource(id = R.color.app_main),
        errorBorderColor = colorResource(id = R.color.error),
        unfocusedBorderColor = colorResource(id = R.color.gray_light)
    ),
    singleLine: Boolean = true,
    fonSize: TextUnit = 24.sp,
    textAlign: TextAlign = TextAlign.Start,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit) = {},
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = fonSize,
            textAlign = textAlign
        ),
        keyboardOptions = keyboardOptions,
        colors = colors,
        shape = shape,
        label = label,
        isError = isError,
        singleLine = singleLine,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        modifier = modifier
    )
}