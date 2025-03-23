//import androidx.compose.foundation.layout.Box
//import androidx.compose.material.icons.Icons
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.chatbot.viewmodels.AppViewModel
//import com.example.chatbot.data.MessageModel

//package com.example.chatbot
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.chatbot.ui.theme.modelColor
//import com.example.chatbot.ui.theme.userColor
//
//@Composable
//fun ChatPage(modifier: Modifier = Modifier, viewModel: AppViewModel) {
//    Column(
//        modifier = modifier
//    ) {
////        AppHeader()
//        MessageList(modifier = Modifier.weight(1f), messageList = viewModel.messageList)
//        MessageInput(viewModel, onMessageSend = {
//            viewModel.sendMessage(it)
//        }
//        )
//    }
//}
//
//@Composable
//fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
//    val listState = rememberLazyListState() // State for LazyColumn
//
//    // Scroll to the last message when the list changes
//    LaunchedEffect(messageList.size) {
//        if (messageList.isNotEmpty()) {
//            listState.animateScrollToItem(messageList.lastIndex)
//        }
//    }
//    LazyColumn(
//        modifier = modifier,
//        state = listState
//    ) {
//        items(messageList) {
//            MessageRow(it)
//        }
//    }
//}
//
//
//@Composable
//fun MessageRow(messageModel: MessageModel) {
//    val isModel = (messageModel.role == "model")
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Box(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Box(
//                modifier = Modifier
//                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
//                    .padding(
//                        start = if (isModel) 8.dp else 50.dp,
//                        end = if (isModel) 50.dp else 8.dp,
//                        top = 8.dp,
//                        bottom = 8.dp
//                    )
//                    .clip(RoundedCornerShape(48f))
//                    .background(if (isModel) userColor else modelColor)
//                    .padding(16.dp)
//            ) {
//                Text(messageModel.message, fontWeight = FontWeight.W500, color = Color.White)
//            }
//        }
//    }
//}


package com.example.chatbot.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatbot.viewmodels.AppViewModel
import com.example.chatbot.data.MessageModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatPage(modifier: Modifier = Modifier, viewModel: AppViewModel) {


    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val showScrollToBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            val totalItems = layoutInfo.totalItemsCount

            // Show button if not at the bottom (last item not fully visible)
            if (visibleItems.isNotEmpty() && totalItems > 0) {
                val lastVisibleItem = visibleItems.last()
                lastVisibleItem.index < totalItems - 1 || // Last item isn't visible
                        lastVisibleItem.offset + lastVisibleItem.size > layoutInfo.viewportEndOffset // Last item is partially off-screen
            } else {
                false // No items or empty list
            }
        }
//        derivedStateOf {
//            listState.firstVisibleItemIndex > 0
//        }
    }
    val isTyping by remember { viewModel.isTyping }
    val inputValue by remember { viewModel.input }

    Box(modifier = modifier) {
        Column {
            MessageList(
                modifier = Modifier.weight(1f),
                messageList = viewModel.messageList,
                listState = listState,
                isTyping = isTyping
            )

            MessageInput(
                input = inputValue,
                onInputChange = { viewModel.updateInput(it) },
                isTyping = isTyping,
                onSendClick = {
                    if (inputValue.isNotBlank()) {
                        viewModel.sendMessage(inputValue)
                        viewModel.clearInput()
                    }
                }
            )
        }

        // Scroll to bottom button
        AnimatedVisibility(
            visible = showScrollToBottom,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            FloatingActionButton(
                onClick = {
                    if (viewModel.messageList.isNotEmpty()) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(
                                viewModel.messageList.lastIndex,
                                scrollOffset = Int.MAX_VALUE
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Scroll to bottom"
                )
            }
        }
    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<MessageModel>,
    listState: LazyListState,
    isTyping: Boolean
) {
    // Scroll to the last message when the list changes
    LaunchedEffect(messageList.size, isTyping) {
        if (messageList.isNotEmpty()) {
            listState.animateScrollToItem(messageList.lastIndex)
        }
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(messageList) { index, message ->
            val isFirstMessageFromSender = index == 0 ||
                    messageList[index - 1].role != message.role
            val isLastMessageFromSender = index == messageList.lastIndex ||
                    messageList[index + 1].role != message.role

            MessageRow(
                messageModel = message,
                isFirstInGroup = isFirstMessageFromSender,
                isLastInGroup = isLastMessageFromSender
            )
        }

        if (isTyping) {
            item {
                TypingIndicator()
            }
        }
    }
}

@Composable
fun MessageRow(
    messageModel: MessageModel,
    isFirstInGroup: Boolean = true,
    isLastInGroup: Boolean = true
) {
    val isFromModel = (messageModel.role == "model")
    val formattedTime = remember(messageModel.timestamp) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(messageModel.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isFirstInGroup) 8.dp else 2.dp),
        horizontalAlignment = if (isFromModel) Alignment.Start else Alignment.End
    ) {
        // Sender name if first in group
        if (isFirstInGroup) {
            Text(
                text = if (isFromModel) "Gemini" else "You",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(
                    start = if (isFromModel) 16.dp else 0.dp,
                    end = if (isFromModel) 0.dp else 16.dp,
                    bottom = 2.dp
                )
            )
        }

        // Message bubble
        Surface(
            shape = RoundedCornerShape(
                topStart = if (isFromModel && isFirstInGroup) 4.dp else 20.dp,
                topEnd = if (!isFromModel && isFirstInGroup) 4.dp else 20.dp,
                bottomStart = if (isFromModel && isLastInGroup) 20.dp else 4.dp,
                bottomEnd = if (!isFromModel && isLastInGroup) 20.dp else 4.dp
            ),
            color = if (isFromModel)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(
                start = if (isFromModel) 8.dp else 60.dp,
                end = if (isFromModel) 60.dp else 8.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = messageModel.message,
                    color = if (isFromModel)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Timestamp if last in group
        if (isLastInGroup) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(
                    start = if (isFromModel) 16.dp else 0.dp,
                    end = if (isFromModel) 0.dp else 16.dp,
                    top = 2.dp,
                    bottom = 4.dp
                )
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val dots = 3

    Row(
        modifier = Modifier
            .padding(start = 16.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Gemini is thinking",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        for (i in 0 until dots) {
            val delay = i * 300
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(6.dp)
                    .graphicsLayer(alpha = alpha)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            )
        }
    }
}

@Composable
fun MessageInput(
    input: String,
    onInputChange: (String) -> Unit,
    isTyping: Boolean,
    onSendClick: () -> Unit
) {
    val isMessageEmpty = input.isBlank()
    val maxCharacterCount = 500
    val characterCount = input.length
    val isNearLimit = characterCount > maxCharacterCount * 0.8f
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        if (it.length <= maxCharacterCount) {
                            onInputChange(it)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask Gemini anything...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        onSendClick()
                        keyboardController?.hide()
                    },
                    enabled = !isMessageEmpty && !isTyping,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (!isMessageEmpty && !isTyping)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (!isMessageEmpty && !isTyping)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            // Character counter
//            if (characterCount > 0) {
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = "$characterCount/$maxCharacterCount",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = if (isNearLimit)
//                        MaterialTheme.colorScheme.error
//                    else
//                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
//                    textAlign = TextAlign.End,
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
        }
    }
}


