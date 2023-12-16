package com.example.chatappstarting.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatappstarting.R

@Composable
fun DefaultNavIconAppAuth(navigateBack: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                navigateBack()
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_downward_24px),
            contentDescription = "Nav Icon",
            modifier = Modifier.rotate(90f),
            colorFilter = ColorFilter.tint(color = colorResource(id = R.color.app_main))
        )

        Spacer(modifier = Modifier.width(2.dp))

        Text(
            text = stringResource(id = R.string.go_back),
            color = colorResource(id = R.color.app_main),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }

}