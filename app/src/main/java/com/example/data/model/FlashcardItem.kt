package com.example.data.model

data class FlashcardItem(
    val id: String,
    val title: String,
    val questionOrFront: String,
    val answerOrBack: String,
    val category: String,
    val isAiReinforcement: Boolean,
    val toolBadge: String? = null
)
