package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ActivityLog
import com.example.data.model.DictionaryTerm
import com.example.data.model.JournalEntry
import com.example.data.model.PortfolioProject
import com.example.data.model.Quest
import com.example.data.model.UserProfile

@Entity(tableName = "dictionary_terms")
data class DictionaryTermEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val term: String,
    val command: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val isMastered: Boolean,
    val isAiGenerated: Boolean,
    val aiExplanation: String,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val updatedAtEpoch: Long = System.currentTimeMillis()
) {
    fun toModel(): DictionaryTerm = DictionaryTerm(
        id = id,
        userId = userId,
        term = term,
        command = command,
        description = description,
        category = category,
        difficulty = difficulty,
        isMastered = isMastered,
        isAiGenerated = isAiGenerated,
        aiExplanation = aiExplanation
    )

    companion object {
        fun fromModel(model: DictionaryTerm): DictionaryTermEntity = DictionaryTermEntity(
            id = model.id,
            userId = model.userId,
            term = model.term,
            command = model.command,
            description = model.description,
            category = model.category,
            difficulty = model.difficulty,
            isMastered = model.isMastered,
            isAiGenerated = model.isAiGenerated,
            aiExplanation = model.aiExplanation
        )
    }
}

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val notes: String,
    val solution: String,
    val tags: List<String>,
    val category: String,
    val isVerified: Boolean,
    val isMastered: Boolean,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val updatedAtEpoch: Long = System.currentTimeMillis()
) {
    fun toModel(): JournalEntry = JournalEntry(
        id = id,
        userId = userId,
        title = title,
        notes = notes,
        solution = solution,
        tags = tags,
        category = category,
        isVerified = isVerified,
        isMastered = isMastered
    )

    companion object {
        fun fromModel(model: JournalEntry): JournalEntryEntity = JournalEntryEntity(
            id = model.id,
            userId = model.userId,
            title = model.title,
            notes = model.notes,
            solution = model.solution,
            tags = model.tags,
            category = model.category,
            isVerified = model.isVerified,
            isMastered = model.isMastered
        )
    }
}

@Entity(tableName = "portfolio_projects")
data class PortfolioProjectEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val executiveSummary: String,
    val problemBusinessNeed: String,
    val toolsTechnologies: List<String>,
    val architectureSteps: String,
    val resultsMetrics: String,
    val verifiedEvidence: String,
    val lessonsLearned: String,
    val isVerified: Boolean,
    val isMastered: Boolean,
    val linkedJournalIds: List<String>,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val updatedAtEpoch: Long = System.currentTimeMillis()
) {
    fun toModel(): PortfolioProject = PortfolioProject(
        id = id,
        userId = userId,
        title = title,
        executiveSummary = executiveSummary,
        problemBusinessNeed = problemBusinessNeed,
        toolsTechnologies = toolsTechnologies,
        architectureSteps = architectureSteps,
        resultsMetrics = resultsMetrics,
        verifiedEvidence = verifiedEvidence,
        lessonsLearned = lessonsLearned,
        isVerified = isVerified,
        isMastered = isMastered,
        linkedJournalIds = linkedJournalIds
    )

    companion object {
        fun fromModel(model: PortfolioProject): PortfolioProjectEntity = PortfolioProjectEntity(
            id = model.id,
            userId = model.userId,
            title = model.title,
            executiveSummary = model.executiveSummary,
            problemBusinessNeed = model.problemBusinessNeed,
            toolsTechnologies = model.toolsTechnologies,
            architectureSteps = model.architectureSteps,
            resultsMetrics = model.resultsMetrics,
            verifiedEvidence = model.verifiedEvidence,
            lessonsLearned = model.lessonsLearned,
            isVerified = model.isVerified,
            isMastered = model.isMastered,
            linkedJournalIds = model.linkedJournalIds
        )
    }
}

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val questType: String,
    val category: String,
    val xpReward: Long,
    val status: String,
    val outsideLearningRequirement: String,
    val isVerified: Boolean,
    val isMastered: Boolean
) {
    fun toModel(): Quest = Quest(
        id = id,
        userId = userId,
        title = title,
        description = description,
        questType = questType,
        category = category,
        xpReward = xpReward,
        status = status,
        outsideLearningRequirement = outsideLearningRequirement,
        isVerified = isVerified,
        isMastered = isMastered
    )

    companion object {
        fun fromModel(model: Quest): QuestEntity = QuestEntity(
            id = model.id,
            userId = model.userId,
            title = model.title,
            description = model.description,
            questType = model.questType,
            category = model.category,
            xpReward = model.xpReward,
            status = model.status,
            outsideLearningRequirement = model.outsideLearningRequirement,
            isVerified = model.isVerified,
            isMastered = model.isMastered
        )
    }
}

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val displayName: String,
    val email: String,
    val xp: Long,
    val level: Long,
    val streakCount: Long,
    val lastActiveDate: String,
    val badges: List<String>,
    val pinHash: String,
    val failedPinAttempts: Long,
    val lockoutUntil: Long
) {
    fun toModel(): UserProfile = UserProfile(
        userId = userId,
        displayName = displayName,
        email = email,
        xp = xp,
        level = level,
        streakCount = streakCount,
        lastActiveDate = lastActiveDate,
        badges = badges,
        pinHash = pinHash,
        failedPinAttempts = failedPinAttempts,
        lockoutUntil = lockoutUntil
    )

    companion object {
        fun fromModel(model: UserProfile): UserProfileEntity = UserProfileEntity(
            userId = model.userId,
            displayName = model.displayName,
            email = model.email,
            xp = model.xp,
            level = model.level,
            streakCount = model.streakCount,
            lastActiveDate = model.lastActiveDate,
            badges = model.badges,
            pinHash = model.pinHash,
            failedPinAttempts = model.failedPinAttempts,
            lockoutUntil = model.lockoutUntil
        )
    }
}

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val title: String,
    val moduleType: String,
    val status: String,
    val gradePercent: Int?,
    val completedSteps: Int,
    val totalSteps: Int,
    val weakTopics: List<String>,
    val summary: String,
    val timestamp: Long
) {
    fun toModel(): ActivityLog = ActivityLog(
        id = id,
        title = title,
        moduleType = moduleType,
        status = status,
        gradePercent = gradePercent,
        completedSteps = completedSteps,
        totalSteps = totalSteps,
        weakTopics = weakTopics,
        summary = summary,
        timestamp = timestamp
    )

    companion object {
        fun fromModel(model: ActivityLog): ActivityLogEntity = ActivityLogEntity(
            id = model.id,
            title = model.title,
            moduleType = model.moduleType,
            status = model.status,
            gradePercent = model.gradePercent,
            completedSteps = model.completedSteps,
            totalSteps = model.totalSteps,
            weakTopics = model.weakTopics,
            summary = model.summary,
            timestamp = model.timestamp
        )
    }
}
