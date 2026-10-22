package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_messages")
data class AppMessageEntity(
    @PrimaryKey
    val id: String,
    val sender: String,
    val text: String,
    val imageUri: String? = null,
    val appName: String? = null,
    val appDescription: String? = null,
    val appFeaturesJson: String? = null,
    val appCategory: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val sender: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
