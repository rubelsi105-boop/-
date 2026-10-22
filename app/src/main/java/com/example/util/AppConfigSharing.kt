package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.example.model.GeneratedApp
import org.json.JSONArray
import org.json.JSONObject

/**
 * Utility for exporting, deep linking, sharing, and importing custom Rubel AI app configurations.
 */
object AppConfigSharing {

    fun generateConfigCode(app: GeneratedApp): String {
        val json = JSONObject().apply {
            put("v", "1.0")
            put("id", app.id)
            put("name", app.appName)
            put("desc", app.description)
            put("cat", app.appCategory)
            put("features", JSONArray(app.featuresConfigured))
            put("created", app.timestamp)
        }
        val encoded = Base64.encodeToString(json.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return "RUBEL-APP://$encoded"
    }

    fun generateDeepLink(app: GeneratedApp): String {
        val code = generateConfigCode(app)
        return "https://rubel.ai/app/open?spec=${Uri.encode(code)}"
    }

    fun parseConfigCode(code: String): GeneratedApp? {
        return try {
            val cleanCode = code.trim().removePrefix("RUBEL-APP://").trim()
            val decodedBytes = Base64.decode(cleanCode, Base64.DEFAULT)
            val json = JSONObject(String(decodedBytes, Charsets.UTF_8))
            val featuresList = mutableListOf<String>()
            val featArray = json.optJSONArray("features")
            if (featArray != null) {
                for (i in 0 until featArray.length()) {
                    featuresList.add(featArray.getString(i))
                }
            }
            GeneratedApp(
                id = json.optString("id", java.util.UUID.randomUUID().toString()),
                appName = json.optString("name", "Shared App"),
                description = json.optString("desc", "Imported Cyber App"),
                featuresConfigured = featuresList,
                appCategory = json.optString("cat", "GENERAL"),
                timestamp = json.optLong("created", System.currentTimeMillis())
            )
        } catch (e: Exception) {
            null
        }
    }

    fun shareAppViaSystem(context: Context, app: GeneratedApp) {
        val deepLink = generateDeepLink(app)
        val configCode = generateConfigCode(app)
        val shareText = buildString {
            append("🚀 Check out my custom app '${app.appName}' on Rubel AI!\n\n")
            append("📱 Description: ${app.description}\n")
            append("⚙️ Features: ${app.featuresConfigured.joinToString(", ")}\n\n")
            append("🔗 Deep Link: $deepLink\n\n")
            append("🔑 App Config Code:\n$configCode")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Rubel App Design: ${app.appName}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        val chooser = Intent.createChooser(sendIntent, "Share '${app.appName}' with")
        context.startActivity(chooser)
    }
}
