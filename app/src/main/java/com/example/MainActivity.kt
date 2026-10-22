package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.GeneratedApp
import com.example.model.RubelTab
import com.example.ui.components.InteractiveAppViewer
import com.example.ui.components.NeonBottomNavigationBar
import com.example.ui.components.NeonWavesBackground
import com.example.ui.components.ShareAppDialog
import com.example.ui.screens.AppEditScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.RubelViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RubelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val designSettings by viewModel.designSettings.collectAsState()
            MyApplicationTheme(darkTheme = designSettings.isDarkTheme) {
                RubelApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RubelApp(viewModel: RubelViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val selectedFilterChip by viewModel.selectedFilterChip.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val designSettings by viewModel.designSettings.collectAsState()
    val savedDrafts by viewModel.savedDrafts.collectAsState()
    val appEditMessages by viewModel.appEditMessages.collectAsState()
    val isAppBuilding by viewModel.isAppBuilding.collectAsState()
    val currentlySpeakingId by viewModel.currentlySpeakingId.collectAsState()
    var showAppStudioFromHome by remember { mutableStateOf(false) }

    var activeViewerApp by remember { mutableStateOf<GeneratedApp?>(null) }
    var appToShare by remember { mutableStateOf<GeneratedApp?>(null) }

    if (showAppStudioFromHome) {
        com.example.ui.screens.AppStudioListScreen(
            onClose = { showAppStudioFromHome = false },
            onPublishApp = { appName, prompt, imageUri ->
                showAppStudioFromHome = false
                viewModel.selectTab(RubelTab.APP_EDIT)
                val finalPrompt = if (prompt.isNotBlank()) prompt else "নতুন অ্যাপ তৈরি করো: $appName"
                viewModel.buildAppFromPrompt(finalPrompt, imageUri?.toString())
            }
        )
        return
    }

    // Active full-screen interactive app viewer (Direct Live Test & AI Fixer)
    activeViewerApp?.let { app ->
        InteractiveAppViewer(
            app = app,
            onDismiss = { activeViewerApp = null },
            onShareApp = { appToShare = it },
            onFixApp = { targetApp, issueDescription ->
                viewModel.fixOrUpdateApp(targetApp, issueDescription) { updatedApp ->
                    activeViewerApp = updatedApp
                }
            }
        )
    }

    // Share Dialog for APK/ZIP Source Code and deep links
    appToShare?.let { app ->
        ShareAppDialog(
            app = app,
            onDismiss = { appToShare = null }
        )
    }

    // Handle back button to return to Home tab if on sub-screens
    BackHandler(enabled = currentTab != RubelTab.HOME) {
        viewModel.selectTab(RubelTab.HOME)
    }

    NeonWavesBackground(
        modifier = Modifier.fillMaxSize(),
        pinkIntensity = userProfile.pinkGlowIntensity
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NeonBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        viewModel.selectTab(tab)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                Crossfade(
                    targetState = currentTab,
                    animationSpec = tween(durationMillis = 280),
                    label = "tab_crossfade"
                ) { tab ->
                    when (tab) {
                        RubelTab.HOME -> {
                            HomeScreen(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                                chatMessages = chatMessages,
                                isGenerating = isGenerating,
                                quickPrompts = viewModel.quickPrompts,
                                smartTools = viewModel.smartTools,
                                selectedFilterChip = selectedFilterChip,
                                onFilterChipSelect = { viewModel.selectFilterChip(it) },
                                onSendMessage = { viewModel.sendUserMessage(it) },
                                onOpenProfile = { viewModel.selectTab(RubelTab.PROFILE) },
                                currentlySpeakingId = currentlySpeakingId,
                                onSpeakMessage = { id, text, context ->
                                    viewModel.speakMessage(id, text, context)
                                },
                                onStopSpeaking = {
                                    viewModel.stopSpeaking()
                                },
                                onClearChat = {
                                    viewModel.clearChatHistory()
                                },
                                onOpenAppStudio = {
                                    showAppStudioFromHome = true
                                },
                                onDeleteChatMessage = { id ->
                                    viewModel.deleteChatMessage(id)
                                },
                                onLaunchGeneratedApp = { app ->
                                    activeViewerApp = app
                                },
                                onNavigateToCreateApp = {
                                    viewModel.selectTab(RubelTab.APP_EDIT)
                                }
                            )
                        }

                        RubelTab.APP_EDIT -> {
                            AppEditScreen(
                                appEditMessages = appEditMessages,
                                isAppBuilding = isAppBuilding,
                                currentlySpeakingId = currentlySpeakingId,
                                onBuildApp = { prompt, imageUri ->
                                    viewModel.buildAppFromPrompt(prompt, imageUri)
                                },
                                onFixApp = { targetApp, issueDescription, onComplete ->
                                    viewModel.fixOrUpdateApp(targetApp, issueDescription, onComplete)
                                },
                                onDeleteMessage = { id ->
                                    viewModel.deleteAppEditMessage(id)
                                },
                                onSpeakMessage = { id, text, context ->
                                    viewModel.speakMessage(id, text, context)
                                },
                                onStopSpeaking = {
                                    viewModel.stopSpeaking()
                                },
                                pinkGlowIntensity = userProfile.pinkGlowIntensity,
                                onUpdatePinkGlow = { intensity ->
                                    viewModel.updatePinkGlow(intensity)
                                },
                                designSettings = designSettings,
                                onSaveDesignSettings = { updated ->
                                    viewModel.updateDesignSettings(updated)
                                }
                            )
                        }

                        RubelTab.PROFILE -> {
                            ProfileScreen(
                                userProfile = userProfile,
                                designSettings = designSettings,
                                onUpdateProfile = { name, role, lang ->
                                    viewModel.updateProfile(name, role, lang)
                                },
                                onToggleHaptic = {
                                    viewModel.toggleHaptic()
                                },
                                onToggleAutoSuggest = {
                                    viewModel.setAutoSuggest(!userProfile.isAutoSuggestEnabled)
                                },
                                onToggleTheme = { isDark ->
                                    viewModel.updateTheme(isDark)
                                },
                                onResetSettings = {
                                    viewModel.resetPreferencesToDefault()
                                },
                                onClearChat = {
                                    viewModel.clearChatHistory()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
