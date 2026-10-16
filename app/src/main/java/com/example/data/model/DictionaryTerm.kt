package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class DictionaryTerm(
    @DocumentId val id: String = "",
    val userId: String = "",
    val term: String = "",
    val command: String = "",
    val description: String = "",
    val category: String = "Networking",
    val difficulty: String = "Beginner",
    @get:PropertyName("isMastered") @set:PropertyName("isMastered") var isMastered: Boolean = false,
    @get:PropertyName("isAiGenerated") @set:PropertyName("isAiGenerated") var isAiGenerated: Boolean = false,
    val aiExplanation: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "term" to term,
        "command" to command,
        "description" to description,
        "category" to category,
        "difficulty" to difficulty,
        "isMastered" to isMastered,
        "isAiGenerated" to isAiGenerated,
        "aiExplanation" to aiExplanation
    )
}
