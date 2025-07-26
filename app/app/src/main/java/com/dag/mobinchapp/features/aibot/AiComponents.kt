package com.dag.mobinchapp.features.aibot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dag.mobinchapp.ui.theme.primaryText
import com.dag.mobinchapp.ui.theme.secondaryText
import com.dag.mobinchapp.R
import androidx.compose.material3.Divider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import com.dag.one_inch.MessageShortcut
import kotlinx.coroutines.launch


@Composable
internal fun MobinchAgentViewWithDrawer(
    state: AiBotVS.Success,
    onMessageSend: (String) -> Unit,
    onActionExecute: (AiBotVS.SuggestedAction) -> Unit,
    onHeaderClick: () -> Unit,
    onChatFromMemorySelected: (memoryId: String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Text(
                    text = "Message History",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    ),
                    modifier = Modifier.padding(16.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.drawerMessages.size) { index ->
                        val message = state.drawerMessages[index]
                        DrawerChatMessageItem(message = message){
                            scope.launch {
                                drawerState.close()
                            }
                            onChatFromMemorySelected(message.id)
                        }
                        Divider(color = Color(0xFF2D2D3F), thickness = 1.dp)
                    }
                }
            }
        },
        content = {
            MobinchAgentView(
                state = state,
                onMessageSend = onMessageSend,
                onActionExecute = onActionExecute,
                onHeaderClick = onHeaderClick,
                onHistoryClick = { 
                    scope.launch { 
                        if (drawerState.isClosed) drawerState.open() 
                        else drawerState.close() 
                    }
                }
            )
        }
    )
}

@Composable
internal fun MobinchAgentView(
    state: AiBotVS.Success,
    onMessageSend: (String) -> Unit,
    onActionExecute: (AiBotVS.SuggestedAction) -> Unit,
    onHeaderClick: () -> Unit,
    onHistoryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Animated Header
        AnimatedHeader(
            state = state,
            onHeaderClick = onHeaderClick,
            onActionExecute = onActionExecute,
            onHistoryClick = onHistoryClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chat and Actions Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column {
                // Chat Messages
                ChatMessages(
                    messages = state.chatMessages,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                // Suggested Actions
                if (state.suggestedActions.isNotEmpty() && !state.isHeaderExpanded) {
                    SuggestedActions(
                        actions = state.suggestedActions,
                        onActionClick = onActionExecute
                    )
                }

                // Message Input
                if (!state.isHeaderExpanded) {
                    MessageInput(onMessageSend = onMessageSend)
                }
            }
        }
    }
}


@Composable
private fun SuggestedActions(
    actions: List<AiBotVS.SuggestedAction>,
    onActionClick: (AiBotVS.SuggestedAction) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(actions.size) { index ->
            val action = actions[index]
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D3F)),
                modifier = Modifier.clickable { onActionClick(action) }
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .width(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = action.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryText,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = action.description,
                        fontSize = 12.sp,
                        color = secondaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedHeader(
    state: AiBotVS.Success,
    onHeaderClick: () -> Unit,
    onActionExecute: (AiBotVS.SuggestedAction) -> Unit,
    onHistoryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onHeaderClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1inch AI Agent",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_history_24), // You'll need to add this drawable
                            contentDescription = "Message History",
                            tint = primaryText
                        )
                    }

                    Image(
                        painter = painterResource(R.drawable._inch_logo),
                        contentDescription = "1inch Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            AnimatedVisibility(
                visible = state.isHeaderExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Quick Actions with Mobinch",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            title = "Become Resolver",
                            onClick = {
                                onActionExecute(
                                    AiBotVS.SuggestedAction(
                                        title = "Become Resolver",
                                        description = "Join Resolver Team",
                                        type = AiBotVS.ActionType.RESOLVER
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2D2D3F)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
    ) {
        Text(
            text = title,
            color = primaryText,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun MessageInput(
    onMessageSend: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                placeholder = {
                    Text(
                        text = "Ask anything about 1inch...",
                        color = secondaryText
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onMessageSend(message)
                        message = ""
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_send_24),
                    contentDescription = "Send",
                    tint = primaryText
                )
            }
        }
    }
}


@Composable
private fun ChatMessages(
    messages: List<AiBotVS.ChatMessage>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true
    ) {
        items(messages.size) { index->
            ChatMessageItem(message = messages.reversed()[index])
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun DrawerChatMessageItem(
    message: MessageShortcut,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D3F)),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.firstMessage,
                    fontSize = 14.sp,
                    color = primaryText
                )
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: AiBotVS.ChatMessage
) {
    val backgroundColor = when {
        message.isFromAI -> Color(0xFF1E1E2E)
        else -> Color(0xFF2D2D3F)
    }

    val alignment = when {
        message.isFromAI -> Alignment.CenterStart
        else -> Alignment.CenterEnd
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                when (message.messageType) {
                    AiBotVS.MessageType.ERROR -> {
                        Text(
                            text = message.content,
                            fontSize = 14.sp,
                            color = Color.Red
                        )
                    }
                    else -> {
                        Text(
                            text = message.content,
                            fontSize = 14.sp,
                            color = primaryText
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WalletConnectionDialog(
    onDismiss: () -> Unit,
    onConnect: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E2E)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Connect Wallet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Please connect your wallet to proceed with 1inch operations.",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = Color(0xFFAAAAAA)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onConnect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6E56CF)
                    )
                ) {
                    Text(
                        text = "Connect Wallet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFAAAAAA)
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}