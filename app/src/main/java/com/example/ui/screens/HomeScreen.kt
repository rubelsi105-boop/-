package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.GeneratedApp
import com.example.model.MessageSender
import com.example.model.QuickPrompt
import com.example.model.SmartTool
import com.example.ui.components.AppStoreDashboard
import com.example.ui.components.BouncingNeonDots
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.PinkBorderBrush
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun formatDayHeader(timestamp: Long): String {
    val now = Calendar.getInstance()
    val msgCal = Calendar.getInstance().apply { timeInMillis = timestamp }

    return when {
        now.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR) -> "Today"
        now.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - msgCal.get(Calendar.DAY_OF_YEAR) == 1 -> "Yesterday"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

/**
 * Main Screen UI featuring a high-fidelity Neon Pink Cyber-themed Chat Interface
 * with Room Database history storage and smooth scroll-back interaction.
 */
@Composable
fun HomeScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    chatMessages: List<ChatMessage>,
    isGenerating: Boolean,
    quickPrompts: List<QuickPrompt>,
    smartTools: List<SmartTool>,
    selectedFilterChip: String,
    onFilterChipSelect: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
    currentlySpeakingId: String? = null,
    onSpeakMessage: ((String, String, Context) -> Unit)? = null,
    onStopSpeaking: (() -> Unit)? = null,
    onClearChat: (() -> Unit)? = null,
    onOpenAppStudio: () -> Unit = {},
    onDeleteChatMessage: ((String) -> Unit)? = null,
    onLaunchGeneratedApp: ((GeneratedApp) -> Unit)? = null,
    onNavigateToCreateApp: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var activeHomeMode by remember { mutableStateOf("STORE") }

    // Synchronize external search query with local input if query updated
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty() && inputText.isEmpty()) {
            inputText = searchQuery
        }
    }

    // Filter conversations if search query is active
    val filteredMessages = remember(chatMessages, searchQuery) {
        if (searchQuery.isBlank()) {
            chatMessages
        } else {
            chatMessages.filter { it.text.contains(searchQuery, ignoreCase = true) }
        }
    }

    // Auto-scroll to latest message when new message appears or AI is typing
    LaunchedEffect(chatMessages.size, isGenerating) {
        if (chatMessages.isNotEmpty() && !listState.isScrollInProgress) {
            listState.animateScrollToItem(chatMessages.size)
        }
    }

    // Detect if user has scrolled back up to read previous conversations
    val isScrolledBack by remember {
        derivedStateOf {
            val total = filteredMessages.size
            total > 2 && listState.firstVisibleItemIndex < total - 2
        }
    }

    fun handleSend() {
        val trimmed = inputText.trim()
        if (trimmed.isNotBlank()) {
            onSendMessage(trimmed)
            inputText = ""
            onSearchQueryChange("")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cyber_chat_screen")
    ) {
        // Dual Mode Switcher: Online App Store & Games vs AI Chat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isStore = activeHomeMode == "STORE"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isStore) NeonPink else Color(0x33101E4D))
                    .border(1.dp, if (isStore) NeonPinkLight else Color(0x4400F0FF), RoundedCornerShape(12.dp))
                    .clickable { activeHomeMode = "STORE" },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏪 অ্যাপ স্টোর ও গেম",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isStore) NeonCyan else Color(0x33101E4D))
                    .border(1.dp, if (!isStore) NeonCyan else Color(0x4400F0FF), RoundedCornerShape(12.dp))
                    .clickable { activeHomeMode = "CHAT" },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💬 এআই চ্যাট",
                    color = if (!isStore) Color.Black else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (activeHomeMode == "STORE") {
            AppStoreDashboard(
                onLaunchApp = { genApp -> onLaunchGeneratedApp?.invoke(genApp) },
                onNavigateToCreate = { prompt -> onNavigateToCreateApp?.invoke(prompt) }
            )
        } else {
            // 1. Material 3 Cyber Neon Header with Room DB History Counter
            CyberChatHeader(
                onOpenProfile = onOpenProfile,
                onOpenAppStudio = onOpenAppStudio,
                onClearChatRequest = { showClearConfirmDialog = true },
                messageCount = chatMessages.size
            )

        // 2. Material 3 Cyber Filter Chips Row
        CyberFilterChipsRow(
            selectedCategory = selectedFilterChip,
            onSelectCategory = { category ->
                onFilterChipSelect(category)
                // Filter or insert quick action inspiration
                when (category) {
                    "💡 Ideas" -> inputText = "নতুন ইউনিক মোবাইল অ্যাপ আইডিয়া দাও"
                    "💻 Code" -> inputText = "Kotlin Jetpack Compose এ অ্যানিমেশন কোড দেখাও"
                    "🌸 বাংলা" -> inputText = "আমায় নিয়ে বাংলায় একটি সুন্দর মিষ্টি কবিতা বলো"
                    "⚡ Quick" -> inputText = "Explain Artificial Intelligence simply"
                    else -> Unit
                }
            }
        )

        // 3. Middle Chat Message Feed with Scroll-Back History and Jump-to-Bottom
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("chat_messages_list"),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Hero Welcome Card if empty
                if (filteredMessages.isEmpty()) {
                    item {
                        if (searchQuery.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "No history found",
                                        tint = TextSilver,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "কোনো পূর্ববর্তী বার্তা মেলেনি: '$searchQuery'",
                                        color = TextSilver,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            CyberWelcomeHeroCard(
                                onSelectSuggestion = { suggestion ->
                                    inputText = suggestion
                                }
                            )
                        }
                    }
                }

                // Chat Messages Stream with Room Database History Date Separators
                itemsIndexed(filteredMessages, key = { _, msg -> msg.id }) { index, msg ->
                    val prevMsg = if (index > 0) filteredMessages[index - 1] else null
                    if (prevMsg == null || !isSameDay(prevMsg.timestamp, msg.timestamp)) {
                        CyberHistoryDateSeparator(timestamp = msg.timestamp)
                    }

                    if (msg.sender == MessageSender.USER) {
                        UserMessageBubble(
                            message = msg,
                            onDelete = { onDeleteChatMessage?.invoke(msg.id) }
                        )
                    } else {
                        AiAssistantMessageCard(
                            message = msg,
                            isSpeaking = currentlySpeakingId == msg.id,
                            onSpeak = {
                                if (onSpeakMessage != null) {
                                    onSpeakMessage(msg.id, msg.text, context)
                                }
                            },
                            onStopSpeak = { onStopSpeaking?.invoke() },
                            onDelete = { onDeleteChatMessage?.invoke(msg.id) }
                        )
                    }
                }

                // AI Generating / Typing Indicator Bubble
                if (isGenerating) {
                    item {
                        CyberAiTypingBubble()
                    }
                }
            }

            // Floating "Scroll to latest" button when user scrolls back up into history
            this@Column.AnimatedVisibility(
                visible = isScrolledBack,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .testTag("scroll_to_latest_button")
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xF00D0522))
                        .border(1.5.dp, Brush.horizontalGradient(listOf(NeonPink, NeonCyan)), RoundedCornerShape(20.dp))
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(filteredMessages.size)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Scroll to latest",
                            tint = NeonPinkLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scroll to latest",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4. Material 3 Bottom Chat Input Bar with Quick Prompts Carousel
        CyberChatBottomBar(
            inputText = inputText,
            onInputTextChange = {
                inputText = it
                onSearchQueryChange(it)
            },
            onSend = { handleSend() },
            isGenerating = isGenerating,
            quickPrompts = quickPrompts,
            onSelectPrompt = { promptText ->
                inputText = promptText
            }
        )
        }
    }

    // Confirmation dialog for clearing chat
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear Chat",
                        tint = NeonPink,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clear Chat History",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to clear the conversation history with Rubel AI? This will reset the chat stream.",
                    color = TextSilver,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        onClearChat?.invoke()
                        Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    modifier = Modifier.testTag("confirm_clear_chat_button")
                ) {
                    Text("Clear", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = NeonCyan)
                }
            },
            containerColor = Color(0xFF0C1338)
        )
    }
}

