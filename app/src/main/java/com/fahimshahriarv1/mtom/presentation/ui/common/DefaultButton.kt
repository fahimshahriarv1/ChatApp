package com.fahimshahriarv1.mtom.presentation.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.fahimshahriarv1.mtom.R

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    buttonTxt: String,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp),
    colors:ButtonColors = ButtonDefaults.buttonColors(
        containerColor = colorResource(id = R.color.app_main)
    ),
    oncClick: () -> Unit = {}
) {
    Button(
        onClick = oncClick,
        shape = shape,
        enabled = enabled,
        colors = colors,
        modifier = modifier
    ) {
        Text(
            text = buttonTxt,
            color = Color.White,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
    }
}