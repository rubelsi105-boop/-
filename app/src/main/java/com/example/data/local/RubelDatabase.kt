package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AppMessageDao
import com.example.data.local.entity.AppMessageEntity
import com.example.data.local.entity.ChatMessageEntity

@Database(
    entities = [AppMessageEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RubelDatabase : RoomDatabase() {

    abstract fun appMessageDao(): AppMessageDao

    companion object {
        @Volatile
        private var INSTANCE: RubelDatabase? = null

        fun getDatabase(context: Context): RubelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RubelDatabase::class.java,
                    "rubel_cyber_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