//@Composable
//fun MessageRow(messageModel: MessageModel) {
//    val isModel = (messageModel.role == "model")
//
//    // Check if the message starts and ends with '**' to detect a heading
//    val isHeading = messageModel.message.startsWith("**") && messageModel.message.endsWith("**")
//
//    // If it's a heading, remove the '**' markers
//    val displayMessage = if (isHeading) {
//        messageModel.message.removePrefix("**").removeSuffix("**")
//    } else {
//        messageModel.message
//    }
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Box(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Box(
//                modifier = Modifier
//                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
//                    .padding(
//                        start = if (isModel) 8.dp else 50.dp,
//                        end = if (isModel) 50.dp else 8.dp,
//                        top = 8.dp,
//                        bottom = 8.dp
//                    )
//                    .clip(RoundedCornerShape(48f))
//                    .background(if (isModel) userColor else modelColor)
//                    .padding(16.dp)
//            ) {
//                // Display heading with larger font and bold styling
//                if (isHeading) {
//                    Text(
//                        displayMessage,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 20.sp, // Larger font size for headings
//                        color = Color.White
//                    )
//                } else {
//                    // Display regular message
//                    Text(
//                        displayMessage,
//                        fontWeight = FontWeight.W500,
//                        color = Color.White
//                    )
//                }
//            }
//        }
//    }
//}


//@Preview
//@Composable
//private fun MessageRowPreview() {
//    MessageRow(
//        MessageModel(
//            "This is the model",
//            "model"
//        )
//    )
//}
//
//@Composable
//fun AppHeader(modifier: Modifier = Modifier) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.primary)
//    ) {
//        Text(
//            "Chat Bot",
//            color = Color.White,
//            fontSize = 24.sp,
//            modifier = Modifier.padding(16.dp)
//        )
//    }
//}
//
//@Composable
//fun MessageInput(
//    viewModel: AppViewModel,
//    onMessageSend: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        OutlinedTextField(
//            viewModel.input.value,
//            onValueChange = {
//                viewModel.updateInput(it)
//            },
//            label = { Text("Message") },
//            modifier = Modifier.weight(3f),
//        )
//        IconButton(
//            onClick = {
//                if (viewModel.input.value.isNotEmpty()) {
////                    send the message with the history of the chat
//                    onMessageSend(viewModel.input.value)
//                    viewModel.clearInput()
//                }
//            },
//        ) {
//            Icon(Icons.Default.Send, "Send")
//        }
//    }
//}
//
//
//@Preview(showSystemUi = true)
//@Composable
//private fun ChatPagePreview() {
//    ChatPage(viewModel = viewModel())
//}


//package com.example.chatbot
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.chatbot.ui.theme.modelColor
//import com.example.chatbot.ui.theme.userColor
//
//@Composable
//fun ChatPage(modifier: Modifier = Modifier, viewModel: AppViewModel) {
//    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
////        AppHeader()
//        MessageList(modifier = Modifier.weight(1f), messageList = viewModel.messageList)
//        MessageInput(viewModel, onMessageSend = { viewModel.sendMessage(it) })
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppHeader() {
//    TopAppBar(
//        title = { Text("ChatBot AI", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
//        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF6200EA), titleContentColor = Color.White)
//    )
//}
//
//@Composable
//fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
//    val listState = rememberLazyListState()
//    LaunchedEffect(messageList.size) {
//        if (messageList.isNotEmpty()) {
//            listState.animateScrollToItem(messageList.lastIndex)
//        }
//    }
//    LazyColumn(
//        modifier = modifier.padding(8.dp),
//        state = listState
//    ) {
//        items(messageList) {
//            MessageRow(it)
//        }
//    }
//}
//
//@Composable
//fun MessageRow(messageModel: MessageModel) {
//    val isModel = (messageModel.role == "model")
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End
//    ) {
//        Box(
//            modifier = Modifier
//                .padding(8.dp)
//                .clip(RoundedCornerShape(20.dp))
//                .background(if (isModel) modelColor else userColor)
//                .padding(horizontal = 16.dp, vertical = 12.dp)
//        ) {
//            Text(
//                text = messageModel.message,
//                fontWeight = FontWeight.Medium,
//                fontSize = 16.sp,
//                color = Color.White
//            )
//        }
//    }
//}
//
//@Composable
//fun MessageInput(viewModel: AppViewModel, onMessageSend: (String) -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .background(Color.White, RoundedCornerShape(12.dp))
//            .padding(horizontal = 8.dp, vertical = 4.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        OutlinedTextField(
//            value = viewModel.input.value,
//            onValueChange = { viewModel.updateInput(it) },
//            label = { Text("Type a message...") },
//            modifier = Modifier.weight(1f)
//        )
//        IconButton(
//            onClick = {
//                if (viewModel.input.value.isNotEmpty()) {
//                    onMessageSend(viewModel.input.value)
//                    viewModel.clearInput()
//                }
//            }
//        ) {
//            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF6200EA))
//        }
//    }
//}
//
//
//
//
//
//
