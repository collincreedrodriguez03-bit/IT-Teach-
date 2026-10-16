package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

enum class QuestType(val displayName: String) {
    MAIN("Main Quest (Certifications)"),
    DAILY("Daily Challenge"),
    SIDE("Side Quest (Hands-on Lab)")
}

@IgnoreExtraProperties
data class Quest(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val questType: String = "MAIN", // MAIN, DAILY, SIDE
    val category: String = "CCNA", // CCNA, A_PLUS, SECURITY_PLUS, PYTHON, LINUX, NETWORKING
    val xpReward: Long = 100L,
    val status: String = "ACTIVE", // ACTIVE, COMPLETED, VERIFIED
    val outsideLearningRequirement: String = "",
    @get:PropertyName("isVerified") @set:PropertyName("isVerified") var isVerified: Boolean = false,
    @get:PropertyName("isMastered") @set:PropertyName("isMastered") var isMastered: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "title" to title,
        "description" to description,
        "questType" to questType,
        "category" to category,
        "xpReward" to xpReward,
        "status" to status,
        "outsideLearningRequirement" to outsideLearningRequirement,
        "isVerified" to isVerified,
        "isMastered" to isMastered
    )

    val isCompleted: Boolean
        get() = status == "COMPLETED" || status == "VERIFIED"
}

data class BadgeDefinition(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val requiredLevel: Long = 1L,
    val requiredXp: Long = 0L,
    val category: String = "General"
)