/**
 * Top Cyber Header featuring Neon Pink styling, Assistant Online Pulse, and Profile Avatar.
 */
@Composable
private fun CyberChatHeader(
    onOpenProfile: () -> Unit,
    onOpenAppStudio: () -> Unit,
    onClearChatRequest: () -> Unit,
    messageCount: Int = 0
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Avatar with glowing pink ring
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = NeonPink),
                    onClick = onOpenProfile
                )
                .testTag("home_avatar_profile_button")
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(NeonPink.copy(alpha = 0.45f), Color.Transparent),
                                radius = size.width * 0.9f
                            )
                        )
                    }
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(listOf(NeonPink, NeonMagenta, ElectricBlue)),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(Color(0xFF140828)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "R",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonPinkLight
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RUBEL AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FF2E93))
                            .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CYBER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonPinkLight,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Active • $messageCount in Room DB",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSilver
                    )
                }
            }
        }

        // Right Actions: Top [ App ] Button and Clear Chat
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Glowing [ App ] button on the top header
            Box(
                modifier = Modifier
                    .testTag("home_top_app_button")
                    .height(38.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = NeonCyan),
                        onClick = onOpenAppStudio
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(listOf(NeonCyan, ElectricBlue)),
                        shape = RoundedCornerShape(19.dp)
                    )
                    .background(Color(0x990A1A4E))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "App Studio",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "App",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Clear Chat history button
            IconButton(
                onClick = onClearChatRequest,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FF2E93))
                    .border(1.dp, NeonPink.copy(alpha = 0.4f), CircleShape)
                    .testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Clear Chat",
                    tint = NeonPinkLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Material 3 Filter Chips Row for rapid topic selection and AI interaction.
 */
