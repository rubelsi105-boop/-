package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.components.NeonCardBox
import com.example.ui.theme.CyberNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.PinkBorderBrush
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Storage
import com.example.model.DesignSettings
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    designSettings: DesignSettings = DesignSettings(),
    onUpdateProfile: (name: String, role: String, language: String) -> Unit,
    onToggleHaptic: () -> Unit,
    onToggleAutoSuggest: (() -> Unit)? = null,
    onToggleTheme: ((Boolean) -> Unit)? = null,
    onResetSettings: (() -> Unit)? = null,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.name) }
    var editRole by remember { mutableStateOf(userProfile.roleTitle) }
    var editLang by remember { mutableStateOf(userProfile.preferredLanguage) }

    val languages = listOf("Bilingual (বাংলা + English)", "বাংলা (Bengali)", "English Only")

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Holographic Glowing Profile Avatar Box
        item {
            NeonCardBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_avatar_box"),
                cornerRadius = 28.dp,
                borderBrush = Brush.horizontalGradient(listOf(NeonCyan, NeonMagenta, NeonPink))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            NeonPink.copy(alpha = 0.5f),
                                            NeonCyan.copy(alpha = 0.3f),
                                            Color.Transparent
                                        ),
                                        radius = size.width * 0.95f
                                    )
                                )
                            }
                            .border(
                                width = 2.5.dp,
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
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userProfile.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        IconButton(
                            onClick = {
                                editName = userProfile.name
                                editRole = userProfile.roleTitle
                                editLang = userProfile.preferredLanguage
                                showEditDialog = true
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = NeonPinkLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = userProfile.roleTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeonCyan
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userProfile.email,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Section 2: Stats Grid in Glowing Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Total Queries
                NeonCardBox(
                    modifier = Modifier.weight(1f),
                    cornerRadius = 20.dp,
                    borderBrush = Brush.horizontalGradient(listOf(NeonPink, NeonMagenta))
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Queries",
                        tint = NeonPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${userProfile.totalQueries}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "AI Queries",
                        fontSize = 12.sp,
                        color = TextSilver
                    )
                }

                // Card 2: Saved Snippets
                NeonCardBox(
                    modifier = Modifier.weight(1f),
                    cornerRadius = 20.dp,
                    borderBrush = Brush.horizontalGradient(listOf(NeonCyan, ElectricBlue))
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Drafts",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${userProfile.savedDraftsCount}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Saved Drafts",
                        fontSize = 12.sp,
                        color = TextSilver
                    )
                }
            }
        }

        // Section 3: Preferences
        item {
            NeonCardBox(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
                borderBrush = PinkBorderBrush
            ) {
                Text(
                    text = "⚙️ Preferences & Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPinkLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Language Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Preferred Language",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Text(
                                text = userProfile.preferredLanguage,
                                fontSize = 12.sp,
                                color = TextSilver
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            val nextLang = when (userProfile.preferredLanguage) {
                                "Bilingual (বাংলা + English)" -> "বাংলা (Bengali)"
                                "বাংলা (Bengali)" -> "English Only"
                                else -> "Bilingual (বাংলা + English)"
                            }
                            onUpdateProfile(userProfile.name, userProfile.roleTitle, nextLang)
                            Toast.makeText(context, "Language: $nextLang", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Change", color = NeonPink, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Haptic feedback toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Haptics",
                            tint = NeonPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Haptic & Cyber Click Feedback",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    }

                    Switch(
                        checked = userProfile.isHapticEnabled,
                        onCheckedChange = { onToggleHaptic() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonPink,
                            checkedTrackColor = NeonPinkLight.copy(alpha = 0.5f),
                            uncheckedThumbColor = TextSilver,
                            uncheckedTrackColor = CyberNavy
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // App Theme Preference toggle (Persistent DataStore)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "Dark Theme",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cyber Dark Theme",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Text(
                                text = if (designSettings.isDarkTheme) "ডিপ ব্ল্যাক নিয়ন থিম সক্রিয়" else "লাইট সাইবার থিম সক্রিয়",
                                fontSize = 11.sp,
                                color = TextSilver
                            )
                        }
                    }

                    Switch(
                        checked = designSettings.isDarkTheme,
                        onCheckedChange = { onToggleTheme?.invoke(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonCyan,
                            checkedTrackColor = NeonCyan.copy(alpha = 0.5f),
                            uncheckedThumbColor = TextSilver,
                            uncheckedTrackColor = CyberNavy
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Auto-Suggest Toggle (Persistent DataStore)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Auto Suggest",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Smart Prompt Suggestion",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Text(
                                text = if (userProfile.isAutoSuggestEnabled) "স্মার্ট প্রম্পট অটো সাজেস্ট অন" else "অটো সাজেস্ট অফ",
                                fontSize = 11.sp,
                                color = TextSilver
                            )
                        }
                    }

                    Switch(
                        checked = userProfile.isAutoSuggestEnabled,
                        onCheckedChange = { onToggleAutoSuggest?.invoke() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD54F),
                            checkedTrackColor = Color(0xFFFFD54F).copy(alpha = 0.5f),
                            uncheckedThumbColor = TextSilver,
                            uncheckedTrackColor = CyberNavy
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // DataStore Persistence Status Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x3300E5FF))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "DataStore",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Jetpack DataStore: থিম ও সেটিংস ডিভাইসে সংরক্ষিত",
                        fontSize = 11.5.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reset Settings to Default
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onResetSettings?.invoke()
                            Toast.makeText(context, "সকল সেটিংস ডিফল্টে রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Reset Settings",
                            tint = NeonPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Reset Preferences to Default",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    }

                    Text("Reset", color = NeonPink, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reset Chat History
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClearChat()
                            Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Clear Chat",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Clear Chat History",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFF8A80)
                        )
                    }

                    Text("Reset", color = Color(0xFFFF5252), fontSize = 12.sp)
                }
            }
        }

        // Section 4: About Rubel AI
        item {
            NeonCardBox(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
                borderBrush = Brush.horizontalGradient(listOf(NeonMagenta, NeonCyan))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About Rubel AI (অ্যাপ পরিচিতি)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rubel AI হলো একটি হাইপার-ফিউচারিস্টিক সাইবারনেটিক অ্যাসিস্ট্যান্ট, যা আপনার দৈনন্দিন কাজ, লেখালেখি, কোডিং এবং বাংলা-ইংরেজি অনুবাদের জন্য তৈরি করা হয়েছে।\n\nডিজাইনে ব্যবহৃত হয়েছে গোলাপি ও নিয়ন সায়ানের উজ্জ্বল গ্লোয়িং স্টাইলিশ বক্স, যা ছবিটির নিখুঁত রূপায়ন!",
                    fontSize = 13.sp,
                    color = TextSilver,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "App Name: Rubel", fontSize = 12.sp, color = NeonPinkLight)
                    Text(text = "Edition: Cyber Glow 2.0", fontSize = 12.sp, color = NeonCyan)
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Rubel Profile",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    OutlinedTextField(
                        value = editRole,
                        onValueChange = { editRole = it },
                        label = { Text("Role / Bio") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editName, editRole, editLang)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            },
            containerColor = Color(0xFF0F1738)
        )
    }
}
