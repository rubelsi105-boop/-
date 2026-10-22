package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight

/**
 * Visual 'typing...' animation indicator rendered inside a glowing AI message bubble.
 * Features 3 animated bouncing & pulsing neon dots with glowing text.
 */
@Composable
fun TypingIndicatorBubble(
    modifier: Modifier = Modifier,
    title: String = "Rubel AI / উত্তর তৈরি হচ্ছে...",
    caption: String = "typing..."
) {
    val cornerRadius = 22.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_typing_indicator_bubble")
            .drawBehind {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        listOf(NeonPink.copy(alpha = 0.25f), Color.Transparent),
                        radius = size.width * 0.9f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
                    style = Stroke(width = 4f)
                )
            }
            .border(
                width = 1.6.dp,
                brush = Brush.horizontalGradient(listOf(NeonPink, NeonMagenta, NeonCyan)),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(0xEE0A1238))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Header line
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NeonPink)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPinkLight,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bouncing dots and caption
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BouncingNeonDots()
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = caption,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = NeonCyan,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun BouncingNeonDots(
    modifier: Modifier = Modifier,
    dotSize: Dp = 10.dp,
    travelDistance: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots_bounce")

    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 130, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 260, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DotItem(
            offsetProgress = dot1Offset,
            color = NeonPink,
            dotSize = dotSize,
            travelDistance = travelDistance
        )
        DotItem(
            offsetProgress = dot2Offset,
            color = NeonMagenta,
            dotSize = dotSize,
            travelDistance = travelDistance
        )
        DotItem(
            offsetProgress = dot3Offset,
            color = NeonCyan,
            dotSize = dotSize,
            travelDistance = travelDistance
        )
    }
}

@Composable
private fun DotItem(
    offsetProgress: Float,
    color: Color,
    dotSize: Dp,
    travelDistance: Dp
) {
    val offsetY = -travelDistance * offsetProgress
    val scale = 0.8f + (offsetProgress * 0.4f)

    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .size(dotSize * scale)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(color.copy(alpha = 0.6f), Color.Transparent),
                        radius = size.width * 1.2f
                    )
                )
            }
            .clip(CircleShape)
            .background(color)
    )
}
