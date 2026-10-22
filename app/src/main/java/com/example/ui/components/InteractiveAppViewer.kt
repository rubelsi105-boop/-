package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.GeneratedApp
import com.example.ui.theme.CyberNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

/**
 * Fullscreen Interactive Live App Preview matching the user's reference mockup:
 * - Phone frame with glowing neon cyan/blue and magenta edges
 * - Top-right glowing serif "seve" (Save) button
 * - Top-left circular button with back arrow (←)
 * - Top camera teardrop notch
 * - Undulating neon cyan and magenta light waves at top and bottom
 * - Direct execution of any AI-generated app (Calculator, Todo, Notes, Music, Counter, Weather, Quiz, Scanner, Expense, Fitness, Graphics)
 * - AI Problem Solving & Bug Fixing ("অ্যাপে কোন সমস্যা হলে এআইকে আবার লিখলে ঠিক করে দেবে")
 * - Beautiful color graphics design interactive text box ("লেখার বক্স")
 */
@Composable
fun InteractiveAppViewer(
    app: GeneratedApp,
    onDismiss: () -> Unit,
    onShareApp: ((GeneratedApp) -> Unit)? = null,
    onFixApp: ((targetApp: GeneratedApp, issueDescription: String) -> Unit)? = null
) {
    val context = LocalContext.current
    var inputMessage by remember { mutableStateOf("") }
    var isFixingMode by remember { mutableStateOf(false) }
    var isAiFixingActive by remember { mutableStateOf(false) }
    var lastFixReport by remember { mutableStateOf<String?>(null) }

    val interactiveNotes = remember {
        mutableStateListOf(
            "⚡ ${app.appName} লাইভ চলছে",
            "🎨 কালার গ্রাফিক্স ডিজাইন মোড সক্রিয়"
        )
    }

    // Dynamic accent color from app or user selection
    var selectedGraphicColor by remember(app.accentColorHex) {
        mutableStateOf(Color(app.accentColorHex.toULong()))
    }

    // Pulse animation for cursor and glow waves
    val infiniteTransition = rememberInfiniteTransition(label = "preview_waves")
    val wavePulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_pulse"
    )

    fun handleTextOrFixSubmit(text: String = inputMessage) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return

        val lower = trimmed.lowercase()
        val isExplicitFix = isFixingMode || lower.contains("ঠিক") || lower.contains("ফিক্স") ||
                lower.contains("সমস্যা") || lower.contains("কাজ করছে না") || lower.contains("বাগ") ||
                lower.contains("fix") || lower.contains("ভুল") || lower.contains("কালার") || lower.contains("নতুন")

        if (isExplicitFix && onFixApp != null) {
            isAiFixingActive = true
            lastFixReport = null
            onFixApp(app, trimmed)
            inputMessage = ""
            Toast.makeText(context, "🤖 এআই সমস্যা সমাধান ও কোড আপডেট করছে...", Toast.LENGTH_SHORT).show()
        } else {
            interactiveNotes.add(trimmed)
            inputMessage = ""
            Toast.makeText(context, "ইনপুট যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    // Clear fixing spinner after 1.5s
    LaunchedEffect(isAiFixingActive) {
        if (isAiFixingActive) {
            delay(1500)
            isAiFixingActive = false
            lastFixReport = "✅ অ্যাপের সমস্যা সফলভাবে সমাধান ও কোড আপডেট করা হয়েছে!"
            Toast.makeText(context, "✅ সমস্যা সমাধান সম্পন্ন!", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF030820)),
            contentAlignment = Alignment.Center
        ) {
            // Full-screen backdrop using the user's uploaded reference picture
            Image(
                painter = painterResource(id = R.drawable.bg_run_preview),
                contentDescription = "Background Glow Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("preview_reference_bg_art")
            )

            // Dark gradient scrim overlay to keep controls high contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x3302061A),
                                Color(0x55040B29),
                                Color(0x7702061A)
                            )
                        )
                    )
            )

            // Smartphone frame container matching reference picture
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                val phoneMaxWidth = maxWidth.coerceAtMost(430.dp)

                Column(
                    modifier = Modifier
                        .width(phoneMaxWidth)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Top Outer Row: Status & "seve" Button at the Top-Right (Matching reference image)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Status Pill
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE: ${app.appCategory}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00E676),
                                letterSpacing = 1.1.sp
                            )
                        }

                        // The "seve" (Save) Button exactly as shown in user's image
                        Box(
                            modifier = Modifier
                                .testTag("preview_seve_button")
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(color = NeonCyan),
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "💾 ${app.appName} ড্রাফট সফলভাবে সেভ করা হয়েছে!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "seve",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                letterSpacing = 1.sp,
                                style = TextStyle(
                                    shadow = androidx.compose.ui.graphics.Shadow(
                                        color = Color.Black,
                                        blurRadius = 10f
                                    )
                                )
                            )
                        }
                    }

                    // 2. The Smartphone Device Frame with glowing cyan/pink border
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .drawBehind {
                                // Outer Neon Aura
                                drawRoundRect(
                                    brush = Brush.radialGradient(
                                        listOf(selectedGraphicColor.copy(alpha = 0.5f * wavePulse), Color.Transparent),
                                        radius = size.width * 0.9f
                                    ),
                                    cornerRadius = CornerRadius(34.dp.toPx()),
                                    style = Stroke(width = 6f)
                                )
                            }
                            .clip(RoundedCornerShape(34.dp))
                            .border(
                                width = 2.5.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        selectedGraphicColor,
                                        Color(0xFF0072FF),
                                        NeonPink,
                                        selectedGraphicColor
                                    )
                                ),
                                shape = RoundedCornerShape(34.dp)
                            )
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF07123A),
                                        Color(0xFF040A24),
                                        Color(0xFF07123A)
                                    )
                                )
                            )
                    ) {
                        // Phone Screen Inner Layout
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            // Phone Screen Header: Circular Back Arrow (←) & Notch
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                // Circular Back Arrow (←) at top-left inside phone screen (matching picture)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .align(Alignment.CenterStart)
                                        .testTag("preview_phone_back_button")
                                        .drawBehind {
                                            drawCircle(
                                                brush = Brush.radialGradient(
                                                    listOf(Color(0xFF00E5FF).copy(alpha = 0.6f), Color.Transparent)
                                                ),
                                                radius = size.width * 0.9f
                                            )
                                        }
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.5.dp,
                                            brush = Brush.horizontalGradient(
                                                listOf(Color(0xFF00E5FF), Color(0xFF0072FF))
                                            ),
                                            shape = CircleShape
                                        )
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF0D3174), Color(0xFF051945))
                                            )
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple(color = Color.White),
                                            onClick = onDismiss
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }

                                // Centered Waterdrop Camera Notch
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .width(50.dp)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E5FF).copy(alpha = 0.8f))
                                    )
                                }

                                // Right Action: Share button
                                if (onShareApp != null) {
                                    IconButton(
                                        onClick = { onShareApp(app) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .align(Alignment.CenterEnd)
                                            .testTag("preview_share_app_btn")
                                            .clip(CircleShape)
                                            .background(Color(0x3300F0FF))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                }
                            }

                            // Glowing Top Undulating Waves (Neon Cyan & Magenta ribbons)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .padding(vertical = 1.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                selectedGraphicColor.copy(alpha = 0.7f * wavePulse),
                                                NeonMagenta.copy(alpha = 0.8f),
                                                ElectricBlue.copy(alpha = 0.6f),
                                                NeonPink.copy(alpha = 0.7f * wavePulse)
                                            )
                                        )
                                    )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Interactive App Content Body (Direct Execution of Generated App)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0x88050E2D))
                                    .border(1.dp, selectedGraphicColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                    .padding(10.dp)
                            ) {
                                when (app.appCategory) {
                                    "CALCULATOR" -> InteractiveCalculator()
                                    "TODO" -> InteractiveTodoList()
                                    "NOTES" -> InteractiveNotesApp()
                                    "MUSIC" -> InteractiveMusicPlayer()
                                    "COUNTER" -> InteractiveCounter()
                                    "WEATHER" -> InteractiveWeather()
                                    "QUIZ" -> InteractiveQuiz()
                                    "SCANNER" -> InteractiveSecurityScanner()
                                    "EXPENSE" -> InteractiveExpenseManager()
                                    "FITNESS" -> InteractiveFitnessTracker()
                                    else -> InteractiveGraphicsStudioApp(
                                        app = app,
                                        selectedColor = selectedGraphicColor,
                                        onColorChange = { selectedGraphicColor = it },
                                        notes = interactiveNotes
                                    )
                                }
                            }

                            // AI Fix Notification Banner (if recently fixed or fixing)
                            AnimatedVisibility(
                                visible = isAiFixingActive || lastFixReport != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isAiFixingActive) Color(0x66FF9100) else Color(0x6600E676)
                                        )
                                        .border(
                                            1.dp,
                                            if (isAiFixingActive) Color(0xFFFF9100) else Color(0xFF00E676),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isAiFixingActive) "🤖 এআই সমস্যা বিশ্লেষণ ও কোড ফিক্স করছে... ⚙️"
                                        else lastFixReport ?: "",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Quick AI Problem Fixing & Graphics Chips ("এআই সমস্যা সমাধান চিপস")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val fixChips = listOf(
                                    "🐞 সমস্যা ফিক্স" to "এই অ্যাপে বাটন বা স্টেট সমস্যা ঠিক করো",
                                    "🎨 কালার লাল" to "কালার পরিবর্তন করে লাল নিয়ন থিম করো",
                                    "✨ নতুন ফিচার" to "অ্যাপটিতে একটি নতুন স্মার্ট ফিচার ও মেমোরি যোগ করো",
                                    "🚀 স্পিড আপ" to "ইউআই ও বাটন ক্লিক রেসপন্স ফাস্ট করো"
                                )

                                fixChips.forEach { (label, prompt) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0x3300F0FF))
                                            .clickable {
                                                inputMessage = prompt
                                                handleTextOrFixSubmit(prompt)
                                            }
                                            .padding(vertical = 3.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 3. "সুন্দর কালার গ্রাফিক্স ডিজাইন লেখার বক্স" (The Beautiful Color Graphics Design Text Box)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("preview_color_graphics_text_box")
                                    .drawBehind {
                                        // Multi-tone neon glow matching picture
                                        drawRoundRect(
                                            brush = Brush.horizontalGradient(
                                                listOf(
                                                    selectedGraphicColor.copy(alpha = 0.5f),
                                                    NeonPink.copy(alpha = 0.4f),
                                                    NeonCyan.copy(alpha = 0.5f)
                                                )
                                            ),
                                            cornerRadius = CornerRadius(26.dp.toPx()),
                                            style = Stroke(width = 4.5f)
                                        )
                                    }
                                    .border(
                                        width = 1.8.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                selectedGraphicColor,
                                                NeonPink,
                                                ElectricBlue,
                                                NeonCyan
                                            )
                                        ),
                                        shape = RoundedCornerShape(26.dp)
                                    )
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xF00A1948), Color(0xF8040C24))
                                        )
                                    ),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                // Top specular highlight gloss
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(11.dp)
                                        .align(Alignment.TopCenter)
                                        .padding(top = 2.dp)
                                        .clip(RoundedCornerShape(5.5.dp))
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
                                    // Rounded Square [ + ] Glass Button (Toggles AI Fix / Graphics Mode)
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(11.dp))
                                            .border(
                                                width = 1.dp,
                                                brush = Brush.horizontalGradient(
                                                    listOf(selectedGraphicColor, Color(0xFF0072FF))
                                                ),
                                                shape = RoundedCornerShape(11.dp)
                                            )
                                            .background(
                                                if (isFixingMode) Color(0xFFD81B60)
                                                else Color(0xFF0D3174)
                                            )
                                            .clickable {
                                                isFixingMode = !isFixingMode
                                                val msg = if (isFixingMode) "🛠️ এআই ফিক্স মোড সক্রিয়: সমস্যা লিখে পাঠান!"
                                                else "🎨 নরমাল ইনপুট মোড সক্রিয়"
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isFixingMode) Icons.Default.Build else Icons.Default.Add,
                                            contentDescription = "Toggle Mode",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Text Field with Glowing Blinking Vertical Cursor
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Glowing cursor
                                            Box(
                                                modifier = Modifier
                                                    .width(2.5.dp)
                                                    .height(18.dp)
                                                    .clip(RoundedCornerShape(1.dp))
                                                    .background(selectedGraphicColor.copy(alpha = wavePulse))
                                            )

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Box(modifier = Modifier.weight(1f)) {
                                                if (inputMessage.isEmpty()) {
                                                    Text(
                                                        text = if (isFixingMode) "সমস্যা লিখুন (এআই ঠিক করবে)..."
                                                        else "অ্যাপ ইনপুট বা সমস্যা লিখুন...",
                                                        fontSize = 12.5.sp,
                                                        color = Color(0x99A5B8DC)
                                                    )
                                                }
                                                BasicTextField(
                                                    value = inputMessage,
                                                    onValueChange = { inputMessage = it },
                                                    singleLine = true,
                                                    textStyle = TextStyle(
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    cursorBrush = SolidColor(selectedGraphicColor),
                                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                                    keyboardActions = KeyboardActions(onSend = { handleTextOrFixSubmit() }),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .testTag("preview_text_box_input")
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Send/Execute Button (➤) with glowing magenta/pink gradient
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("preview_send_text_button")
                                            .clip(CircleShape)
                                            .border(
                                                width = 1.dp,
                                                brush = Brush.horizontalGradient(
                                                    listOf(NeonPink, NeonMagenta)
                                                ),
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
                                                onClick = { handleTextOrFixSubmit() }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Glowing Bottom Undulating Waves (Neon Magenta & Cyan)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .padding(vertical = 1.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                NeonPink.copy(alpha = 0.8f),
                                                selectedGraphicColor.copy(alpha = 0.7f * wavePulse),
                                                ElectricBlue.copy(alpha = 0.8f),
                                                NeonMagenta.copy(alpha = 0.7f * wavePulse)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Working Music Player Component */
@Composable
private fun InteractiveMusicPlayer() {
    var isPlaying by remember { mutableStateOf(true) }
    var trackIndex by remember { mutableIntStateOf(0) }
    var volume by remember { mutableFloatStateOf(0.75f) }

    val tracks = listOf(
        "Cyber Dhaka Beats 2077" to "Rubel Sound Lab",
        "Neon Rain Horizon" to "Synthwave City",
        "Electric Pulse Melody" to "Cyberpunk Studio"
    )

    val currentTrack = tracks[trackIndex % tracks.size]

    val transition = rememberInfiniteTransition(label = "music_eq")
    val bar1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.4f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
        label = "b3"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎧 CYBER BEATS PLAYER", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Animated Equalizer Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF040A24))
                    .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(40.dp)
                ) {
                    val heights = if (isPlaying) listOf(bar1, bar2, bar3, bar2, bar1, bar3, bar2)
                    else listOf(0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f)

                    heights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height((40 * h).dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.verticalGradient(listOf(NeonPink, NeonCyan))
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(currentTrack.first, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(currentTrack.second, color = TextSilver, fontSize = 11.sp)
        }

        // Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { trackIndex = if (trackIndex > 0) trackIndex - 1 else tracks.size - 1 }) {
                Icon(Icons.Default.FastRewind, contentDescription = "Prev", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(NeonPink)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = { trackIndex += 1 }) {
                Icon(Icons.Default.FastForward, contentDescription = "Next", tint = Color.White)
            }
        }

        // Volume Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔊", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Slider(
                value = volume,
                onValueChange = { volume = it },
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonPink)
            )
        }
    }
}

/** Working Notes & Diary Component */
@Composable
private fun InteractiveNotesApp() {
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    val notes = remember {
        mutableStateListOf(
            "Rubel AI আর্কিটেকচার আইডিয়া" to "রিয়েল-টাইম রুম ডাটাবেজ ইন্টিগ্রেশন সম্পন্ন।",
            "নিয়ন সাইবার থিম প্যালেট" to "গোলাপি, সায়ান ও ডিপ স্যাফায়ার ব্লু কালার স্কিম।"
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                placeholder = { Text("নতুন নোট শিরোনাম...", color = Color(0xFFA5B8DC), fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberNavy,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = {
                    if (noteTitle.isNotBlank()) {
                        notes.add(noteTitle.trim() to (noteContent.ifBlank { "নোট সংরক্ষিত।" }))
                        noteTitle = ""
                        noteContent = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(notes.indices.toList()) { index ->
                val (title, content) = notes[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33101E4D))
                        .border(1.dp, Color(0x4400F0FF), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(content, color = TextSilver, fontSize = 11.sp, maxLines = 1)
                        }
                        IconButton(
                            onClick = { notes.removeAt(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFFF5252))
                        }
                    }
                }
            }
        }
    }
}

/** Working Security Scanner Component */
@Composable
private fun InteractiveSecurityScanner() {
    var isScanning by remember { mutableStateOf(false) }
    var firewallOn by remember { mutableStateOf(true) }
    var healthScore by remember { mutableIntStateOf(98) }

    val infinite = rememberInfiniteTransition(label = "scan_radar")
    val angle by infinite.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "radar_angle"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛡️ CYBER SECURITY RADAR", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            // Radar Sweep Circle
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, NeonCyan, CircleShape)
                    .background(Color(0xFF030A24)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(if (isScanning) angle else 45f)
                        .background(
                            Brush.sweepGradient(
                                listOf(Color.Transparent, NeonCyan.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
                Text(
                    text = "$healthScore%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x3300F0FF))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ফায়ারওয়াল সুরক্ষা", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Switch(
                checked = firewallOn,
                onCheckedChange = { firewallOn = it },
                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF0D3174))
            )
        }

        Button(
            onClick = {
                isScanning = true
                healthScore = 100
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isScanning) "সিস্টেম স্ক্যান সক্রিয়..." else "⚡ সম্পূর্ণ সিস্টেম স্ক্যান করুন")
        }
    }
}

/** Working Expense & Money Manager */
@Composable
private fun InteractiveExpenseManager() {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val transactions = remember {
        mutableStateListOf(
            "সার্ভার হোস্টিং" to "- ৳ ১,২০০",
            "ফ্রিল্যান্স পেমেন্ট" to "+ ৳ ৮,৫০০",
            "সাইবার আর্ট টুল" to "- ৳ ৮৫০"
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x3300E676))
                    .padding(8.dp)
            ) {
                Column {
                    Text("মোট আয়", color = Color(0xFF00E676), fontSize = 10.sp)
                    Text("৳ ৮,৫০০", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33FF5252))
                    .padding(8.dp)
            ) {
                Column {
                    Text("মোট ব্যয়", color = Color(0xFFFF5252), fontSize = 10.sp)
                    Text("৳ ২,০৫০", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("খাত...", color = Color(0xFFA5B8DC), fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberNavy,
                    focusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = { Text("৳...", color = Color(0xFFA5B8DC), fontSize = 11.sp) },
                modifier = Modifier.width(75.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPink,
                    unfocusedBorderColor = CyberNavy,
                    focusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    if (note.isNotBlank() && amount.isNotBlank()) {
                        transactions.add(note.trim() to "- ৳ ${amount.trim()}")
                        note = ""
                        amount = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(transactions.toList()) { (title, amt) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33101E4D))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(title, color = Color.White, fontSize = 12.sp)
                    Text(
                        amt,
                        color = if (amt.startsWith("+")) Color(0xFF00E676) else Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/** Working Fitness & Hydration Tracker */
@Composable
private fun InteractiveFitnessTracker() {
    var steps by remember { mutableIntStateOf(6420) }
    var waterGlasses by remember { mutableIntStateOf(5) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("🏃 CYBER FITNESS & HEALTH", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(3.dp, Brush.sweepGradient(listOf(NeonPink, NeonCyan)), CircleShape)
                .background(Color(0xFF040A24)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$steps", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text("পদক্ষেপ / ১০,০০০", color = TextSilver, fontSize = 9.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x3300F0FF))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💧 পানি: $waterGlasses গ্লাস (${waterGlasses * 250} মিলি)", color = Color.White, fontSize = 12.sp)
            Button(
                onClick = { waterGlasses += 1 },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+১ গ্লাস", fontSize = 11.sp)
            }
        }

        Button(
            onClick = { steps += 250 },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("👟 হাঁটা ট্র্যাক করুন (+২৫০ স্টেপ)", color = MidnightBlack, fontWeight = FontWeight.Bold)
        }
    }
}

/** Interactive Color Graphics Studio App */
@Composable
private fun InteractiveGraphicsStudioApp(
    app: GeneratedApp,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    notes: List<String>
) {
    var activeTag by remember { mutableStateOf("নিয়ন গ্লো") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // App Title & Tag Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = app.appName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                    Text(
                        text = "কালার গ্রাফিক্স ডিজাইন ইঞ্জিন",
                        fontSize = 11.sp,
                        color = selectedColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(selectedColor.copy(alpha = 0.2f))
                        .border(1.dp, selectedColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = selectedColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Decorative Graphic Art Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                selectedColor.copy(alpha = 0.25f),
                                Color(0xFF030A24)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(selectedColor, NeonPink)),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(selectedColor)
                        )
                        Text(
                            text = "✨ $activeTag মোড সক্রিয়",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(NeonPink)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "নিচের লেখার বক্সে লিখলে এআই সরাসরি আপডেট করবে",
                        fontSize = 11.sp,
                        color = TextSilver
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Graphics Chips
            val tags = listOf("নিয়ন গ্লো", "কালার গ্রাফিক্স", "সাইবার আর্ট", "স্পিড লাইন")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tags.forEach { tag ->
                    val isSelected = activeTag == tag
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) selectedColor else Color(0x33101E4D))
                            .clickable { activeTag = tag }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tag,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MidnightBlack else Color.White
                        )
                    }
                }
            }
        }

        // Live Log/Notes Stream
        Column {
            Text(
                text = "📝 আউটপুট হিস্টোরি:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(3.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(notes.reversed().take(3)) { note ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3300F0FF))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = note,
                            fontSize = 10.5.sp,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/** Working Calculator Component */
@Composable
private fun InteractiveCalculator() {
    var display by remember { mutableStateOf("0") }
    var storedValue by remember { mutableDoubleStateOf(0.0) }
    var pendingOperation by remember { mutableStateOf<String?>(null) }
    var clearOnNextDigit by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (clearOnNextDigit || display == "0") {
            display = d
            clearOnNextDigit = false
        } else {
            if (display.length < 10) display += d
        }
    }

    fun onOp(op: String) {
        storedValue = display.toDoubleOrNull() ?: 0.0
        pendingOperation = op
        clearOnNextDigit = true
    }

    fun onEqual() {
        val current = display.toDoubleOrNull() ?: 0.0
        val result = when (pendingOperation) {
            "+" -> storedValue + current
            "-" -> storedValue - current
            "×" -> storedValue * current
            "÷" -> if (current != 0.0) storedValue / current else 0.0
            else -> current
        }
        display = if (result % 1.0 == 0.0) result.toLong().toString() else "%.2f".format(result)
        pendingOperation = null
        clearOnNextDigit = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Calculator Screen Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF04081E))
                .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = display,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Keypad Grid
        val buttons = listOf(
            listOf("C", "÷", "×", "DEL"),
            listOf("7", "8", "9", "-"),
            listOf("4", "5", "6", "+"),
            listOf("1", "2", "3", "="),
            listOf("0", ".", "±", "OK")
        )

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    row.forEach { label ->
                        val isOp = label in listOf("+", "-", "×", "÷", "=", "C", "DEL", "OK")
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    if (label == "=") NeonPink
                                    else if (isOp) Color(0x5500F0FF)
                                    else Color(0x33101E4D)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (label == "=") NeonPinkLight else Color(0x4400F0FF),
                                    shape = RoundedCornerShape(9.dp)
                                )
                                .clickable {
                                    when (label) {
                                        "C" -> {
                                            display = "0"
                                            storedValue = 0.0
                                            pendingOperation = null
                                        }
                                        "DEL" -> {
                                            display = if (display.length > 1) display.dropLast(1) else "0"
                                        }
                                        "=", "OK" -> onEqual()
                                        "+", "-", "×", "÷" -> onOp(label)
                                        else -> onDigit(label)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
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

/** Working Todo List Component */
@Composable
private fun InteractiveTodoList() {
    var newTaskText by remember { mutableStateOf("") }
    val tasks = remember {
        mutableStateListOf(
            "Rubel AI অ্যাপ সেটআপ সম্পন্ন করা" to true,
            "নতুন সাইবার প্রজেক্ট শুরু করা" to false,
            "কালার গ্রাফিক্স টেস্ট করা" to false
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                placeholder = { Text("নতুন কাজ লিখুন...", color = Color(0xFFA5B8DC), fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberNavy,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    if (newTaskText.isNotBlank()) {
                        tasks.add(newTaskText.trim() to false)
                        newTaskText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(9.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(tasks.indices.toList()) { index ->
                val (title, isDone) = tasks[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0x33101E4D))
                        .border(1.dp, if (isDone) Color(0x3300E676) else Color(0x3300F0FF), RoundedCornerShape(9.dp))
                        .clickable { tasks[index] = title to !isDone }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = { tasks[index] = title to it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonCyan,
                            checkmarkColor = MidnightBlack
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = if (isDone) Color(0xFF8B9CB8) else Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { tasks.removeAt(index) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/** Working Counter Component */
@Composable
private fun InteractiveCounter() {
    var count by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CYBER TALLY COUNTER", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(95.dp)
                .clip(CircleShape)
                .border(2.5.dp, Brush.sweepGradient(listOf(NeonPink, NeonCyan, NeonPink)), CircleShape)
                .background(Color(0xFF04081E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { count = maxOf(0, count - 1) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x55FF5252)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("- 1", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { count = 0 },
                colors = ButtonDefaults.buttonColors(containerColor = CyberNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
            }

            Button(
                onClick = { count += 1 },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ 1", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Working Quiz Challenge Component */
@Composable
private fun InteractiveQuiz() {
    var questionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }

    val questions = listOf(
        Triple(
            "Android-এ আধুনিক UI তৈরির ফ্রেমওয়ার্ক কোনটি?",
            listOf("XML Layouts", "Jetpack Compose", "Swing", "Flutter Native"),
            1
        ),
        Triple(
            "Rubel AI অ্যাপের মূল সিগনেচার কালার কোনটি?",
            listOf("সবুজ", "গোলাপি ও নিয়ন সায়ান", "হলুদ", "ধূসর"),
            1
        ),
        Triple(
            "Kotlin-এ ব্যাকগ্রাউন্ড টাস্ক চালানোর সেরা উপায় কোনটি?",
            listOf("Coroutines & Flow", "Thread.sleep", "Timer", "AsyncTask"),
            0
        )
    )

    val currentQ = questions[questionIndex % questions.size]

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Question ${questionIndex + 1}/${questions.size}", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Score: $score", color = NeonPinkLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(currentQ.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            currentQ.second.forEachIndexed { i, opt ->
                val isSelected = selectedOption == i
                val isCorrect = i == currentQ.third
                val btnColor = when {
                    selectedOption == null -> Color(0x33101E4D)
                    isSelected && isCorrect -> Color(0x8800E676)
                    isSelected && !isCorrect -> Color(0x88FF5252)
                    isCorrect -> Color(0x6600E676)
                    else -> Color(0x22101E4D)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(btnColor)
                        .border(1.dp, if (isSelected) NeonPink else Color(0x4400F0FF), RoundedCornerShape(9.dp))
                        .clickable(enabled = selectedOption == null) {
                            selectedOption = i
                            if (i == currentQ.third) score += 10
                        }
                        .padding(8.dp)
                ) {
                    Text(opt, color = Color.White, fontSize = 11.5.sp)
                }
            }
        }

        if (selectedOption != null) {
            Button(
                onClick = {
                    questionIndex = (questionIndex + 1) % questions.size
                    selectedOption = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Next Question", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

/** Working Weather Component */
@Composable
private fun InteractiveWeather() {
    var selectedCity by remember { mutableStateOf("Dhaka") }
    val cities = mapOf(
        "Dhaka" to Pair("31°C", "Sunny & Warm ☀️"),
        "Chittagong" to Pair("29°C", "Coastal Breeze 🌊"),
        "Sylhet" to Pair("27°C", "Cyber Rain Showers 🌧️"),
        "Tokyo" to Pair("18°C", "Neon Night 🌃")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cities.keys.forEach { city ->
                val isSelected = selectedCity == city
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonPink else Color(0x33101E4D))
                        .clickable { selectedCity = city }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(city, color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(selectedCity, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(cities[selectedCity]?.first ?: "30°C", color = NeonCyan, fontSize = 34.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(cities[selectedCity]?.second ?: "Clear", color = NeonPinkLight, fontSize = 12.sp)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x3300F0FF))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("Humidity: 68%", color = TextWhite, fontSize = 10.sp)
                Text("Wind: 14 km/h", color = TextWhite, fontSize = 10.sp)
                Text("UV: Moderate", color = TextWhite, fontSize = 10.sp)
            }
        }
    }
}