@Composable
private fun CyberFilterChipsRow(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf("All", "💡 Ideas", "💻 Code", "🌸 বাংলা", "⚡ Quick")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category || (selectedCategory.isEmpty() && category == "All")

            FilterChip(
                selected = isSelected,
                onClick = { onSelectCategory(category) },
                label = {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0x330B1438),
                    labelColor = TextSilver,
                    selectedContainerColor = NeonPink,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) NeonPinkLight else NeonPink.copy(alpha = 0.35f),
                    borderWidth = if (isSelected) 1.5.dp else 1.dp
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("filter_chip_$category")
            )
        }
    }
}

/**
 * Welcome Hero Card displayed when the chat stream is fresh.
 */
@Composable
private fun CyberWelcomeHeroCard(
    onSelectSuggestion: (String) -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        listOf(NeonPink.copy(alpha = 0.25f), Color.Transparent),
                        radius = size.width * 0.9f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                    style = Stroke(width = 4f)
                )
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color(0xEE091136)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(NeonPink, NeonMagenta, NeonCyan)),
            width = 1.6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FF2E93))
                    .border(2.dp, NeonPink, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Assistant",
                    tint = NeonPinkLight,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome to Rubel Cyber AI",
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your futuristic AI assistant for creative ideas, code generation, and rapid answering in English & Bengali.",
                fontSize = 13.sp,
                color = TextSilver,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Suggestions prompt starters
            val starters = listOf(
                "🌸 বাংলায় একটি মিষ্টি অনুপ্রেরণামূলক কবিতা লিখো",
                "💡 একটি ইউনিক অ্যান্ড্রয়েড অ্যাপের কনসেপ্ট দাও",
                "💻 Explain Kotlin Coroutines and StateFlow with sample"
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                starters.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .background(Color(0x33120A2E))
                            .clickable { onSelectSuggestion(prompt) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Prompt",
                                tint = NeonPinkLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = prompt,
                                fontSize = 12.5.sp,
                                color = TextWhite,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CyberHistoryDateSeparator(timestamp: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x770D183E))
                .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Interaction Date",
                    tint = NeonCyan,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatDayHeader(timestamp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSilver
                )
            }
        }
    }
}

/**
 * User Message Bubble styled in Neon Pink Cyber aesthetic with Room deletion support.
 */
