package com.example.chatappstarting.presentation.ui.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.chatappstarting.R

@Composable
fun UserComponent(
    name: String,
    isOnline: Boolean,
    onTxt: () -> Unit = {},
    onCall: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isOnline)
                    colorResource(id = R.color.app_main)
                else
                    colorResource(id = R.color.gray_light)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = name, modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterStart)

            )
        }

        if (isOnline)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "call", modifier = Modifier
                            .padding(20.dp)
                            .clickable {
                                onCall()
                            }

                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "txt", modifier = Modifier
                            .padding(20.dp)
                            .clickable {
                                onTxt()
                            }
                    )
                }
            }
    }
}