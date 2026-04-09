package com.fahimshahriarv1.mtom.presentation.ui.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fahimshahriarv1.mtom.R

@Composable
fun UserComponent(
    name: String,
    isOnline: Boolean,
    onTxt: () -> Unit = {},
    onCall: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(
                color = if (isOnline)
                    colorResource(id = R.color.app_main).copy(alpha = 0.1f)
                else
                    colorResource(id = R.color.gray_light).copy(alpha = 0.15f)
            )
            .padding(12.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (isOnline) colorResource(id = R.color.app_main)
                    else colorResource(id = R.color.gray_light)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name + status
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = if (isOnline) "Online" else "Offline",
                fontSize = 12.sp,
                color = if (isOnline) colorResource(id = R.color.app_main)
                else colorResource(id = R.color.gray_light)
            )
        }

        // Action icons
        IconButton(
            onClick = onTxt,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Message",
                tint = if (isOnline) colorResource(id = R.color.app_main)
                else colorResource(id = R.color.gray_light),
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = onCall,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                tint = if (isOnline) colorResource(id = R.color.app_main)
                else colorResource(id = R.color.gray_light),
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = { /* Info / profile view */ },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = if (isOnline) colorResource(id = R.color.app_main)
                else colorResource(id = R.color.gray_light),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