@Composable
private fun UserMessageBubble(
    message: ChatMessage,
    onDelete: (() -> Unit)? = null
) {
    val timeString = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            listOf(NeonPink.copy(alpha = 0.3f), Color.Transparent),
                            radius = size.width * 0.9f
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
                        style = Stroke(width = 3f)
                    )
                }
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomEnd = 18.dp, bottomStart = 18.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(listOf(NeonPink, NeonMagenta)),
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomEnd = 18.dp, bottomStart = 18.dp)
                )
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF38082D), Color(0xFF5A0947))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("user_message_${message.id}")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "You",
                            tint = NeonPinkLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "YOU",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPinkLight
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = timeString,
                            fontSize = 10.sp,
                            color = Color(0xFFA592B8)
                        )
                        if (onDelete != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier
                                    .size(18.dp)
                                    .testTag("delete_user_message_${message.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFA592B8),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.text,
                    fontSize = 14.5.sp,
                    color = TextWhite,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

/**
 * AI Assistant Message Card styled in Material 3 with cyber neon accents,
 * text-to-speech voice playback, clipboard copying, and Room persistence removal.
 */
@Composable
private fun AiAssistantMessageCard(
    message: ChatMessage,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onStopSpeak: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val timeString = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        ElevatedCard(
            modifier = Modifier
                .widthIn(max = 330.dp)
                .testTag("ai_message_${message.id}"),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xEE090F2E)
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.4.dp,
                        brush = Brush.horizontalGradient(listOf(NeonPink, NeonMagenta, NeonCyan)),
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp)
                    )
                    .padding(14.dp)
            ) {
                // Header with AI avatar, Name, TTS, Copy & Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FF2E93))
                                .border(1.2.dp, NeonPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "R",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonPinkLight
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "RUBEL AI",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPinkLight
                            )
                            Text(
                                text = "Cyber Assistant",
                                fontSize = 10.sp,
                                color = TextSilver
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // TTS read-out button
                        IconButton(
                            onClick = {
                                if (isSpeaking) onStopSpeak() else onSpeak()
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("chat_tts_${message.id}")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop voice" else "Read aloud",
                                tint = if (isSpeaking) NeonPink else TextSilver,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Copy button
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Rubel AI Response", message.text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("chat_copy_${message.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy message",
                                tint = TextSilver,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Delete individual interaction from Room DB
                        if (onDelete != null) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("chat_delete_${message.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete interaction",
                                    tint = TextSilver,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Message Text
                Text(
                    text = message.text,
                    fontSize = 14.5.sp,
                    color = TextWhite,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Footer timestamp and status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ Verified AI Output",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B87B8)
                    )

                    Text(
                        text = timeString,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

/**
 * Animated Cyber Typing Bubble shown while AI is synthesizing answers.
 */
@Composable
private fun CyberAiTypingBubble() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .border(
                    width = 1.4.dp,
                    brush = Brush.horizontalGradient(listOf(NeonPink, NeonMagenta, NeonCyan)),
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
                )
                .background(Color(0xEE090F2E))
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("chat_typing_indicator")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BouncingNeonDots()
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Rubel AI is typing...",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeonPinkLight
                )
            }
        }
    }
}

/**
 * Material 3 Cyber Chat Bottom Bar containing Quick Prompts Carousel and Stadium Input Field.
 */
@Composable
private fun CyberChatBottomBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isGenerating: Boolean,
    quickPrompts: List<QuickPrompt>,
    onSelectPrompt: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xF2060B24),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Quick Prompts Carousel
            if (quickPrompts.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    quickPrompts.take(4).forEach { prompt ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, NeonPink.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                                .background(Color(0x33140A2C))
                                .clickable { onSelectPrompt(prompt.prompt) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = prompt.iconEmoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = prompt.title,
                                    fontSize = 11.5.sp,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Stadium Cyber Input Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    NeonPink.copy(alpha = 0.35f),
                                    NeonCyan.copy(alpha = 0.35f)
                                )
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx()),
                            style = Stroke(width = 5f)
                        )
                    }
                    .border(
                        width = 1.8.dp,
                        brush = Brush.horizontalGradient(
                            listOf(NeonPink, NeonMagenta, ElectricBlue, NeonCyan)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xF0080E2C))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Leading sparkle icon
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Sparkle",
                        tint = NeonPinkLight,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Basic text input field
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Ask Rubel AI anything...",
                                fontSize = 14.5.sp,
                                color = Color(0xFFA5B4D4),
                                fontWeight = FontWeight.Normal
                            )
                        }

                        BasicTextField(
                            value = inputText,
                            onValueChange = onInputTextChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = TextWhite,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.Default
                            ),
                            cursorBrush = SolidColor(NeonPink),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onSend() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chat_input_field")
                        )
                    }

                    // Clear button if text exists
                    if (inputText.isNotEmpty()) {
                        IconButton(
                            onClick = { onInputTextChange("") },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("chat_clear_input_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextSilver,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Send Button with Neon Pink Gradient & minimum 48dp touch target
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() && !isGenerating) {
                                    Brush.linearGradient(listOf(NeonPink, NeonMagenta))
                                } else {
                                    Brush.linearGradient(listOf(Color(0xFF381532), Color(0xFF221128)))
                                }
                            )
                            .clickable(
                                enabled = inputText.isNotBlank() && !isGenerating,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Color.White),
                                onClick = onSend
                            )
                            .testTag("chat_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message",
                            tint = if (inputText.isNotBlank() && !isGenerating) Color.White else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
