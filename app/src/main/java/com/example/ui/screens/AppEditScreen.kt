package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Share
import coil.compose.AsyncImage
import com.example.model.AppEditMessage
import com.example.model.DesignSettings
import com.example.model.GeneratedApp
import com.example.model.MessageSender
import com.example.ui.components.AiDesignStudioDialog
import com.example.ui.components.InteractiveAppViewer
import com.example.ui.components.ShareAppDialog
import com.example.ui.components.TypingIndicatorBubble
import com.example.util.AppConfigSharing
import com.example.ui.theme.CyberNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

/**
 * App Edit Screen fully customized per user specifications:
 * - Top: [App] pill and [পেলাস] pill with glowing borders
 * - Middle Feed: User & AI messages styled in glowing neon cyber boxes
 * - Each AI Box:
 *    - Full detailed configuration list of features added to the app
 *    - Interactive App Card with "🚀 Open App" button (launches working interactive preview)
 *    - 🔊/🎙️ Mic button (reads text aloud via TTS)
 *    - 🗑️ Delete button
 *    - 📋 Copy button
 * - Each User Box:
 *    - User's prompt inside glowing box
 *    - Attached image preview (if uploaded)
 *    - 🗑️ Delete button
 * - Bottom:
 *    - (+) Button opens Android Photo Picker to attach picture
 *    - "আপনার বার্তা লিখুন..." input field
 *    - (➤) Send button
 *    - Centered [⚡ Run] button with neon pulse speed lines
 *    - Automatic persistence of all created apps and messages
 */
