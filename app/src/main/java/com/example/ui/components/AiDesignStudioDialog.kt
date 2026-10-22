package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DesignSettings
import com.example.ui.theme.CyberNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

/**
 * AI Design Studio Dialog opened by the Palette button:
 * Provides comprehensive control over primary color, button color, neon border,
 * background mode, corner radius, AI auto-styling suggestions, and persistent DataStore save/reset.
 */
@Composable
fun AiDesignStudioDialog(
    pinkGlowIntensity: Float,
    onUpdatePinkGlow: (Float) -> Unit,
    designSettings: DesignSettings = DesignSettings(),
    onSaveDesignSettings: ((DesignSettings) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val presetColors = listOf(
        Color(0xFFFF007F), // Neon Pink
        Color(0xFF00E5FF), // Cyber Cyan
        Color(0xFFA855F7), // Electric Purple
        Color(0xFF00E676), // Neon Green
        Color(0xFFFFB300), // Amber Gold
        Color(0xFFFF1744), // Crimson Red
        Color(0xFF0072FF)  // Sapphire Blue
    )

    var selectedPrimaryColor by remember(designSettings) {
        mutableStateOf(Color(designSettings.primaryColorHex.toULong()))
    }
    var selectedBorderColor by remember(designSettings) {
        mutableStateOf(Color(designSettings.neonBorderColorHex.toULong()))
    }
    var cornerRadius by remember(designSettings) {
        mutableFloatStateOf(designSettings.cornerRadiusDp.toFloat())
    }
    var isDarkTheme by remember(designSettings) {
        mutableStateOf(designSettings.isDarkTheme)
    }
    var fontScale by remember(designSettings) {
        mutableFloatStateOf(designSettings.fontScale)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = "Palette", tint = NeonPink, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Design Studio", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. AI Auto Design Suggestion Button
                Button(
                    onClick = {
                        val randomPrimary = presetColors.random()
                        val randomBorder = presetColors.random()
                        selectedPrimaryColor = randomPrimary
                        selectedBorderColor = randomBorder
                        onUpdatePinkGlow(kotlin.random.Random.nextDouble(0.5, 1.0).toFloat())
                        Toast.makeText(context, "✨ AI নতুন আকর্ষণীয় নিয়ন থিম তৈরি করেছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Suggest")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✨ AI দিয়ে নতুন ডিজাইন সাজাও", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // 2. Primary Accent Color Picker
                Text("১. মূল অ্যাকসেন্ট ও বাটন কালার:", color = NeonPinkLight, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEach { color ->
                        val isSelected = selectedPrimaryColor == color
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedPrimaryColor = color }
                        )
                    }
                }

                // 3. Neon Border Color Picker
                Text("২. নিয়ন বর্ডার ও গ্লো কালার:", color = NeonCyan, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEach { color ->
                        val isSelected = selectedBorderColor == color
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedBorderColor = color }
                        )
                    }
                }

                // 4. Glow Intensity Slider
                Text(
                    text = "৩. গোলাপি গ্লো ইনটেনসিটি: ${(pinkGlowIntensity * 100).toInt()}%",
                    color = TextSilver,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = pinkGlowIntensity,
                    onValueChange = onUpdatePinkGlow,
                    valueRange = 0.2f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonPink,
                        activeTrackColor = NeonPinkLight,
                        inactiveTrackColor = CyberNavy
                    )
                )

                // 5. Button Corner Radius
                Text(
                    text = "৪. বাটনের রাউন্ডেড কোণা: ${cornerRadius.toInt()} dp",
                    color = TextSilver,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = cornerRadius,
                    onValueChange = { cornerRadius = it },
                    valueRange = 8f..32f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = CyberNavy
                    )
                )

                // 6. Dark / Light Theme Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("৫. সাইবার ডার্ক থিম মোড", color = TextWhite, fontSize = 12.5.sp)
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF0D3174))
                    )
                }

                // Live Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(cornerRadius.dp))
                        .background(Color(0xFF0B1438))
                        .border(2.dp, Brush.horizontalGradient(listOf(selectedPrimaryColor, selectedBorderColor)), RoundedCornerShape(cornerRadius.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🎨 লাইভ প্রিভিউ টেস্ট", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = selectedPrimaryColor),
                            shape = RoundedCornerShape(cornerRadius.dp)
                        ) {
                            Text("নিয়ন বাটন স্টাইল", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = DesignSettings(
                        primaryColorHex = selectedPrimaryColor.value.toLong(),
                        buttonColorHex = selectedPrimaryColor.value.toLong(),
                        neonBorderColorHex = selectedBorderColor.value.toLong(),
                        cornerRadiusDp = cornerRadius.toInt(),
                        isDarkTheme = isDarkTheme,
                        fontScale = fontScale
                    )
                    onSaveDesignSettings?.invoke(updated)
                    Toast.makeText(context, "✅ কাস্টম ডিজাইন সেভ ও প্রয়োগ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
            ) {
                Text("ডিজাইন সেভ করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                val defaultSettings = DesignSettings()
                onSaveDesignSettings?.invoke(defaultSettings)
                onUpdatePinkGlow(0.85f)
                Toast.makeText(context, "ডিজাইন ডিফল্টে রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                onDismiss()
            }) {
                Text("রিসেট", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF081232)
    )
}
