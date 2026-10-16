package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "IT Engineer",
    val email: String = "",
    val xp: Long = 0L,
    val level: Long = 1L,
    val streakCount: Long = 1L,
    val lastActiveDate: String = "",
    val badges: List<String> = emptyList(),
    val pinHash: String = "",
    val failedPinAttempts: Long = 0L,
    val lockoutUntil: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "displayName" to displayName,
        "email" to email,
        "xp" to xp,
        "level" to level,
        "streakCount" to streakCount,
        "lastActiveDate" to lastActiveDate,
        "badges" to badges,
        "pinHash" to pinHash,
        "failedPinAttempts" to failedPinAttempts,
        "lockoutUntil" to lockoutUntil
    )

    val rankTitle: String
        get() = when {
            level >= 7 -> "Principal NetDevOps Architect"
            level >= 6 -> "Infrastructure Engineer"
            level >= 5 -> "Security Analyst"
            level >= 4 -> "Network Specialist"
            level >= 3 -> "Junior Sysadmin"
            level >= 2 -> "Helpdesk Operator"
            else -> "Script Apprentice"
        }

    val xpForNextLevel: Long
        get() = when (level) {
            1L -> 250L
            2L -> 600L
            3L -> 1200L
            4L -> 2000L
            5L -> 3200L
            6L -> 5000L
            else -> 10000L
        }

    val xpCurrentLevelBase: Long
        get() = when (level) {
            1L -> 0L
            2L -> 250L
            3L -> 600L
            4L -> 1200L
            5L -> 2000L
            6L -> 3200L
            else -> 5000L
        }

    val progressFraction: Float
        get() {
            val base = xpCurrentLevelBase
            val target = xpForNextLevel
            if (target <= base) return 1f
            return ((xp - base).toFloat() / (target - base).toFloat()).coerceIn(0f, 1f)
        }
}
