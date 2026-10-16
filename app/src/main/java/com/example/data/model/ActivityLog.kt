package com.example.data.model

data class ActivityLog(
    val id: String = "",
    val title: String = "",
    val moduleType: String = "QUIZ", // QUIZ, WORKSHEET, FLASHCARDS, TEST, PROJECT
    val status: String = "Incomplete", // Completed, Incomplete
    val gradePercent: Int? = null,
    val completedSteps: Int = 0,
    val totalSteps: Int = 10,
    val weakTopics: List<String> = emptyList(),
    val summary: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val displayStatus: String
        get() = if (status.equals("Completed", ignoreCase = true) && gradePercent != null) {
            "Completed ($gradePercent%)"
        } else {
            "Incomplete ($completedSteps/$totalSteps steps)"
        }
}
