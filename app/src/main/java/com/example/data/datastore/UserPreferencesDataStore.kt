package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.DesignSettings
import com.example.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rubel_user_preferences")

data class AppConfigState(
    val lastActiveTab: String = "HOME",
    val isTtsAudioEnabled: Boolean = true,
    val aiStudioMode: String = "ADVANCED",
    val isFirstLaunchCompleted: Boolean = true
)

class UserPreferencesDataStore(private val context: Context) {

    private object PreferencesKeys {
        // User Profile
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val TOTAL_QUERIES = intPreferencesKey("total_queries")
        val SAVED_DRAFTS_COUNT = intPreferencesKey("saved_drafts_count")
        val PINK_GLOW_INTENSITY = floatPreferencesKey("pink_glow_intensity")
        val IS_HAPTIC_ENABLED = booleanPreferencesKey("is_haptic_enabled")
        val IS_AUTO_SUGGEST_ENABLED = booleanPreferencesKey("is_auto_suggest_enabled")

        // Theme and Design Settings
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val PRIMARY_COLOR_HEX = longPreferencesKey("primary_color_hex")
        val BUTTON_COLOR_HEX = longPreferencesKey("button_color_hex")
        val TEXT_COLOR_HEX = longPreferencesKey("text_color_hex")
        val BACKGROUND_COLOR_HEX = longPreferencesKey("background_color_hex")
        val NEON_BORDER_COLOR_HEX = longPreferencesKey("neon_border_color_hex")
        val CORNER_RADIUS_DP = intPreferencesKey("corner_radius_dp")
        val FONT_SCALE = floatPreferencesKey("font_scale")

        // Persistent App Configuration
        val LAST_ACTIVE_TAB = stringPreferencesKey("last_active_tab")
        val IS_TTS_AUDIO_ENABLED = booleanPreferencesKey("is_tts_audio_enabled")
        val AI_STUDIO_MODE = stringPreferencesKey("ai_studio_mode")
        val IS_FIRST_LAUNCH_COMPLETED = booleanPreferencesKey("is_first_launch_completed")
    }

    /**
     * Flow of UserProfile observed reactively across the entire application.
     */
    val userProfileFlow: Flow<UserProfile> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserProfile(
                name = preferences[PreferencesKeys.USER_NAME] ?: "Rubel",
                roleTitle = preferences[PreferencesKeys.USER_ROLE] ?: "Cyber Architect & AI Explorer",
                email = preferences[PreferencesKeys.USER_EMAIL] ?: "rubel.ai@cyberhub.net",
                totalQueries = preferences[PreferencesKeys.TOTAL_QUERIES] ?: 148,
                savedDraftsCount = preferences[PreferencesKeys.SAVED_DRAFTS_COUNT] ?: 12,
                preferredLanguage = preferences[PreferencesKeys.PREFERRED_LANGUAGE] ?: "Bilingual (বাংলা + English)",
                pinkGlowIntensity = preferences[PreferencesKeys.PINK_GLOW_INTENSITY] ?: 0.85f,
                isHapticEnabled = preferences[PreferencesKeys.IS_HAPTIC_ENABLED] ?: true,
                isAutoSuggestEnabled = preferences[PreferencesKeys.IS_AUTO_SUGGEST_ENABLED] ?: true
            )
        }

    /**
     * Flow of DesignSettings for user-customized colors, themes, and corner radiuses.
     */
    val designSettingsFlow: Flow<DesignSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            DesignSettings(
                primaryColorHex = preferences[PreferencesKeys.PRIMARY_COLOR_HEX] ?: 0xFF00E5FF,
                buttonColorHex = preferences[PreferencesKeys.BUTTON_COLOR_HEX] ?: 0xFFFF007F,
                textColorHex = preferences[PreferencesKeys.TEXT_COLOR_HEX] ?: 0xFFFFFFFF,
                backgroundColorHex = preferences[PreferencesKeys.BACKGROUND_COLOR_HEX] ?: 0xFF030820,
                neonBorderColorHex = preferences[PreferencesKeys.NEON_BORDER_COLOR_HEX] ?: 0xFF00E5FF,
                cornerRadiusDp = preferences[PreferencesKeys.CORNER_RADIUS_DP] ?: 22,
                isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: true,
                fontScale = preferences[PreferencesKeys.FONT_SCALE] ?: 1.0f
            )
        }

    /**
     * Flow of persistent application runtime state.
     */
    val appConfigFlow: Flow<AppConfigState> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppConfigState(
                lastActiveTab = preferences[PreferencesKeys.LAST_ACTIVE_TAB] ?: "HOME",
                isTtsAudioEnabled = preferences[PreferencesKeys.IS_TTS_AUDIO_ENABLED] ?: true,
                aiStudioMode = preferences[PreferencesKeys.AI_STUDIO_MODE] ?: "ADVANCED",
                isFirstLaunchCompleted = preferences[PreferencesKeys.IS_FIRST_LAUNCH_COMPLETED] ?: true
            )
        }

    suspend fun updateProfile(name: String, role: String, language: String) {
        context.dataStore.edit { preferences ->
            if (name.isNotBlank()) preferences[PreferencesKeys.USER_NAME] = name.trim()
            if (role.isNotBlank()) preferences[PreferencesKeys.USER_ROLE] = role.trim()
            if (language.isNotBlank()) preferences[PreferencesKeys.PREFERRED_LANGUAGE] = language.trim()
        }
    }

    suspend fun updatePinkGlow(intensity: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PINK_GLOW_INTENSITY] = intensity.coerceIn(0.1f, 1.0f)
        }
    }

    suspend fun toggleHaptic() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.IS_HAPTIC_ENABLED] ?: true
            preferences[PreferencesKeys.IS_HAPTIC_ENABLED] = !current
        }
    }

    suspend fun setHaptic(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun setAutoSuggest(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_AUTO_SUGGEST_ENABLED] = enabled
        }
    }

    suspend fun incrementTotalQueries() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.TOTAL_QUERIES] ?: 148
            preferences[PreferencesKeys.TOTAL_QUERIES] = current + 1
        }
    }

    suspend fun updateSavedDraftsCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SAVED_DRAFTS_COUNT] = maxOf(0, count)
        }
    }

    suspend fun updateTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
        }
    }

    suspend fun updateDesignSettings(settings: DesignSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIMARY_COLOR_HEX] = settings.primaryColorHex
            preferences[PreferencesKeys.BUTTON_COLOR_HEX] = settings.buttonColorHex
            preferences[PreferencesKeys.TEXT_COLOR_HEX] = settings.textColorHex
            preferences[PreferencesKeys.BACKGROUND_COLOR_HEX] = settings.backgroundColorHex
            preferences[PreferencesKeys.NEON_BORDER_COLOR_HEX] = settings.neonBorderColorHex
            preferences[PreferencesKeys.CORNER_RADIUS_DP] = settings.cornerRadiusDp
            preferences[PreferencesKeys.IS_DARK_THEME] = settings.isDarkTheme
            preferences[PreferencesKeys.FONT_SCALE] = settings.fontScale
        }
    }

    suspend fun updateLastActiveTab(tabName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_ACTIVE_TAB] = tabName
        }
    }

    suspend fun setTtsAudioEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_TTS_AUDIO_ENABLED] = enabled
        }
    }

    suspend fun resetAllSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
