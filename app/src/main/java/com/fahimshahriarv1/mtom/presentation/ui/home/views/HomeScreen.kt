package com.fahimshahriarv1.mtom.presentation.ui.home.views

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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fahimshahriarv1.mtom.R
import com.fahimshahriarv1.mtom.data.room.model.ChatUserEntity
import com.fahimshahriarv1.mtom.data.room.model.UserInformation
import com.fahimshahriarv1.mtom.domain.model.StatusEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    name: String = "",
    currentUserName: String = "",
    list: List<UserInformation>,
    chatList: List<ChatUserEntity> = emptyList(),
    isLoading: Boolean = false,
    addUserText: MutableState<String> = mutableStateOf(""),
    onSettingsClicked: () -> Unit = {},
    onStartChatClicked: () -> Unit = {},
    onUserChatClicked: (userName: String) -> Unit = {},
    onChatItemClicked: (chatId: String, recipientName: String) -> Unit = { _, _ -> }
) {
    val showDialog = remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chats", "Contacts")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = name, color = Color.White, fontSize = 14.sp) },
                navigationIcon = {},
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                actions = {
                    IconButton(onClick = { onSettingsClicked() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.app_main)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = colorResource(id = R.color.app_main),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.White
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = colorResource(id = R.color.app_main),
                    trackColor = colorResource(id = R.color.app_main).copy(alpha = .4f)
                )
            }

            when (selectedTab) {
                0 -> ChatsTab(
                    chatList = chatList,
                    currentUserName = currentUserName,
                    onChatItemClicked = onChatItemClicked
                )
                1 -> ContactsTab(
                    list = list,
                    isLoading = isLoading,
                    onUserChatClicked = onUserChatClicked
                )
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            FloatingActionButton(
                onClick = { showDialog.value = true },
                modifier = Modifier.align(Alignment.BottomEnd),
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
            onOkClicked = {
                onStartChatClicked()
                showDialog.value = false
            },
            text = addUserText
        )
}

@Composable
private fun ChatsTab(
    chatList: List<ChatUserEntity>,
    currentUserName: String,
    onChatItemClicked: (chatId: String, recipientName: String) -> Unit
) {
    if (chatList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No conversations yet",
                color = colorResource(id = R.color.gray_light),
                fontSize = 14.sp
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            items(chatList) { chat ->
                val recipientName = chat.chatId
                    .split("_")
                    .firstOrNull { it != currentUserName }
                    ?: chat.chatId
                ChatListItem(
                    contactName = recipientName,
                    lastMessage = chat.message,
                    timestamp = chat.timeStamp,
                    onClick = { onChatItemClicked(chat.chatId, recipientName) }
                )
            }
        }
    }
}

@Composable
private fun ContactsTab(
    list: List<UserInformation>,
    isLoading: Boolean,
    onUserChatClicked: (userName: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        if (list.isEmpty() && !isLoading) {
            item { Text(text = "No contacts added", modifier = Modifier.padding(16.dp)) }
        }

        items(list) { userInfo ->
            UserComponent(
                name = userInfo.name,
                isOnline = userInfo.status == StatusEnum.ONLINE,
                onTxt = { onUserChatClicked(userInfo.userName) }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        item { Spacer(modifier = Modifier.height(4.dp)) }
    }
}
