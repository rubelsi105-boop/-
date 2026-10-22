package com.example.model

enum class RubelTab(val label: String) {
    HOME("Home"),
    APP_EDIT("App Edit"),
    PROFILE("Profile")
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    RUBEL_AI
}

data class GeneratedApp(
    val id: String = java.util.UUID.randomUUID().toString(),
    val appName: String,
    val description: String,
    val featuresConfigured: List<String>,
    val appCategory: String = "GENERAL",
    val accentColorHex: Long = 0xFFFF2E93,
    val codePreview: String = "",
    val isGame: Boolean = false,
    val isPublished: Boolean = false,
    val version: String = "1.0.0",
    val authorName: String = "Rubel Developer",
    val downloadCount: Int = 0,
    val rating: Float = 5.0f,
    val apkSizeMb: String = "14.2 MB",
    val timestamp: Long = System.currentTimeMillis()
)

data class AppEditMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val imageUri: String? = null,
    val generatedApp: GeneratedApp? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class QuickPrompt(
    val id: String,
    val title: String,
    val prompt: String,
    val category: String,
    val iconEmoji: String
)

data class SmartTool(
    val id: String,
    val title: String,
    val titleBn: String,
    val description: String,
    val promptTemplate: String,
    val iconName: String,
    val glowColorHex: Long
)

data class SavedDraft(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val tag: String = "Prompt",
    val dateString: String = "Just now"
)

data class UserProfile(
    val name: String = "Rubel",
    val roleTitle: String = "Cyber Architect & AI Explorer",
    val email: String = "rubel.ai@cyberhub.net",
    val totalQueries: Int = 148,
    val savedDraftsCount: Int = 12,
    val preferredLanguage: String = "Bilingual (বাংলা + English)",
    val pinkGlowIntensity: Float = 0.85f,
    val isHapticEnabled: Boolean = true,
    val isAutoSuggestEnabled: Boolean = true
)

data class StoreComment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val authorName: String,
    val commentText: String,
    val rating: Int = 5,
    val date: String = "Today"
)

data class StoreApp(
    val id: String = java.util.UUID.randomUUID().toString(),
    val appName: String,
    val packageName: String,
    val description: String,
    val category: String,
    val authorName: String,
    val rating: Float = 4.9f,
    val downloadCount: Int = 1250,
    val version: String = "1.0.2",
    val updatedDate: String = "Oct 2026",
    val accentColorHex: Long = 0xFF00E5FF,
    val iconEmoji: String = "🚀",
    val isVerifiedSafe: Boolean = true,
    val isFeatured: Boolean = false,
    val isGame: Boolean = false,
    val apkSizeMb: String = "15.4 MB",
    val features: List<String> = emptyList(),
    val comments: List<StoreComment> = emptyList()
)

data class DesignSettings(
    val primaryColorHex: Long = 0xFF00E5FF,
    val buttonColorHex: Long = 0xFFFF007F,
    val textColorHex: Long = 0xFFFFFFFF,
    val backgroundColorHex: Long = 0xFF030820,
    val neonBorderColorHex: Long = 0xFF00E5FF,
    val cornerRadiusDp: Int = 22,
    val isDarkTheme: Boolean = true,
    val fontScale: Float = 1.0f
)

data class ProjectFile(
    val path: String,
    val name: String,
    val content: String,
    val extension: String
)
