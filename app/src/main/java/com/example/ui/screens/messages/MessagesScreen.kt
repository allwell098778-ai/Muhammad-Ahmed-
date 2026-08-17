package com.example.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.ui.components.CallModalDialog
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MessagesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    var showPinUnlockDialogForConv by remember { mutableStateOf<ConversationEntity?>(null) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "World Eagle Secure Desk",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "One-to-one, group dispatches & encrypted chat lock",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = EagleGold
                    )
                }
            }
        }

        items(conversations) { conv ->
            ConversationItemCard(
                conv = conv,
                onClick = {
                    if (conv.isLocked) {
                        showPinUnlockDialogForConv = conv
                        enteredPin = ""
                        pinError = false
                    } else {
                        viewModel.openConversation(conv)
                    }
                }
            )
        }

        item {
            WorldEagleFooter()
        }
    }

    // Chat Lock PIN Verification Dialog
    if (showPinUnlockDialogForConv != null) {
        val targetConv = showPinUnlockDialogForConv!!
        AlertDialog(
            onDismissRequest = { showPinUnlockDialogForConv = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = EagleGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enter Chat Lock PIN")
                }
            },
            text = {
                Column {
                    Text(
                        text = "This conversation is secured with end-to-end PIN protection.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            enteredPin = it
                            pinError = false
                        },
                        label = { Text("4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = pinError,
                        supportingText = {
                            if (pinError) Text("Incorrect PIN", color = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (targetConv.pinHash.isBlank() || targetConv.pinHash == enteredPin) {
                            showPinUnlockDialogForConv = null
                            viewModel.openConversation(targetConv)
                        } else {
                            pinError = true
                        }
                    }
                ) {
                    Text("Unlock Chat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinUnlockDialogForConv = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ConversationItemCard(
    conv: ConversationEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(EagleNavy800)
                    .border(1.5.dp, EagleGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = conv.avatarEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conv.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = conv.lastMessageTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (conv.isLocked) "🔒 Locked Message" else conv.lastMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (conv.isLocked) EagleGold else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )

                    if (conv.isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = EagleGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeConv by viewModel.activeConversation.collectAsState()
    val activeCallType by viewModel.activeCallType.collectAsState()

    var messageText by remember { mutableStateOf("") }
    var showChatLockModal by remember { mutableStateOf(false) }
    var chatPinInput by remember { mutableStateOf("") }
    var showAttachmentMenu by remember { mutableStateOf(false) }

    val conv = activeConv ?: return

    val messages by viewModel.repository.getMessages(conv.id).collectAsState(initial = emptyList())
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = conv.avatarEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = conv.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Online • End-to-End Encrypted",
                                style = MaterialTheme.typography.labelSmall.copy(color = SignalBullish)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Voice Call Trigger
                    IconButton(onClick = { viewModel.startCall("VOICE") }) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = SignalBullish)
                    }
                    // Video Call Trigger
                    IconButton(onClick = { viewModel.startCall("VIDEO") }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = EagleGold)
                    }
                    // Chat Lock Toggle
                    IconButton(onClick = { showChatLockModal = true }) {
                        Icon(
                            imageVector = if (conv.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock Chat",
                            tint = if (conv.isLocked) EagleGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Message feed
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = EagleNavy800,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🔒 Messages in this desk are cryptographically stored",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                items(messages) { msg ->
                    MessageBubble(msg)
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attach File", tint = EagleGold)
                    }

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Write encrypted message...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EagleGold)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = EagleNavy900)
                    }
                }
            }

            if (showAttachmentMenu) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AttachmentOption(icon = Icons.Default.Image, label = "Photo") {
                        viewModel.sendMessage("📷 Attached Verified Intelligence Image", msgType = "IMAGE")
                        showAttachmentMenu = false
                    }
                    AttachmentOption(icon = Icons.Default.Mic, label = "Voice Note") {
                        viewModel.sendMessage("🎙️ Voice Memo (00:32) - Analysis Dispatch", msgType = "VOICE")
                        showAttachmentMenu = false
                    }
                    AttachmentOption(icon = Icons.Default.InsertDriveFile, label = "Document") {
                        viewModel.sendMessage("📄 SEC_13F_Report.pdf (3.4 MB)", msgType = "FILE")
                        showAttachmentMenu = false
                    }
                }
            }
        }
    }

    // Call Modal Dialog
    if (activeCallType != null) {
        CallModalDialog(
            callType = activeCallType!!,
            contactName = conv.title,
            onEndCall = { viewModel.endCall() }
        )
    }

    // Chat Lock Modal
    if (showChatLockModal) {
        AlertDialog(
            onDismissRequest = { showChatLockModal = false },
            title = { Text(if (conv.isLocked) "Unlock or Change PIN" else "Set Chat Lock PIN") },
            text = {
                Column {
                    Text(
                        text = "Set a 4-digit PIN to lock this private desk from unauthorized eyes.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = chatPinInput,
                        onValueChange = { chatPinInput = it },
                        label = { Text("4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.lockConversation(conv.id, chatPinInput.isNotBlank(), chatPinInput)
                        showChatLockModal = false
                    }
                ) {
                    Text(if (chatPinInput.isNotBlank()) "Lock Chat" else "Remove Lock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChatLockModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = EagleGold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun MessageBubble(msg: MessageEntity) {
    val isMe = msg.isMe
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isMe) EagleGold else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isMe) {
                    Text(
                        text = msg.senderFullName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMe) EagleNavy900 else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isMe) EagleNavy900.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered",
                            tint = EagleNavy900,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
