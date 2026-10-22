package com.example.data.repository

import com.example.data.local.dao.AppMessageDao
import com.example.data.local.entity.AppMessageEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.model.AppEditMessage
import com.example.model.ChatMessage
import com.example.model.GeneratedApp
import com.example.model.MessageSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppMessageRepository(private val dao: AppMessageDao) {

    val allAppEditMessages: Flow<List<AppEditMessage>> = dao.getAllAppMessages().map { entities ->
        entities.map { entity ->
            val generatedApp = if (entity.appName != null) {
                GeneratedApp(
                    id = entity.id,
                    appName = entity.appName,
                    description = entity.appDescription ?: "",
                    featuresConfigured = entity.appFeaturesJson?.split(";;")?.filter { it.isNotBlank() } ?: emptyList(),
                    appCategory = entity.appCategory ?: "GENERAL",
                    timestamp = entity.timestamp
                )
            } else null

            AppEditMessage(
                id = entity.id,
                sender = if (entity.sender == "USER") MessageSender.USER else MessageSender.RUBEL_AI,
                text = entity.text,
                imageUri = entity.imageUri,
                generatedApp = generatedApp,
                timestamp = entity.timestamp
            )
        }
    }

    val allChatMessages: Flow<List<ChatMessage>> = dao.getAllChatMessages().map { entities ->
        entities.map { entity ->
            ChatMessage(
                id = entity.id,
                sender = if (entity.sender == "USER") MessageSender.USER else MessageSender.RUBEL_AI,
                text = entity.text,
                timestamp = entity.timestamp
            )
        }
    }

    suspend fun insertAppEditMessage(message: AppEditMessage) {
        val app = message.generatedApp
        val entity = AppMessageEntity(
            id = message.id,
            sender = message.sender.name,
            text = message.text,
            imageUri = message.imageUri,
            appName = app?.appName,
            appDescription = app?.description,
            appFeaturesJson = app?.featuresConfigured?.joinToString(";;"),
            appCategory = app?.appCategory,
            timestamp = message.timestamp
        )
        dao.insertAppMessage(entity)
    }

    suspend fun deleteAppEditMessage(id: String) {
        dao.deleteAppMessageById(id)
    }

    suspend fun clearAllAppEditMessages() {
        dao.clearAllAppMessages()
    }

    suspend fun insertChatMessage(message: ChatMessage) {
        val entity = ChatMessageEntity(
            id = message.id,
            sender = message.sender.name,
            text = message.text,
            timestamp = message.timestamp
        )
        dao.insertChatMessage(entity)
    }

    suspend fun deleteChatMessage(id: String) {
        dao.deleteChatMessageById(id)
    }

    fun searchChatMessages(query: String): Flow<List<ChatMessage>> = dao.searchChatMessages(query).map { entities ->
        entities.map { entity ->
            ChatMessage(
                id = entity.id,
                sender = if (entity.sender == "USER") MessageSender.USER else MessageSender.RUBEL_AI,
                text = entity.text,
                timestamp = entity.timestamp
            )
        }
    }

    suspend fun clearAllChatMessages() {
        dao.clearAllChatMessages()
    }

    suspend fun seedInitialDataIfEmpty() {
        if (dao.getAppMessageCount() == 0) {
            val initialMessage = AppEditMessage(
                sender = MessageSender.RUBEL_AI,
                text = "স্বাগতম Rubel App Studio-তে! 🎉\n\nআপনি যে ধরনের অ্যাপ চান (যেমন: ক্যালকুলেটর, টুডু লিস্ট, কুইজ অ্যাপ, আবহাওয়া, কাউন্টার বা যেকোনো কাস্টম অ্যাপ), নিচে লিখে '⚡ Run' বাটনে চাপ দিন। আমি সরাসরি অ্যাপটি বানিয়ে দেব এবং কী কী ফিচার সেট করেছি বিস্তারিত লিখে দেব।\n\nপ্লাস (+) বাটনে চাপ দিয়ে ছবি বা পিকচার আপলোড করে সেটির ওপর ভিত্তি করেও অ্যাপ তৈরি করতে পারেন! প্রতিটি বার্তা Room ডাটাবেজে স্থায়ীভাবে সেভ থাকবে এবং মাইক বাটনে চাপ দিলে আমি তা বাংলায় পড়ে শোনাব।"
            )
            insertAppEditMessage(initialMessage)
        }

        if (dao.getChatMessageCount() == 0) {
            val initialChatMessage = ChatMessage(
                sender = MessageSender.RUBEL_AI,
                text = "হ্যালো! আমি Rubel AI — আপনার স্মার্ট পার্সোনাল অ্যাসিস্ট্যান্ট।\n\nআপনি আমাকে যেকোনো প্রশ্ন করতে পারেন, বাংলায় বা ইংরেজিতে কোনো বিষয় জানতে চাইতে পারেন, কোড তৈরি করতে পারেন কিংবা নিচের পিঙ্ক গ্লোয়িং টুলস ও প্রম্পট ব্যবহার করতে পারেন!"
            )
            insertChatMessage(initialChatMessage)
        }
    }
}
