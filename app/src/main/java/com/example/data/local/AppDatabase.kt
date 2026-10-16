package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.ActivityLogDao
import com.example.data.local.dao.DictionaryDao
import com.example.data.local.dao.JournalDao
import com.example.data.local.dao.PortfolioDao
import com.example.data.local.dao.QuestDao
import com.example.data.local.dao.UserProfileDao
import com.example.data.local.entity.ActivityLogEntity
import com.example.data.local.entity.DictionaryTermEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.PortfolioProjectEntity
import com.example.data.local.entity.QuestEntity
import com.example.data.local.entity.UserProfileEntity

@Database(
    entities = [
        DictionaryTermEntity::class,
        JournalEntryEntity::class,
        PortfolioProjectEntity::class,
        QuestEntity::class,
        UserProfileEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dictionaryDao(): DictionaryDao
    abstract fun journalDao(): JournalDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun questDao(): QuestDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "netquest_local.db"
                )
                    // Explicitly non-destructive; preserve existing data
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
