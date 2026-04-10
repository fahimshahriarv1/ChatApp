package com.fahimshahriarv1.mtom.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fahimshahriarv1.mtom.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(
    message: String,
    timestamp: String,
    isSent: Boolean
) {
    val formattedTime = formatTimestamp(timestamp)
    val maxBubbleWidth = (LocalConfiguration.current.screenWidthDp * 0.75).dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isSent) 60.dp else 6.dp,
                end = if (isSent) 6.dp else 60.dp,
                top = 1.dp,
                bottom = 1.dp
            ),
        horizontalArrangement = if (isSent) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 80.dp, max = maxBubbleWidth)
                .clip(
                    RoundedCornerShape(
                        topStart = if (isSent) 10.dp else 2.dp,
                        topEnd = if (isSent) 2.dp else 10.dp,
                        bottomStart = 10.dp,
                        bottomEnd = 10.dp
                    )
                )
                .background(
                    color = if (isSent)
                        colorResource(id = R.color.sent_bubble)
                    else
                        colorResource(id = R.color.received_bubble)
                )
                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = message,
                    color = if (isSent) Color.White else colorResource(id = R.color.black),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 8.dp, bottom = 2.dp)
                )

                Text(
                    text = formattedTime,
                    color = if (isSent) Color.White.copy(alpha = 0.7f) else colorResource(id = R.color.msg_time),
                    fontSize = 11.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DateSeparator(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            color = colorResource(id = R.color.msg_time),
            fontSize = 12.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(id = R.color.received_bubble).copy(alpha = 0.9f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

private fun formatTimestamp(timestamp: String): String {
    return try {
        val millis = timestamp.toLong()
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(millis))
    } catch (e: Exception) {
        ""
    }
}

fun formatDateHeader(timestamp: String): String {
    return try {
        val millis = timestamp.toLong()
        val msgDate = Date(millis)
        val today = Date()
        val sdfDay = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        when {
            sdfDay.format(msgDate) == sdfDay.format(today) -> "Today"
            sdfDay.format(msgDate) == sdfDay.format(Date(today.time - 86400000)) -> "Yesterday"
            else -> SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(msgDate)
        }
    } catch (e: Exception) {
        ""
    }
}
