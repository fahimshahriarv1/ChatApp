package com.fahimshahriarv1.mtom.presentation.widgets

import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun TopAppBarActionButton(
    imageIcon: Painter,
    description: String,
    onClick: () -> Unit
) {
    IconButton(onClick = {
        onClick()
    }) {
        Image(painter = imageIcon, contentDescription = description)
    }
}