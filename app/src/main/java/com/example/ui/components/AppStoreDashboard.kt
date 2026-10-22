package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GeneratedApp
import com.example.model.StoreApp
import com.example.model.StoreComment
import com.example.ui.theme.CyberNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkLight
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import com.example.util.AppConfigSharing
import com.example.util.ProjectZipGenerator

/**
 * Online App Store, Game Maker & Community Showcase Dashboard:
 * Displays trending apps, games, download statistics, ratings, real ZIP export,
 * interactive app launch, publishing rules, and user reviews.
 */
@Composable
fun AppStoreDashboard(
    onLaunchApp: (GeneratedApp) -> Unit,
    onNavigateToCreate: (initialPrompt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedAppDetail by remember { mutableStateOf<StoreApp?>(null) }
    var showPublishGuidelinesDialog by remember { mutableStateOf(false) }
    var showUploadModal by remember { mutableStateOf(false) }

    val favoriteIds = remember { mutableStateListOf<String>() }

    // Pre-populated verified online apps and games in the store
    val storeApps = remember {
        mutableStateListOf(
            StoreApp(
                id = "store_app_1",
                appName = "Cyber Maze 2077",
                packageName = "com.rubel.game.cybermaze",
                description = "সাইবারনেটিক মেজ এস্কেপ ও পাজল চ্যালেঞ্জ গেম। নিয়ন লাইট ও ইন্টারেক্টিভ স্কোর।",
                category = "Games",
                authorName = "CyberDev Rubel",
                rating = 4.9f,
                downloadCount = 3840,
                version = "1.2.0",
                accentColorHex = 0xFFFF2E93,
                iconEmoji = "🎮",
                isGame = true,
                isFeatured = true,
                features = listOf("3D মেজ লেআউট", "টাইমার স্কোরবোর্ড", "সাউন্ড এফেক্টস", "লোকাল সেভ"),
                comments = listOf(
                    StoreComment(authorName = "তানভীর", commentText = "অসাধারণ গেম! নিয়ন গ্রাফিক্স চমৎকার।", rating = 5),
                    StoreComment(authorName = "Arif Hasan", commentText = "Smooth performance and great UI!", rating = 5)
                )
            ),
            StoreApp(
                id = "store_app_2",
                appName = "Rubel Cyber Beats Player",
                packageName = "com.rubel.app.beats",
                description = "ইন্টারেক্টিভ অডিও ভিজ্যুয়ালাইজার, ইকুয়ালাইজার ও সাইবার মিউজিক প্লেয়ার।",
                category = "Media",
                authorName = "Rubel Audio Lab",
                rating = 4.8f,
                downloadCount = 2950,
                version = "1.0.5",
                accentColorHex = 0xFF00E5FF,
                iconEmoji = "🎵",
                isFeatured = true,
                features = listOf("লাইভ ওয়েভফর্ম", "প্লেব্যাক কন্ট্রোল", "ভলিউম স্লাইডার", "অফলাইন সাপোর্ট"),
                comments = listOf(
                    StoreComment(authorName = "মিজানুর", commentText = "অডিও কোয়ালিটি এবং ইকুয়ালাইজার দারুণ!", rating = 5)
                )
            ),
            StoreApp(
                id = "store_app_3",
                appName = "Space Defender 2D",
                packageName = "com.rubel.game.spacedefender",
                description = "স্পেস আর্কেড ও এলিয়েন ডিফেন্স গেম। টাচ কন্ট্রোল ও বুলেট স্প্রেড।",
                category = "Games",
                authorName = "Pixel Studio",
                rating = 4.7f,
                downloadCount = 1840,
                version = "1.0.1",
                accentColorHex = 0xFF00E676,
                iconEmoji = "🚀",
                isGame = true,
                features = listOf("স্পেসশিপ কন্ট্রোল", "স্কোর মাল্টিপ্লায়ার", "বস ফাইট", "হ্যাপটিক ভাইব্রেশন"),
                comments = listOf(
                    StoreComment(authorName = "রাফি", commentText = "টাইম পাস করার জন্য অসাধারণ আর্কেড গেম।", rating = 4)
                )
            ),
            StoreApp(
                id = "store_app_4",
                appName = "Smart Task & Habit Master",
                packageName = "com.rubel.app.taskmaster",
                description = "দৈনন্দিন কাজের তালিকা, রিমাইন্ডার ও হ্যাবিট চেকলিস্ট ম্যানেজার।",
                category = "Productivity",
                authorName = "Rubel Productivity",
                rating = 4.9f,
                downloadCount = 4210,
                version = "2.1.0",
                accentColorHex = 0xFFA855F7,
                iconEmoji = "✅",
                isFeatured = true,
                features = listOf("ক্যাটাগরি ট্যাগ", "অটো সেভ রুম ডিবি", "ডিলিট ও ফিল্টার", "ডার্ক মোড")
            ),
            StoreApp(
                id = "store_app_5",
                appName = "Cyber Security Radar",
                packageName = "com.rubel.app.securityradar",
                description = "সিস্টেম ডায়াগনস্টিক, সিকিউরিটি স্ক্যানার ও ফায়ারওয়াল মনিটর।",
                category = "Tools",
                authorName = "Security Labs",
                rating = 4.8f,
                downloadCount = 2120,
                version = "1.1.4",
                accentColorHex = 0xFF00E5FF,
                iconEmoji = "🛡️",
                features = listOf("অ্যানিমেটেড রাডার", "থ্রেট স্কোর", "মেমোরি অপ্টিমাইজার", "কুইক রিপোর্ট")
            ),
            StoreApp(
                id = "store_app_6",
                appName = "Cyber Scientific Calculator",
                packageName = "com.rubel.app.calculator",
                description = "ডিজিটাল ডিসপ্লে ও নির্ভুল ম্যাথ ইঞ্জিনযুক্ত সাইবার ক্যালকুলেটর।",
                category = "Tools",
                authorName = "Math Cyber",
                rating = 4.9f,
                downloadCount = 5600,
                version = "2.0.0",
                accentColorHex = 0xFFFF007F,
                iconEmoji = "🧮",
                features = listOf("স্ট্যান্ডার্ড ও সাইন্টিফিক মোড", "হিস্টোরি টেপ", "দশমিক অপারেশন", "মেমোরি ক্লিয়ার")
            )
        )
    }

    // Filter apps based on search and category
    val filteredApps = remember(storeApps, searchQuery, selectedCategory) {
        storeApps.filter { app ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Games" -> app.isGame
                "Apps" -> !app.isGame
                "Featured" -> app.isFeatured
                else -> app.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                app.appName.contains(searchQuery, ignoreCase = true) ||
                        app.description.contains(searchQuery, ignoreCase = true) ||
                        app.authorName.contains(searchQuery, ignoreCase = true) ||
                        app.category.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // 1. Search Bar & Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("অ্যাপ, গেম বা ডেভেলপারের নাম খুঁজুন...", color = Color(0xFFA5B8DC), fontSize = 12.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonCyan, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("store_search_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberNavy,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Upload / Publish App Button
            Button(
                onClick = { showUploadModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .height(46.dp)
                    .testTag("store_upload_app_btn")
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = "Upload", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("আপলোড", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Category Filter Chips
        val categories = listOf("All", "Games", "Apps", "Featured", "Productivity", "Tools", "Media")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonCyan else Color(0x33101E4D))
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else Color(0x4400F0FF),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (cat == "Games") "🎮 Games" else if (cat == "Apps") "📱 Apps" else cat,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Main Store Feed (Scrollable)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Hero Banner: Guidelines & AI Builder quick shortcut
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0D2564), Color(0xFF1E0A3A))
                            )
                        )
                        .border(1.2.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPink)), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("⚡ Rubel AI App & Game Store", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("যেকোনো অ্যাপ ইনস্ট্যান্ট চালিয়ে দেখুন ও সম্পূর্ণ সোর্স ZIP নিন।", color = TextSilver, fontSize = 11.sp)
                        }
                        TextButton(
                            onClick = { showPublishGuidelinesDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = NeonPinkLight)
                        ) {
                            Text("নিয়মাবলী ℹ️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Featured Section
            if (selectedCategory == "All" || selectedCategory == "Featured") {
                item {
                    Text("🔥 ট্রেন্ডিং ও জনপ্রিয় অ্যাপস", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(storeApps.filter { it.isFeatured }) { featuredApp ->
                            FeaturedAppCard(
                                app = featuredApp,
                                onOpenDetail = { selectedAppDetail = featuredApp },
                                onRun = {
                                    val genApp = GeneratedApp(
                                        appName = featuredApp.appName,
                                        description = featuredApp.description,
                                        featuresConfigured = featuredApp.features,
                                        appCategory = if (featuredApp.isGame) "QUIZ" else "GENERAL",
                                        accentColorHex = featuredApp.accentColorHex
                                    )
                                    onLaunchApp(genApp)
                                }
                            )
                        }
                    }
                }
            }

            // Main App Cards
            items(filteredApps) { app ->
                val isFav = favoriteIds.contains(app.id)
                StoreAppCard(
                    app = app,
                    isFavorite = isFav,
                    onToggleFavorite = {
                        if (isFav) favoriteIds.remove(app.id) else favoriteIds.add(app.id)
                    },
                    onOpenDetail = { selectedAppDetail = app },
                    onRunApp = {
                        val genApp = GeneratedApp(
                            appName = app.appName,
                            description = app.description,
                            featuresConfigured = app.features,
                            appCategory = if (app.isGame) "QUIZ" else "GENERAL",
                            accentColorHex = app.accentColorHex
                        )
                        onLaunchApp(genApp)
                    },
                    onDownloadZip = {
                        val genApp = GeneratedApp(
                            appName = app.appName,
                            description = app.description,
                            featuresConfigured = app.features,
                            appCategory = if (app.isGame) "QUIZ" else "GENERAL"
                        )
                        ProjectZipGenerator.shareProjectZip(context, genApp)
                    }
                )
            }
        }
    }

    // App Detail Modal Dialog
    selectedAppDetail?.let { detailApp ->
        AppDetailModal(
            app = detailApp,
            onDismiss = { selectedAppDetail = null },
            onRunApp = {
                val genApp = GeneratedApp(
                    appName = detailApp.appName,
                    description = detailApp.description,
                    featuresConfigured = detailApp.features,
                    appCategory = if (detailApp.isGame) "QUIZ" else "GENERAL",
                    accentColorHex = detailApp.accentColorHex
                )
                selectedAppDetail = null
                onLaunchApp(genApp)
            },
            onAddComment = { newComment ->
                val updatedList = detailApp.comments + newComment
                val idx = storeApps.indexOfFirst { it.id == detailApp.id }
                if (idx != -1) {
                    storeApps[idx] = detailApp.copy(comments = updatedList)
                    selectedAppDetail = storeApps[idx]
                }
                Toast.makeText(context, "মতামত সফলভাবে যোগ হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Upload App Modal
    if (showUploadModal) {
        UploadAppModal(
            onDismiss = { showUploadModal = false },
            onUpload = { name, desc, category, isGame ->
                val newStoreApp = StoreApp(
                    appName = name,
                    packageName = "com.user.${name.replace("[^a-zA-Z0-9]".toRegex(), "").lowercase()}",
                    description = desc,
                    category = category,
                    authorName = "User Developer",
                    accentColorHex = 0xFF00E5FF,
                    iconEmoji = if (isGame) "🎮" else "📱",
                    isGame = isGame,
                    isVerifiedSafe = true,
                    features = listOf("ইউজার আপলোডেড", "রিয়েল সোর্স কোড", "কমিউনিটি অ্যাপ")
                )
                storeApps.add(0, newStoreApp)
                showUploadModal = false
                Toast.makeText(context, "🚀 '$name' সফলভাবে অ্যাপ স্টোরে প্রকাশিত হয়েছে!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Publishing Guidelines Modal
    if (showPublishGuidelinesDialog) {
        AlertDialog(
            onDismissRequest = { showPublishGuidelinesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = "Security", tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অনলাইন অ্যাপ প্রকাশের নিয়মাবলী", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("১. কোনো ক্ষতিকারক কোড বা স্পাইওয়্যার অন্তর্ভুক্ত করা যাবে না।", color = TextSilver, fontSize = 12.sp)
                    Text("২. প্রতিটি প্রকাশিত অ্যাপের সোর্স কোড যাচাইকৃত ও নিরাপদ হতে হবে।", color = TextSilver, fontSize = 12.sp)
                    Text("৩. কপিরাইট লঙ্ঘন বা কারো অনুমতি ছাড়া ফাইল আপলোড নিষিদ্ধ।", color = TextSilver, fontSize = 12.sp)
                    Text("৪. ডাউনলোড সংখ্যা ও রেটিং শুধুমাত্র প্রকৃত ব্যবহারকারীদের অ্যাকশন থেকে হিসেব হয়।", color = TextSilver, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPublishGuidelinesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                ) {
                    Text("বুঝেছি")
                }
            },
            containerColor = Color(0xFF091438)
        )
    }
}

/** Featured App Horizontal Card */
@Composable
private fun FeaturedAppCard(
    app: StoreApp,
    onOpenDetail: () -> Unit,
    onRun: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenDetail),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0C1945))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(app.iconEmoji, fontSize = 28.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x3300E676))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF00E676), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Verified", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(app.appName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(app.authorName, color = TextSilver, fontSize = 10.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(app.description, color = Color(0xFFA5B8DC), fontSize = 11.sp, maxLines = 2)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${app.rating}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onRun,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("⚡ Run", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Individual Store App Item Card */
@Composable
private fun StoreAppCard(
    app: StoreApp,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpenDetail: () -> Unit,
    onRunApp: () -> Unit,
    onDownloadZip: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenDetail),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF081438))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Icon
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x3300F0FF))
                        .border(1.dp, Color(app.accentColorHex.toULong()), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(app.iconEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(app.appName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (app.isGame) "🎮 Game" else "📱 App",
                            fontSize = 9.sp,
                            color = NeonPinkLight,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x33FF007F))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Text(app.description, color = TextSilver, fontSize = 11.sp, maxLines = 1)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("👤 ${app.authorName}", color = Color(0xFFA5B8DC), fontSize = 10.sp)
                        Text("★ ${app.rating}", color = Color(0xFFFFD700), fontSize = 10.sp)
                        Text("📥 ${app.downloadCount}", color = NeonCyan, fontSize = 10.sp)
                    }
                }

                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) NeonPink else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDownloadZip,
                    colors = ButtonDefaults.textButtonColors(contentColor = NeonCyan)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "ZIP", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("সোর্স ZIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = onRunApp,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("সরাসরি চালান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Full Detailed App & Reviews Modal */
@Composable
private fun AppDetailModal(
    app: StoreApp,
    onDismiss: () -> Unit,
    onRunApp: () -> Unit,
    onAddComment: (StoreComment) -> Unit
) {
    val context = LocalContext.current
    var reviewText by remember { mutableStateOf("") }
    var userRating by remember { mutableIntStateOf(5) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEE03081E))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF07123A))
                    .border(1.5.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPink)), RoundedCornerShape(22.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("অ্যাপ বিবরণ ও রিভিউ", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        val genApp = GeneratedApp(appName = app.appName, description = app.description, featuresConfigured = app.features)
                        AppConfigSharing.shareAppViaSystem(context, genApp)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // App Info Box
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x3300F0FF))
                            .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(app.iconEmoji, fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(app.appName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("ডেভেলপার: ${app.authorName}", color = TextSilver, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("★ ${app.rating} (${app.downloadCount} ডাউনলোড)", color = Color(0xFFFFD700), fontSize = 11.5.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ভার্সন ${app.version}", color = NeonCyan, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(app.description, color = Color(0xFFA5B8DC), fontSize = 12.5.sp)

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Run & Download Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onRunApp,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সরাসরি চালান", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val genApp = GeneratedApp(appName = app.appName, description = app.description, featuresConfigured = app.features)
                            ProjectZipGenerator.shareProjectZip(context, genApp)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072FF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সোর্স ZIP", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Review Input Box
                Text("আপনার রেটিং ও মতামত দিন:", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "$star Star",
                            tint = if (star <= userRating) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { userRating = star }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        placeholder = { Text("মতামত লিখুন...", color = Color(0xFFA5B8DC), fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (reviewText.isNotBlank()) {
                                onAddComment(
                                    StoreComment(authorName = "আপনি (User)", commentText = reviewText.trim(), rating = userRating)
                                )
                                reviewText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("পোস্ট")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Comments List
                Text("ব্যবহারকারীদের রিভিউ (${app.comments.size}):", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                app.comments.forEach { comment ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33101E4D))
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(comment.authorName, color = NeonCyan, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text("★ ${comment.rating}", color = Color(0xFFFFD700), fontSize = 11.sp)
                            }
                            Text(comment.commentText, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

/** Upload App Modal */
@Composable
private fun UploadAppModal(
    onDismiss: () -> Unit,
    onUpload: (name: String, desc: String, category: String, isGame: Boolean) -> Unit
) {
    var appName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isGame by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("🚀 নতুন অ্যাপ অনলাইনে প্রকাশ করুন", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("অ্যাপ বা গেমের নাম", color = Color(0xFFA5B8DC), fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("অ্যাপের বিবরণ", color = Color(0xFFA5B8DC), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("এটি একটি গেম?", color = Color.White, fontSize = 13.sp)
                    Button(
                        onClick = { isGame = !isGame },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isGame) NeonPink else Color(0x3300F0FF))
                    ) {
                        Text(if (isGame) "🎮 গেম" else "📱 অ্যাপ", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (appName.isNotBlank()) {
                        onUpload(appName.trim(), description.ifBlank { "ব্যবহারকারীর তৈরি অ্যাপ।" }, if (isGame) "Games" else "Apps", isGame)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
            ) {
                Text("পাবলিশ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = Color.Gray) }
        },
        containerColor = Color(0xFF091238)
    )
}