@Composable
fun AppEditScreen(
    appEditMessages: List<AppEditMessage>,
    isAppBuilding: Boolean,
    currentlySpeakingId: String?,
    onBuildApp: (prompt: String, attachedImageUri: String?) -> Unit,
    onDeleteMessage: (id: String) -> Unit,
    onSpeakMessage: (id: String, text: String, context: Context) -> Unit,
    onStopSpeaking: () -> Unit,
    pinkGlowIntensity: Float,
    onUpdatePinkGlow: (Float) -> Unit,
    designSettings: DesignSettings = DesignSettings(),
    onSaveDesignSettings: ((DesignSettings) -> Unit)? = null,
    onFixApp: ((targetApp: GeneratedApp, issueDescription: String, onComplete: ((GeneratedApp) -> Unit)?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var messageText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var activePreviewApp by remember { mutableStateOf<GeneratedApp?>(null) }
    var appToShare by remember { mutableStateOf<GeneratedApp?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importCodeText by remember { mutableStateOf("") }

    var showCreateAppSetup by remember { mutableStateOf(false) }
    var showAppMenuDialog by remember { mutableStateOf(false) }
    var showPaletteDialog by remember { mutableStateOf(false) }

    if (showCreateAppSetup) {
        CreateAppSetupScreen(
            initialUserName = "Rubel",
            onClose = { showCreateAppSetup = false },
            onNext = { appName, userName, imageUri ->
                showCreateAppSetup = false
                val prompt = "নতুন অ্যাপ তৈরি করো: $appName (ইউজার: $userName)"
                onBuildApp(prompt, imageUri?.toString())
            }
        )
        return
    }

    if (showAppMenuDialog) {
        AppStudioListScreen(
            onClose = { showAppMenuDialog = false },
            onPublishApp = { appName, prompt, imageUri ->
                showAppMenuDialog = false
                val finalPrompt = if (prompt.isNotBlank()) prompt else "নতুন অ্যাপ তৈরি করো: $appName"
                onBuildApp(finalPrompt, imageUri?.toString())
            }
        )
        return
    }

    // Android Zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Toast.makeText(context, "পিকচার যুক্ত করা হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-scroll when new messages arrive
    LaunchedEffect(appEditMessages.size, isAppBuilding) {
        if (appEditMessages.isNotEmpty()) {
            listState.animateScrollToItem(appEditMessages.size - 1)
        }
    }

    // Pulse animation for speed lines
    val infiniteTransition = rememberInfiniteTransition(label = "run_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    fun handleSendOrRun() {
        val prompt = messageText.trim()
        val imageUriString = selectedImageUri?.toString()
        if (prompt.isBlank() && imageUriString == null) {
            val latestApp = appEditMessages.asReversed().mapNotNull { it.generatedApp }.firstOrNull()
            if (latestApp != null) {
                activePreviewApp = latestApp
            } else {
                activePreviewApp = GeneratedApp(
                    id = "run_demo_${System.currentTimeMillis()}",
                    appName = "Cyber Graphic Studio",
                    description = "সুন্দর কালার গ্রাফিক্স ডিজাইন ও লেখার বক্স সম্বলিত ইন্টারেক্টিভ অ্যাপ",
                    appCategory = "GRAPHICS",
                    featuresConfigured = listOf("কালার গ্রাফিক্স", "লেখার বক্স", "নিয়ন গ্লো", "seve ফাংশন")
                )
            }
            return
        }
        val lower = prompt.lowercase()
        val isFixRequest = lower.contains("ঠিক করো") || lower.contains("ফিক্স") || lower.contains("সমস্যা") ||
                lower.contains("কাজ করছে না") || lower.contains("বাগ") || lower.contains("fix") || lower.contains("ভুল")
        val latestApp = appEditMessages.asReversed().mapNotNull { it.generatedApp }.firstOrNull()

        if (isFixRequest && latestApp != null && onFixApp != null) {
            onFixApp(latestApp, prompt) { fixedApp ->
                activePreviewApp = fixedApp
            }
            messageText = ""
            selectedImageUri = null
            return
        }

        onBuildApp(prompt, imageUriString)
        messageText = ""
        selectedImageUri = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // 1. Top Bar: [App] pill on the left, [পেলাস] pill on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Pill: [ App ]
            Box(
                modifier = Modifier
                    .testTag("app_menu_pill_button")
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = NeonCyan),
                        onClick = { showAppMenuDialog = true }
                    )
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(NeonCyan.copy(alpha = 0.45f), Color.Transparent),
                                radius = size.width * 0.9f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                            style = Stroke(width = 4f)
                        )
                    }
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(listOf(NeonCyan, ElectricBlue)),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .background(Color(0x990A1A4E))
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "App",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "App",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Center Action: [ Share ] Button to export or share app design
            Box(
                modifier = Modifier
                    .testTag("app_editor_share_button")
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = NeonCyan),
                        onClick = {
                            val latestApp = appEditMessages.asReversed().mapNotNull { it.generatedApp }.firstOrNull()
                            if (latestApp != null) {
                                appToShare = latestApp
                            } else {
                                appToShare = GeneratedApp(
                                    appName = "Rubel Cyber Studio",
                                    description = "Custom AI Application Architecture",
                                    featuresConfigured = listOf("AI Engine", "Interactive Live Preview", "Neon Theme", "Room Local DB"),
                                    appCategory = "GENERAL"
                                )
                            }
                        }
                    )
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(NeonCyan.copy(alpha = 0.35f), Color.Transparent),
                                radius = size.width * 0.9f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                            style = Stroke(width = 3f)
                        )
                    }
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPink)),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .background(Color(0x990A1A4E))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share App Design",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Right Pill: [ পেলাস / প্লাস ]
            Box(
                modifier = Modifier
                    .testTag("create_app_setup_pill_button")
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = NeonPink),
                        onClick = { showPaletteDialog = true }
                    )
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(NeonPink.copy(alpha = 0.45f), Color.Transparent),
                                radius = size.width * 0.9f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                            style = Stroke(width = 4f)
                        )
                    }
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(listOf(NeonMagenta, NeonPink, NeonCyan)),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .background(Color(0x99240A42))
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "পেলাস",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "পেলাস",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 2. Middle Message Feed: All User & AI messages styled in glowing neon boxes
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(appEditMessages, key = { it.id }) { msg ->
                AppEditMessageBox(
                    message = msg,
                    isSpeaking = currentlySpeakingId == msg.id,
                    onOpenApp = { app -> activePreviewApp = app },
                    onShareApp = { app -> appToShare = app },
                    onSpeak = { onSpeakMessage(msg.id, msg.text, context) },
                    onStopSpeak = onStopSpeaking,
                    onDelete = { onDeleteMessage(msg.id) }
                )
            }

            if (isAppBuilding) {
                item {
                    TypingIndicatorBubble(
                        title = "Rubel AI Studio / অ্যাপ কনফিগার হচ্ছে...",
                        caption = "AI তৈরি ও টাইপ করছে..."
                    )
                }
            }
        }

        // Attached Picture Preview (if user selected an image via +)
        AnimatedVisibility(visible = selectedImageUri != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xDD091238))
                    .border(1.dp, NeonCyan, RoundedCornerShape(14.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Attached image",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "সংযুক্ত পিকচার",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "এই ছবির ওপর ভিত্তি করে অ্যাপ তৈরি হবে",
                        fontSize = 11.sp,
                        color = TextSilver
                    )
                }
                IconButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 3. Stadium Input Capsule ("লেখা বক্স") with Glowing Blue Glass (+) button on left and Neon Magenta (➤) on right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("app_edit_input_pill")
                .drawBehind {
                    // Dual tone outer glow: Cyan on left, Pink on right matching picture
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFF00E5FF).copy(alpha = 0.5f),
                                Color(0xFF0072FF).copy(alpha = 0.35f),
                                Color(0xFFFF007F).copy(alpha = 0.5f)
                            )
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(29.dp.toPx()),
                        style = Stroke(width = 5f)
                    )
                }
                .border(
                    width = 2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF0088FF),
                            Color(0xFFD500F9),
                            Color(0xFFFF007F)
                        )
                    ),
                    shape = RoundedCornerShape(29.dp)
                )
                .clip(RoundedCornerShape(29.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xEE08163D), Color(0xF5040D26))
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            // Top specular highlight gloss
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(14.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 2.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rounded Square [ + ] Glass Button (Matching picture: deep blue glass, specular gloss, cyan glow)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("upload_image_plus_button")
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.radialGradient(
                                    listOf(Color(0xFF00E5FF).copy(alpha = 0.5f), Color.Transparent),
                                    radius = size.width * 0.9f
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(13.dp.toPx()),
                                style = Stroke(width = 3.5f)
                            )
                        }
                        .clip(RoundedCornerShape(13.dp))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF0072FF))),
                            shape = RoundedCornerShape(13.dp)
                        )
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F3D8C),
                                    Color(0xFF0A225C),
                                    Color(0xFF040F2E)
                                )
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color(0xFF00E5FF)),
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Upload Picture",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Text Input ("লেখা বক্স") with Glowing Vertical Cursor |
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Glowing blinking vertical cursor | exactly as in reference picture
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(Color(0xFF00E5FF).copy(alpha = pulseAlpha))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            if (messageText.isEmpty()) {
                                Text(
                                    text = "আপনার অ্যাপের আইডিয়া বা বার্তা লিখুন...",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0x88A5B8DC)
                                )
                            }
                            BasicTextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = TextWhite,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(Color(0xFF00E5FF)),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = { handleSendOrRun() }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("app_edit_text_input")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Neon Magenta/Pink Send (➤) Button matching Publish pill
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("send_prompt_button")
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.radialGradient(
                                    listOf(Color(0xFFFF007F).copy(alpha = 0.5f), Color.Transparent),
                                    radius = size.width * 0.9f
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(21.dp.toPx()),
                                style = Stroke(width = 3.5f)
                            )
                        }
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFF9C27B0))),
                            shape = CircleShape
                        )
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF8E24AA),
                                    Color(0xFFD81B60),
                                    Color(0xFFFF007F)
                                )
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.White),
                            onClick = { handleSendOrRun() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Centered [ ⚡ Run ] Button with Neon Speed Lines
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Speed Lines
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(3.dp)
                        .background(
                            NeonCyan.copy(alpha = pulseAlpha),
                            RoundedCornerShape(2.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .height(3.5.dp)
                        .background(
                            NeonCyan.copy(alpha = pulseAlpha * 0.9f),
                            RoundedCornerShape(2.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(3.dp)
                        .background(
                            NeonCyan.copy(alpha = pulseAlpha * 0.7f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // The [ ⚡ Run ] Button
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(50.dp)
                    .testTag("run_action_button")
                    .clip(RoundedCornerShape(25.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White),
                        onClick = { handleSendOrRun() }
                    )
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(NeonCyan.copy(alpha = 0.5f), Color.Transparent),
                                radius = size.width * 0.9f
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(25.dp.toPx()),
                            style = Stroke(width = 6f)
                        )
                    }
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(NeonPink, NeonMagenta, ElectricBlue, NeonCyan)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                NeonPink,
                                NeonMagenta,
                                ElectricBlue,
                                NeonCyan
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Run",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Run",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right Speed Lines
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(3.dp)
                        .background(
                            NeonCyan.copy(alpha = pulseAlpha),
                            RoundedCornerShape(2.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .height(3.5.dp)
                        .background(
                            NeonCyan.copy(alpha = pulseAlpha * 0.9f),
                            RoundedCornerShape(2.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(3.dp)
                        .background(
                            NeonCyan.copy(alpha = pulseAlpha * 0.7f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }

    // Live Working Interactive App Preview Modal
    activePreviewApp?.let { app ->
        InteractiveAppViewer(
            app = app,
            onDismiss = { activePreviewApp = null },
            onShareApp = { appToShare = it },
            onFixApp = { targetApp, issue ->
                onFixApp?.invoke(targetApp, issue) { updated ->
                    activePreviewApp = updated
                }
            }
        )
    }

    // Share App Dialog (Exports deep link, shareable config code, and system share)
    appToShare?.let { app ->
        ShareAppDialog(
            app = app,
            onDismiss = { appToShare = null }
        )
    }

    // AI Design Studio Dialog (when [পেলাস] / Palette is clicked)
    if (showPaletteDialog) {
        AiDesignStudioDialog(
            pinkGlowIntensity = pinkGlowIntensity,
            onUpdatePinkGlow = onUpdatePinkGlow,
            designSettings = designSettings,
            onSaveDesignSettings = onSaveDesignSettings,
            onDismiss = { showPaletteDialog = false }
        )
    }

    // Import App Dialog (Imports from deep link or config code)
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = {
                showImportDialog = false
                importCodeText = ""
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Import App",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Import Shared App",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Paste a Rubel AI app deep link or configuration code (token) to load and launch the custom app design:",
                        color = TextSilver,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = importCodeText,
                        onValueChange = { importCodeText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("import_code_input"),
                        placeholder = {
                            Text("RUBEL-APP://... or https://rubel.ai/...", color = Color.Gray, fontSize = 12.sp)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = NeonCyan.copy(alpha = 0.5f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val item = clipboard.primaryClip?.getItemAt(0)
                                if (item != null) {
                                    importCodeText = item.text?.toString() ?: ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Paste", tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Paste from Clipboard", color = NeonCyan, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedApp = AppConfigSharing.parseConfigCode(importCodeText)
                        if (parsedApp != null) {
                            showImportDialog = false
                            importCodeText = ""
                            activePreviewApp = parsedApp
                            onBuildApp("Imported custom app: ${parsedApp.appName}", null)
                            Toast.makeText(context, "App '${parsedApp.appName}' successfully imported!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Invalid app code or link format", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("submit_import_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Import & Run", color = Color(0xFF03071E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImportDialog = false
                    importCodeText = ""
                }) {
                    Text("Cancel", color = TextSilver)
                }
            },
            containerColor = Color(0xFF091238)
        )
    }
}

/**
 * Message Card Component:
 * Both User and AI messages are enclosed inside glowing neon cyber boxes,
 * with mic button, delete button, copy button, and interactive Open App preview.
 */
@Composable
private fun AppEditMessageBox(
    message: AppEditMessage,
    isSpeaking: Boolean,
    onOpenApp: (GeneratedApp) -> Unit,
    onShareApp: (GeneratedApp) -> Unit,
    onSpeak: () -> Unit,
    onStopSpeak: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val isUser = message.sender == MessageSender.USER
    val cornerRadius = 22.dp

    val borderBrush = if (isUser) {
        Brush.horizontalGradient(listOf(NeonCyan, ElectricBlue, NeonPinkLight))
    } else {
        Brush.horizontalGradient(listOf(NeonPink, NeonMagenta, NeonCyan))
    }

    val glowColor = if (isUser) NeonCyan else NeonPink

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        listOf(glowColor.copy(alpha = 0.22f), Color.Transparent),
                        radius = size.width * 0.9f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
                    style = Stroke(width = 4f)
                )
            }
            .border(
                width = 1.6.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(if (isUser) Color(0xD90A1747) else Color(0xEE0A1238))
            .padding(14.dp)
    ) {
        // Top Header of the Box: Sender Title and Action Buttons (Mic & Delete)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isUser) NeonCyan else NeonPink)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isUser) "User Request / আপনার প্রম্পট" else "Rubel AI Studio / তৈরি অ্যাপ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) NeonCyan else NeonPinkLight,
                    letterSpacing = 0.5.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mic Button (TTS voice read out)
                if (!isUser) {
                    IconButton(
                        onClick = {
                            if (isSpeaking) onStopSpeak() else onSpeak()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("tts_mic_button_${message.id}")
                            .clip(CircleShape)
                            .background(if (isSpeaking) NeonPink else Color(0x3300F0FF))
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Voice Reader",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Copy Button
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Rubel Message", message.text)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "টেক্সট কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = TextSilver,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("delete_message_button_${message.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Attached image preview if user uploaded one
        if (message.imageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = message.imageUri,
                    contentDescription = "Uploaded App Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Text Content
        Text(
            text = message.text,
            fontSize = 14.sp,
            color = TextWhite,
            lineHeight = 21.sp
        )

        // If AI generated an interactive app, display the App Card with "Open App"
        if (message.generatedApp != null) {
            Spacer(modifier = Modifier.height(12.dp))

            val app = message.generatedApp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF070F30))
                    .border(1.2.dp, Brush.horizontalGradient(listOf(NeonPink, NeonCyan)), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📱 ${app.appName}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = app.description,
                            fontSize = 12.sp,
                            color = TextSilver,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onShareApp(app) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("share_app_button_${app.id}")
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x3300F0FF))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share App",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Button(
                            onClick = { onOpenApp(app) },
                            modifier = Modifier.testTag("launch_app_${app.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Launch,
                                contentDescription = "Open App",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Open App",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
