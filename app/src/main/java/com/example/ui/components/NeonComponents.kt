package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RubelTab
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanPinkBorderBrush
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.PinkBorderBrush
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

/**
 * Neon cyber background with glowing electric cyan & magenta ribbons/waves,
 * mirroring the exact aesthetics of the user's reference image.
 */
@Composable
fun NeonWavesBackground(
    modifier: Modifier = Modifier,
    pinkIntensity: Float = 0.9f,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_motion")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top Ambient Dark Blue Vignette
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF04071C),
                        Color(0xFF081033),
                        Color(0xFF050B24),
                        Color(0xFF020412)
                    )
                )
            )

            // Top Cyan & Pink Glowing Waves (right behind header & search bar)
            val topWavePathCyan = Path().apply {
                moveTo(0f, height * 0.08f + waveOffset * 0.3f)
                cubicTo(
                    width * 0.25f, height * 0.09f + waveOffset * 0.4f,
                    width * 0.55f, height * 0.05f - waveOffset * 0.2f,
                    width, height * 0.07f + waveOffset * 0.2f
                )
            }
            drawPath(
                path = topWavePathCyan,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.15f),
                        NeonCyan.copy(alpha = 0.85f),
                        ElectricBlue.copy(alpha = 0.9f),
                        NeonMagenta.copy(alpha = 0.5f)
                    )
                ),
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )

            val topWavePathPink = Path().apply {
                moveTo(width * 0.35f, height * 0.07f - waveOffset * 0.2f)
                cubicTo(
                    width * 0.65f, height * 0.06f + waveOffset * 0.3f,
                    width * 0.82f, height * 0.09f - waveOffset * 0.4f,
                    width, height * 0.06f
                )
            }
            drawPath(
                path = topWavePathPink,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonMagenta.copy(alpha = 0.2f),
                        NeonPink.copy(alpha = 0.9f * pinkIntensity),
                        NeonPinkLight.copy(alpha = 0.95f)
                    )
                ),
                style = Stroke(width = 7f, cap = StrokeCap.Round)
            )

            // Bottom Flowing Glowing Wave (right above bottom navigation)
            val bottomWaveCyan = Path().apply {
                moveTo(0f, height * 0.89f + waveOffset * 0.15f)
                cubicTo(
                    width * 0.3f, height * 0.87f - waveOffset * 0.25f,
                    width * 0.65f, height * 0.91f + waveOffset * 0.2f,
                    width, height * 0.86f - waveOffset * 0.1f
                )
            }
            drawPath(
                path = bottomWaveCyan,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.95f),
                        ElectricBlue.copy(alpha = 0.9f),
                        NeonMagenta.copy(alpha = 0.7f)
                    )
                ),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            val bottomWavePink = Path().apply {
                moveTo(width * 0.45f, height * 0.88f)
                cubicTo(
                    width * 0.7f, height * 0.89f + waveOffset * 0.2f,
                    width * 0.88f, height * 0.86f - waveOffset * 0.3f,
                    width, height * 0.87f
                )
            }
            drawPath(
                path = bottomWavePink,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonPink.copy(alpha = 0.2f),
                        NeonPink.copy(alpha = 0.85f * pinkIntensity),
                        NeonPinkLight.copy(alpha = 0.95f)
                    )
                ),
                style = Stroke(width = 7f, cap = StrokeCap.Round)
            )
        }

        content()
    }
}

/**
 * Top Header displaying the neon glowing "R" circular badge, "RUBEL AI" title,
 * and "Your Intelligent AI Assistant" subtitle.
 */
@Composable
fun NeonTopHeader(
    modifier: Modifier = Modifier,
    title: String = "RUBEL AI",
    subtitle: String = "Your Intelligent AI Assistant",
    onAvatarClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Glowing circular avatar with "R"
        Box(
            modifier = Modifier
                .size(54.dp)
                .testTag("avatar_badge_button")
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 28.dp),
                    onClick = onAvatarClick
                )
                .drawBehind {
                    // Outer diffuse glow halo
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonPink.copy(alpha = 0.4f),
                                NeonCyan.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            radius = size.width * 0.95f
                        )
                    )
                }
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        listOf(NeonCyan, NeonPink, NeonMagenta, ElectricBlue, NeonCyan)
                    ),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF281156),
                            Color(0xFF0F1238),
                            MidnightBlack
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "R",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.5f),
                        radius = size.width * 0.45f
                    )
                }
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "RUBEL ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "AI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan,
                    letterSpacing = 1.5.sp
                )
            }
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSilver,
                letterSpacing = 0.3.sp
            )
        }
    }
}

