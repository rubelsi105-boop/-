package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.GeneratedApp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.TextWhite
import com.example.util.AppConfigSharing

/**
 * Data model for each interactive App Row item shown in the studio list.
 */
data class AppStudioItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    var title: String,
    val category: String = "APP",
    var imageUri: Uri? = null,
    val defaultPrompt: String
)

/**
 * Dedicated Screen displayed when clicking the top [ App ] button.
 * Faithfully designed according to the user's uploaded reference picture:
 * - Real enchanted cyber nature background with plumeria flowers, lush leaves, and glowing water ripples
 * - 5 Stacked rows each containing:
 *     1. Left: Glowing 3D Glass rounded-square [ + ] button
 *     2. Main: Stadium capsule with cyan-to-magenta glowing neon border and pulsating cursor |
 *     3. Below: Side-by-side [ 📥 Download ] (cyan) and [ 🚀 Publish ] (magenta) glossy pill buttons
 */
@Composable
fun AppStudioListScreen(
    onClose: () -> Unit,
    onPublishApp: (appName: String, prompt: String, imageUri: Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Handle Android system back gesture/button to return smoothly
    BackHandler(onBack = onClose)

    // Pre-populate 5 interactive app slots as shown in the uploaded reference picture
    val appItems = remember {
        mutableStateListOf(
            AppStudioItem(
                title = "Rubel Cyber Calculator",
                category = "CALCULATOR",
                defaultPrompt = "একটি পূর্ণাঙ্গ সাইবারনেটিক সায়েন্টিফিক ও স্ট্যান্ডার্ড ক্যালকুলেটর অ্যাপ।"
            ),
            AppStudioItem(
                title = "Smart Todo & Checklist",
                category = "TODO",
                defaultPrompt = "দৈনন্দিন কাজের তালিকা ও চেকলিস্ট ম্যানেজার অ্যাপ।"
            ),
            AppStudioItem(
                title = "Digital Tally Counter",
                category = "COUNTER",
                defaultPrompt = "একটি ডিজিটাল ট্যালি কাউন্টার ও ক্লিকার অ্যাপ।"
            ),
            AppStudioItem(
                title = "Quiz & Brain Matrix",
                category = "QUIZ",
                defaultPrompt = "একটি ইন্টারেক্টিভ সাধারণ জ্ঞান কুইজ অ্যাপ।"
            ),
            AppStudioItem(
                title = "Cyber Weather Matrix",
                category = "WEATHER",
                defaultPrompt = "রিয়েল-টাইম নিয়ন সাইবার আবহাওয়া ও পূর্বাভাস অ্যাপ।"
            )
        )
    }

    var activeImageSlotIndex by remember { mutableStateOf<Int?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && activeImageSlotIndex != null) {
            val idx = activeImageSlotIndex!!
            if (idx in 0 until appItems.size) {
                appItems[idx] = appItems[idx].copy(imageUri = uri)
                Toast.makeText(context, "অ্যাপের ছবি যুক্ত করা হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        }
        activeImageSlotIndex = null
    }

    // Cursor pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_pulse")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030A1C))
            .testTag("app_studio_list_screen")
    ) {
        // 1. Photorealistic Enchanted Botanical & Aquatic Cyber Backdrop Image
        Image(
            painter = painterResource(id = R.drawable.bg_app_studio),
            contentDescription = "App Studio Scenery",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient scrim that reveals the top flowers & ribbon and bottom aquatic waves & flowers,
        // while softly masking the center with a clean, deep midnight cyber glass canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.Transparent,
                        0.07f to Color(0x88030C20),
                        0.13f to Color(0xF5030C20),
                        0.87f to Color(0xF5030C20),
                        0.93f to Color(0x88030C20),
                        1.00f to Color.Transparent
                    )
                )
        )

        // 2. Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header with Back button and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x88051230))
                        .border(1.5.dp, Brush.horizontalGradient(listOf(NeonCyan, ElectricBlue)), CircleShape)
                        .testTag("app_studio_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Title Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x9905143A))
                        .border(1.2.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPink)), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "APP STUDIO",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                    }
                }

                // Add Slot Button
                IconButton(
                    onClick = {
                        val newNumber = appItems.size + 1
                        appItems.add(
                            AppStudioItem(
                                title = "Custom Cyber App #$newNumber",
                                category = "GENERAL",
                                defaultPrompt = "একটি নতুন আকর্ষণীয় নিয়ন সাইবার মোবাইল অ্যাপ তৈরি করো।"
                            )
                        )
                        Toast.makeText(context, "নতুন অ্যাপ স্লট যোগ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x881A062C))
                        .border(1.5.dp, Brush.horizontalGradient(listOf(NeonMagenta, NeonPink)), CircleShape)
                        .testTag("add_app_slot_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add New App",
                        tint = NeonPinkLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Scrollable List of 5+ App Rows exactly mirroring the user's reference image
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(appItems, key = { _, item -> item.id }) { index, item ->
                    AppRowItem(
                        item = item,
                        index = index,
                        cursorAlpha = cursorAlpha,
                        onTitleChange = { newTitle ->
                            appItems[index] = appItems[index].copy(title = newTitle)
                        },
                        onPlusClick = {
                            activeImageSlotIndex = index
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onDownloadClick = {
                            val sampleApp = GeneratedApp(
                                appName = item.title,
                                description = item.defaultPrompt,
                                featuresConfigured = listOf("UI Engine", "Neon Glass Theme", "Live Preview", "Room DB"),
                                appCategory = item.category
                            )
                            val code = AppConfigSharing.generateConfigCode(sampleApp)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Rubel App Config", code))
                            Toast.makeText(context, "📥 '${item.title}' কনফিগারেশন কোড সেভ ও কপি হয়েছে!", Toast.LENGTH_LONG).show()
                        },
                        onPublishClick = {
                            onPublishApp(item.title, item.defaultPrompt, item.imageUri)
                            Toast.makeText(context, "🚀 '${item.title}' সফলভাবে পাবলিশ হচ্ছে!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

/**
 * Individual App Item Row:
 * - Left: Glowing rounded-square [ + ] button
 * - Main: Stadium capsule with glowing neon gradient border & blinking cursor
 * - Below: Side-by-side [ 📥 Download ] and [ 🚀 Publish ] buttons
 */
@Composable
private fun AppRowItem(
    item: AppStudioItem,
    index: Int,
    cursorAlpha: Float,
    onTitleChange: (String) -> Unit,
    onPlusClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_row_item_$index")
    ) {
        // 1. Top Row: [ + ] Button + Stadium Capsule Text Box ("লেখা বক্স")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Square [ + ] Glass Button (Matching picture: deep blue glass, specular gloss, cyan glow)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .drawBehind {
                        // Cyan outer glow
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFF00E5FF).copy(alpha = 0.55f), Color.Transparent),
                                radius = size.width * 0.95f
                            ),
                            cornerRadius = CornerRadius(18.dp.toPx()),
                            style = Stroke(width = 5f)
                        )
                    }
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF0072FF))),
                        shape = RoundedCornerShape(18.dp)
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
                        onClick = onPlusClick
                    )
                    .testTag("app_plus_button_$index"),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUri != null) {
                    AsyncImage(
                        model = item.imageUri,
                        contentDescription = "App Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    // Glass specular shine on top edge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(16.dp)
                            .align(Alignment.TopCenter)
                            .padding(top = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                                )
                            )
                    )

                    // Central white plus icon inside subtle glass well
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x2600F0FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Picture or Preset",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Right: Stadium Capsule Text Box ("লেখা বক্স" - Matching picture: cyan to pink glowing border & blinking cursor)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .drawBehind {
                        // Dual tone outer glow: Cyan on left, Pink on right
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF00E5FF).copy(alpha = 0.55f),
                                    Color(0xFF0072FF).copy(alpha = 0.35f),
                                    Color(0xFFFF007F).copy(alpha = 0.55f)
                                )
                            ),
                            cornerRadius = CornerRadius(28.dp.toPx()),
                            style = Stroke(width = 5f)
                        )
                    }
                    .clip(RoundedCornerShape(28.dp))
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
                        shape = RoundedCornerShape(28.dp)
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xEE08163D),
                                Color(0xF5040D26)
                            )
                        )
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                // Top specular highlight reflection
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
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vertical blinking cursor | exactly as depicted in the reference picture
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(Color(0xFF00E5FF).copy(alpha = cursorAlpha))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Text Field for app title / prompt
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (item.title.isEmpty()) {
                            Text(
                                text = "অ্যাপের নাম বা আইডিয়া লিখুন...",
                                color = Color(0x77A5B8DC),
                                fontSize = 14.sp
                            )
                        }

                        BasicTextField(
                            value = item.title,
                            onValueChange = onTitleChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = TextWhite,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Default
                            ),
                            cursorBrush = SolidColor(Color(0xFF00E5FF)),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_title_input_$index")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Bottom Row: [ Download ] and [ Publish ] pill buttons side by side (Matching picture)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 2A. [ 📥 Download ] Button (Electric Cyan Glass Pill)
            Box(
                modifier = Modifier
                    .height(35.dp)
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFF00E5FF).copy(alpha = 0.5f), Color.Transparent),
                                radius = size.width * 0.85f
                            ),
                            cornerRadius = CornerRadius(17.5.dp.toPx()),
                            style = Stroke(width = 3.5f)
                        )
                    }
                    .clip(RoundedCornerShape(17.5.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF0072FF))),
                        shape = RoundedCornerShape(17.5.dp)
                    )
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF0072FF),
                                Color(0xFF00B0FF),
                                Color(0xFF00E5FF)
                            )
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White),
                        onClick = onDownloadClick
                    )
                    .padding(horizontal = 14.dp)
                    .testTag("app_download_button_$index"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Circular glass icon container
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Download",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 2B. [ 🚀 Publish ] Button (Neon Magenta/Pink Glass Pill)
            Box(
                modifier = Modifier
                    .height(35.dp)
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFF007F).copy(alpha = 0.55f), Color.Transparent),
                                radius = size.width * 0.85f
                            ),
                            cornerRadius = CornerRadius(17.5.dp.toPx()),
                            style = Stroke(width = 3.5f)
                        )
                    }
                    .clip(RoundedCornerShape(17.5.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFF9C27B0))),
                        shape = RoundedCornerShape(17.5.dp)
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
                        onClick = onPublishClick
                    )
                    .padding(horizontal = 14.dp)
                    .testTag("app_publish_button_$index"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Circular glass icon container
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Publish",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Publish",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}
