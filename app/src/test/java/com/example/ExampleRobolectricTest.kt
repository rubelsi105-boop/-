package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.RubelDatabase
import com.example.data.repository.AppMessageRepository
import com.example.model.AppEditMessage
import com.example.model.ChatMessage
import com.example.model.GeneratedApp
import com.example.model.MessageSender
import com.example.model.RubelTab
import com.example.util.AppConfigSharing
import com.example.viewmodel.RubelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private val testDispatcher = StandardTestDispatcher()
  private lateinit var database: RubelDatabase
  private lateinit var repository: AppMessageRepository
  private lateinit var application: Application

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    application = ApplicationProvider.getApplicationContext<Context>() as Application
    database = Room.inMemoryDatabaseBuilder(application, RubelDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = AppMessageRepository(database.appMessageDao())
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    database.close()
    Dispatchers.resetMain()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Rubel", appName)
  }

  @Test
  fun `verify RubelViewModel tabs and profile`() {
    val viewModel = RubelViewModel(application, repository)
    assertEquals(RubelTab.HOME, viewModel.currentTab.value)
    assertEquals("Rubel", viewModel.userProfile.value.name)

    viewModel.selectTab(RubelTab.APP_EDIT)
    assertEquals(RubelTab.APP_EDIT, viewModel.currentTab.value)

    viewModel.selectTab(RubelTab.PROFILE)
    assertEquals(RubelTab.PROFILE, viewModel.currentTab.value)
  }

  @Test
  fun `verify saving draft in RubelViewModel`() {
    val viewModel = RubelViewModel(application, repository)
    val initialCount = viewModel.savedDrafts.value.size
    viewModel.saveNewDraft("Test Prompt", "Test Content", "বাংলা")
    assertEquals(initialCount + 1, viewModel.savedDrafts.value.size)
  }

  @Test
  fun `verify Room database persistent storage for AppEdit messages`() = runTest {
    repository.seedInitialDataIfEmpty()

    val initialMessages = repository.allAppEditMessages.first()
    assertTrue(initialMessages.isNotEmpty())

    // Insert user request to build calculator app
    val userMsg = AppEditMessage(
      sender = MessageSender.USER,
      text = "একটি ক্যালকুলেটর অ্যাপ বানাও"
    )
    repository.insertAppEditMessage(userMsg)

    // Insert AI generated app response
    val aiApp = GeneratedApp(
      appName = "Rubel Cyber Calculator",
      description = "সায়েন্টিফিক ক্যালকুলেটর",
      featuresConfigured = listOf("স্ক্রিন ডিসপ্লে", "হিসাব ইঞ্জিন"),
      appCategory = "CALCULATOR"
    )
    val aiMsg = AppEditMessage(
      sender = MessageSender.RUBEL_AI,
      text = "ক্যালকুলেটর অ্যাপ সফলভাবে তৈরি হয়েছে!",
      generatedApp = aiApp
    )
    repository.insertAppEditMessage(aiMsg)

    // Verify messages are persistently saved in Room database
    val messagesAfterInsert = repository.allAppEditMessages.first()
    assertEquals(initialMessages.size + 2, messagesAfterInsert.size)

    val retrievedAiMsg = messagesAfterInsert.last()
    assertNotNull(retrievedAiMsg.generatedApp)
    assertEquals("CALCULATOR", retrievedAiMsg.generatedApp?.appCategory)
    assertEquals("Rubel Cyber Calculator", retrievedAiMsg.generatedApp?.appName)

    // Verify delete operation in Room database
    repository.deleteAppEditMessage(retrievedAiMsg.id)
    val messagesAfterDelete = repository.allAppEditMessages.first()
    assertEquals(initialMessages.size + 1, messagesAfterDelete.size)
  }

  @Test
  fun `verify Room database persistent storage for chat messages`() = runTest {
    repository.seedInitialDataIfEmpty()

    val initialChat = repository.allChatMessages.first()
    assertTrue(initialChat.isNotEmpty())

    val chatMsg = com.example.model.ChatMessage(
      sender = MessageSender.USER,
      text = "হ্যালো রুবল এআই"
    )
    repository.insertChatMessage(chatMsg)

    val updatedChat = repository.allChatMessages.first()
    assertEquals(initialChat.size + 1, updatedChat.size)
  }

  @Test
  fun `verify setup screen custom app builder workflow`() = runTest {
    repository.seedInitialDataIfEmpty()
    val viewModel = RubelViewModel(application, repository)
    viewModel.buildAppFromPrompt("নতুন অ্যাপ তৈরি করো: ফটো এডিটর প্রো (ইউজার: Rubel)", null)

    val messages = repository.allAppEditMessages.first { msgs ->
      msgs.any { it.generatedApp?.appName == "ফটো এডিটর প্রো" }
    }
    val createdAiMsg = messages.first { it.generatedApp?.appName == "ফটো এডিটর প্রো" }
    assertEquals("ফটো এডিটর প্রো", createdAiMsg.generatedApp?.appName)
  }

  @Test
  fun `verify deep link and shareable configuration code export and import`() {
    val sampleApp = GeneratedApp(
      id = "test-app-123",
      appName = "Cyber Weather Matrix",
      description = "Real-time atmospheric neon telemetry",
      featuresConfigured = listOf("Live Forecast", "Atmosphere Tracker", "Radar View"),
      appCategory = "WEATHER"
    )

    // 1. Generate shareable configuration code (token)
    val configCode = AppConfigSharing.generateConfigCode(sampleApp)
    assertTrue(configCode.startsWith("RUBEL-APP://"))

    // 2. Generate deep link
    val deepLink = AppConfigSharing.generateDeepLink(sampleApp)
    assertTrue(deepLink.startsWith("https://rubel.ai/app/open?spec="))

    // 3. Parse and import from configuration code
    val importedApp = AppConfigSharing.parseConfigCode(configCode)
    assertNotNull(importedApp)
    assertEquals(sampleApp.id, importedApp?.id)
    assertEquals(sampleApp.appName, importedApp?.appName)
    assertEquals(sampleApp.description, importedApp?.description)
    assertEquals(sampleApp.appCategory, importedApp?.appCategory)
    assertEquals(sampleApp.featuresConfigured, importedApp?.featuresConfigured)
  }

  @Test
  fun `verify app studio screen item model and publishing workflow`() = runTest {
    repository.seedInitialDataIfEmpty()
    val viewModel = RubelViewModel(application, repository)

    val studioItem = com.example.ui.screens.AppStudioItem(
      title = "Rubel Cyber Calculator",
      category = "CALCULATOR",
      defaultPrompt = "একটি পূর্ণাঙ্গ সাইবারনেটিক সায়েন্টিফিক ও স্ট্যান্ডার্ড ক্যালকুলেটর অ্যাপ।"
    )

    // Simulate publishing an app from App Studio
    viewModel.buildAppFromPrompt(studioItem.defaultPrompt, null)

    val messages = repository.allAppEditMessages.first { msgs ->
      msgs.any { it.generatedApp?.appCategory == "CALCULATOR" }
    }
    val generatedMsg = messages.first { it.generatedApp?.appCategory == "CALCULATOR" }
    assertNotNull(generatedMsg.generatedApp)
    assertEquals("CALCULATOR", generatedMsg.generatedApp?.appCategory)
  }

  @Test
  fun `verify local Room database stores recent interaction history for scrolling and deletion`() = runTest {
    repository.seedInitialDataIfEmpty()
    val viewModel = RubelViewModel(application, repository)

    val initialCount = repository.allChatMessages.first().size
    assertTrue(initialCount >= 1)

    // User interacts with AI
    val userMsg1 = ChatMessage(
      id = "chat_msg_1",
      sender = MessageSender.USER,
      text = "What is Kotlin StateFlow?"
    )
    val aiMsg1 = ChatMessage(
      id = "chat_msg_2",
      sender = MessageSender.RUBEL_AI,
      text = "StateFlow is a state-holder observable flow that emits the current and new state updates."
    )
    val userMsg2 = ChatMessage(
      id = "chat_msg_3",
      sender = MessageSender.USER,
      text = "How does Room Database persist history locally?"
    )
    val aiMsg2 = ChatMessage(
      id = "chat_msg_4",
      sender = MessageSender.RUBEL_AI,
      text = "Room provides an abstraction layer over SQLite to store interaction history persistently on-device."
    )

    repository.insertChatMessage(userMsg1)
    repository.insertChatMessage(aiMsg1)
    repository.insertChatMessage(userMsg2)
    repository.insertChatMessage(aiMsg2)

    // Verify all interactions are stored in Room and can be scrolled back through
    val allHistory = repository.allChatMessages.first()
    assertEquals(initialCount + 4, allHistory.size)
    assertTrue(allHistory.any { it.id == "chat_msg_1" && it.text == "What is Kotlin StateFlow?" })
    assertTrue(allHistory.any { it.id == "chat_msg_4" && it.text.contains("Room provides an abstraction layer") })

    // Test search functionality in conversation history
    val searchResults = repository.searchChatMessages("StateFlow").first()
    assertEquals(2, searchResults.size) // userMsg1 and aiMsg1 both mention StateFlow

    // Delete an individual interaction
    viewModel.deleteChatMessage("chat_msg_1")
    val updatedHistory = repository.allChatMessages.first()
    assertEquals(initialCount + 3, updatedHistory.size)
    assertFalse(updatedHistory.any { it.id == "chat_msg_1" })
  }

  @Test
  fun `verify Jetpack DataStore manages user settings persistently`() = runTest {
    val dataStore = com.example.data.datastore.UserPreferencesDataStore(application)

    // Initial default profile
    val initialProfile = dataStore.userProfileFlow.first()
    assertEquals("Rubel", initialProfile.name)
    assertEquals("Bilingual (বাংলা + English)", initialProfile.preferredLanguage)
    assertTrue(initialProfile.isHapticEnabled)

    // Update profile
    dataStore.updateProfile("Rubel Master", "Chief AI Architect", "বাংলা (Bengali)")
    dataStore.updatePinkGlow(0.95f)
    dataStore.setHaptic(false)

    val updatedProfile = dataStore.userProfileFlow.first()
    assertEquals("Rubel Master", updatedProfile.name)
    assertEquals("Chief AI Architect", updatedProfile.roleTitle)
    assertEquals("বাংলা (Bengali)", updatedProfile.preferredLanguage)
    assertEquals(0.95f, updatedProfile.pinkGlowIntensity, 0.01f)
    assertFalse(updatedProfile.isHapticEnabled)
  }

  @Test
  fun `verify Jetpack DataStore manages app theme preference and design settings`() = runTest {
    val dataStore = com.example.data.datastore.UserPreferencesDataStore(application)

    val initialSettings = dataStore.designSettingsFlow.first()
    assertTrue(initialSettings.isDarkTheme)
    assertEquals(22, initialSettings.cornerRadiusDp)

    // Change theme preference and design settings
    val customSettings = com.example.model.DesignSettings(
      primaryColorHex = 0xFFFF007F,
      buttonColorHex = 0xFFFF007F,
      neonBorderColorHex = 0xFF00E5FF,
      cornerRadiusDp = 28,
      isDarkTheme = false,
      fontScale = 1.15f
    )
    dataStore.updateDesignSettings(customSettings)

    val savedSettings = dataStore.designSettingsFlow.first()
    assertFalse(savedSettings.isDarkTheme)
    assertEquals(28, savedSettings.cornerRadiusDp)
    assertEquals(0xFFFF007FL, savedSettings.primaryColorHex)
    assertEquals(1.15f, savedSettings.fontScale, 0.01f)

    // Toggle theme back
    dataStore.updateTheme(true)
    val restoredThemeSettings = dataStore.designSettingsFlow.first()
    assertTrue(restoredThemeSettings.isDarkTheme)
  }

  @Test
  fun `verify Jetpack DataStore manages simple persistent configuration state`() = runTest {
    val dataStore = com.example.data.datastore.UserPreferencesDataStore(application)

    val initialConfig = dataStore.appConfigFlow.first()
    assertEquals("HOME", initialConfig.lastActiveTab)
    assertTrue(initialConfig.isTtsAudioEnabled)

    dataStore.updateLastActiveTab("APP_EDIT")
    dataStore.setTtsAudioEnabled(false)

    val updatedConfig = dataStore.appConfigFlow.first()
    assertEquals("APP_EDIT", updatedConfig.lastActiveTab)
    assertFalse(updatedConfig.isTtsAudioEnabled)
  }
}