/**
 * Neon glowing pill-shaped Search Bar matching the user's reference image exactly:
 * Rounded stadium border with cyan & pink gradient, cyan magnifying glass, and "Search...".
 */
@Composable
fun NeonSearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
    onSearchSubmit: (String) -> Unit = {}
) {
    val searchShape = RoundedCornerShape(percent = 50)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .height(56.dp)
            .testTag("search_container")
            // Diffuse outer glow
            .drawBehind {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(
                            NeonMagenta.copy(alpha = 0.3f),
                            NeonCyan.copy(alpha = 0.3f)
                        )
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2),
                    style = Stroke(width = 6f)
                )
            }
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        NeonMagenta,
                        NeonPink,
                        ElectricBlue,
                        NeonCyan
                    )
                ),
                shape = searchShape
            )
            .clip(searchShape)
            .background(Color(0xE6080D28)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Neon cyan search icon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = NeonCyan,
                modifier = Modifier
                    .size(26.dp)
                    .testTag("search_icon")
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA6BDDE)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(NeonPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                )
            }

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("search_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = TextSilver,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Neon Card / Box ("গোলাপি এক্স এক্স বক্স অনুযায়ী") with translucent cyber background,
 * glowing neon pink & cyan border, and rounded corners.
 */
@Composable
fun NeonCardBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    borderBrush: Brush = PinkBorderBrush,
    borderWidth: Dp = 1.5.dp,
    backgroundColor: Color = CyberCardBg,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = NeonPinkLight),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .then(clickableModifier)
            .drawBehind {
                drawRoundRect(
                    color = NeonPink.copy(alpha = 0.08f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
                    style = Stroke(width = 4f)
                )
            }
            .border(
                width = borderWidth,
                brush = borderBrush,
                shape = shape
            )
            .clip(shape)
            .background(backgroundColor)
            .padding(16.dp),
        content = content
    )
}

/**
 * Exact replica of the bottom navigation from the user's uploaded image:
 * 3 pill-shaped buttons: "Home", "App Edit", and "Profile".
 * Luminous cyan and pink glowing borders, active highlights, and smooth feedback.
 */
@Composable
fun NeonBottomNavigationBar(
    currentTab: RubelTab,
    onTabSelected: (RubelTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Home
            NeonNavPillButton(
                title = "Home",
                icon = Icons.Default.Home,
                isSelected = currentTab == RubelTab.HOME,
                primaryGlowColor = NeonCyan,
                accentGlowColor = ElectricBlue,
                testTag = "nav_tab_home",
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(RubelTab.HOME) }
            )

            // Tab 2: App Edit
            NeonNavPillButton(
                title = "App Edit",
                icon = Icons.Default.Edit,
                isSelected = currentTab == RubelTab.APP_EDIT,
                primaryGlowColor = NeonPink,
                accentGlowColor = NeonMagenta,
                testTag = "nav_tab_app_edit",
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(RubelTab.APP_EDIT) }
            )

            // Tab 3: Profile
            NeonNavPillButton(
                title = "Profile",
                icon = Icons.Default.Person,
                isSelected = currentTab == RubelTab.PROFILE,
                primaryGlowColor = NeonCyan,
                accentGlowColor = NeonPinkLight,
                testTag = "nav_tab_profile",
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(RubelTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun NeonNavPillButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    primaryGlowColor: Color,
    accentGlowColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val pillShape = RoundedCornerShape(22.dp)

    val borderBrush = Brush.sweepGradient(
        listOf(
            primaryGlowColor,
            accentGlowColor,
            NeonMagenta,
            primaryGlowColor
        )
    )

    val backgroundColor = if (isSelected) {
        Color(0x990C194D)
    } else {
        Color(0x7707123A)
    }

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) TextWhite else TextSilver,
        label = "nav_content_color"
    )

    Box(
        modifier = modifier
            .height(72.dp)
            .testTag(testTag)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = primaryGlowColor),
                onClick = onClick
            )
            .drawBehind {
                // Outer diffuse neon halo
                drawRoundRect(
                    brush = Brush.radialGradient(
                        listOf(
                            primaryGlowColor.copy(alpha = if (isSelected) 0.5f else 0.25f),
                            Color.Transparent
                        ),
                        radius = size.width * 0.95f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                    style = Stroke(width = if (isSelected) 6f else 3f)
                )
            }
            .border(
                width = if (isSelected) 2.2.dp else 1.6.dp,
                brush = borderBrush,
                shape = pillShape
            )
            .clip(pillShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(
                                    primaryGlowColor.copy(alpha = if (isSelected) 0.6f else 0.35f),
                                    Color.Transparent
                                ),
                                radius = 24.dp.toPx()
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = primaryGlowColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
