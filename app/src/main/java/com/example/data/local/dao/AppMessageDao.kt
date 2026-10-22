package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AppMessageEntity
import com.example.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppMessageDao {

    @Query("SELECT * FROM app_messages ORDER BY timestamp ASC")
    fun getAllAppMessages(): Flow<List<AppMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppMessage(message: AppMessageEntity)

    @Query("DELETE FROM app_messages WHERE id = :id")
    suspend fun deleteAppMessageById(id: String)

    @Query("DELETE FROM app_messages")
    suspend fun clearAllAppMessages()

    @Query("SELECT COUNT(*) FROM app_messages")
    suspend fun getAppMessageCount(): Int

    // Chat messages (for Home screen local history)
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE text LIKE '%' || :query || '%' ORDER BY timestamp ASC")
    fun searchChatMessages(query: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteChatMessageById(id: String)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllChatMessages()

    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun getChatMessageCount(): Int
}
