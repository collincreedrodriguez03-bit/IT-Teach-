package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class PortfolioProject(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val executiveSummary: String = "",
    val problemBusinessNeed: String = "",
    val toolsTechnologies: List<String> = emptyList(),
    val architectureSteps: String = "",
    val resultsMetrics: String = "",
    val verifiedEvidence: String = "",
    val lessonsLearned: String = "",
    @get:PropertyName("isVerified") @set:PropertyName("isVerified") var isVerified: Boolean = false,
    @get:PropertyName("isMastered") @set:PropertyName("isMastered") var isMastered: Boolean = false,
    val linkedJournalIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "title" to title,
        "executiveSummary" to executiveSummary,
        "problemBusinessNeed" to problemBusinessNeed,
        "toolsTechnologies" to toolsTechnologies,
        "architectureSteps" to architectureSteps,
        "resultsMetrics" to resultsMetrics,
        "verifiedEvidence" to verifiedEvidence,
        "lessonsLearned" to lessonsLearned,
        "isVerified" to isVerified,
        "isMastered" to isMastered,
        "linkedJournalIds" to linkedJournalIds
    )
}
