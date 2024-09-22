package com.example.chatappstarting.presentation.ui.home.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatappstarting.R
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.presentation.ui.home.model.StatusEnum
import com.example.chatappstarting.presentation.widgets.TopAppBarActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    name: String = "",
    list: List<UserInformation>,
    isLoading: Boolean = false,
    addUserText :MutableState<String> = mutableStateOf(""),
    onLogoutClicked: () -> Unit = {},
    onAdduserClicked: () -> Unit = {}
) {
    val showDialog = remember {
        mutableStateOf(false)
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = name, color = Color.DarkGray, fontSize = 14.sp) },
                navigationIcon = {},
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                actions = {
                    TopAppBarActionButton(
                        imageIcon = painterResource(id = R.drawable.drop_down_arrow),
                        description = ""
                    ) {
                        onLogoutClicked()
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.app_main)
                )
            )
        }) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (list.isEmpty() && !isLoading) {
                    item {
                        Text(text = "No one is online")
                    }
                }

                if (isLoading) {
                    item {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp),
                            color = colorResource(id = R.color.app_main),
                            trackColor = colorResource(id = R.color.app_main).copy(alpha = .4f)
                        )
                    }
                }

                items(list) { userInfo ->
                    UserComponent(
                        name = userInfo.name,
                        isOnline = userInfo.status == StatusEnum.ONLINE
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            FloatingActionButton(
                onClick = { showDialog.value = true }, modifier = Modifier
                    .align(Alignment.BottomEnd),
                containerColor = colorResource(id = R.color.app_main)
            ) {
                Text(text = "+", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }

    if (showDialog.value)
        AddUserDialog(
            isLoading = isLoading,
            onDismiss = { showDialog.value = false },
            onOkClicked = onAdduserClicked,
            text = addUserText
        )
}