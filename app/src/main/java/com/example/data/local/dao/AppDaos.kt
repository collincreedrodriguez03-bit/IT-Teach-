package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ActivityLogEntity
import com.example.data.local.entity.DictionaryTermEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.PortfolioProjectEntity
import com.example.data.local.entity.QuestEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM dictionary_terms ORDER BY term ASC")
    fun getAllTerms(): Flow<List<DictionaryTermEntity>>

    @Query("SELECT * FROM dictionary_terms WHERE id = :id LIMIT 1")
    suspend fun getTermById(id: String): DictionaryTermEntity?

    @Query("SELECT COUNT(*) FROM dictionary_terms")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(term: DictionaryTermEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(terms: List<DictionaryTermEntity>)

    @Update
    suspend fun update(term: DictionaryTermEntity)

    @Query("DELETE FROM dictionary_terms WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAtEpoch DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: String): JournalEntryEntity?

    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: JournalEntryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<JournalEntryEntity>)

    @Update
    suspend fun update(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_projects ORDER BY createdAtEpoch DESC")
    fun getAllProjects(): Flow<List<PortfolioProjectEntity>>

    @Query("SELECT * FROM portfolio_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): PortfolioProjectEntity?

    @Query("SELECT COUNT(*) FROM portfolio_projects")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(project: PortfolioProjectEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(projects: List<PortfolioProjectEntity>)

    @Update
    suspend fun update(project: PortfolioProjectEntity)

    @Query("DELETE FROM portfolio_projects WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests")
    fun getAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT COUNT(*) FROM quests")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(quest: QuestEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(quests: List<QuestEntity>)

    @Update
    suspend fun update(quest: QuestEntity)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun getUserProfile(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getFirstProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileSync(userId: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ActivityLogEntity)

    @Query("DELETE FROM activity_logs WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM activity_logs WHERE status != 'Completed' ORDER BY timestamp DESC LIMIT 5")
    suspend fun getIncompleteActivities(): List<ActivityLogEntity>
}
