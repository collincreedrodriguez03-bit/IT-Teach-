package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActivityLog
import com.example.data.model.DictionaryTerm
import com.example.data.model.FlashcardItem
import com.example.data.model.JournalEntry
import com.example.data.model.PortfolioProject
import com.example.data.model.Quest
import com.example.data.model.UserProfile
import com.example.data.repository.NetQuestRepository
import com.example.data.repository.PinVerificationResult
import com.example.data.service.GeminiService
import com.example.data.service.OfflineToolQuestions
import com.example.data.service.QuizQuestion
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NetQuestRepository(application)
    private val geminiService = GeminiService()

    val currentUserId: String?
        get() = repository.currentUserIdOrNull()

    // Navigation state (0: Learn, 1: Journal, 2: Portfolio, 3: Dictionary)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    // Filter and search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    fun setSearchQuery(query: String) { _searchQuery.value = query }

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    fun setSelectedCategory(cat: String) { _selectedCategory.value = cat }

    // Toggles
    private val _isFlashcardMode = MutableStateFlow(false)
    val isFlashcardMode: StateFlow<Boolean> = _isFlashcardMode.asStateFlow()
    fun toggleFlashcardMode() { _isFlashcardMode.value = !_isFlashcardMode.value }

    private val _isEmployerPreview = MutableStateFlow(false)
    val isEmployerPreview: StateFlow<Boolean> = _isEmployerPreview.asStateFlow()
    fun toggleEmployerPreview() { _isEmployerPreview.value = !_isEmployerPreview.value }

    // PIN Security Lock state
    private val _isPinLocked = MutableStateFlow(false)
    val isPinLocked: StateFlow<Boolean> = _isPinLocked.asStateFlow()

    private val _isPinSetupMode = MutableStateFlow(false)
    val isPinSetupMode: StateFlow<Boolean> = _isPinSetupMode.asStateFlow()

    private val _pinAttemptsLeft = MutableStateFlow(3)
    val pinAttemptsLeft: StateFlow<Int> = _pinAttemptsLeft.asStateFlow()

    private val _lockoutCountdown = MutableStateFlow(0)
    val lockoutCountdown: StateFlow<Int> = _lockoutCountdown.asStateFlow()

    private var lockoutJob: Job? = null

    // Real-time local & remote state flows
    val userProfile: StateFlow<UserProfile?> = repository.observeUserProfile()
        .catch { emit(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dictionaryTerms: StateFlow<List<DictionaryTerm>> = repository.observeDictionary()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntry>> = repository.observeJournal()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolioProjects: StateFlow<List<PortfolioProject>> = repository.observePortfolio()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quests: StateFlow<List<Quest>> = repository.observeQuests()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLog>> = repository.observeActivityLogs()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Mentor Chat
    private val _isMentorChatOpen = MutableStateFlow(false)
    val isMentorChatOpen: StateFlow<Boolean> = _isMentorChatOpen.asStateFlow()
    fun setMentorChatOpen(open: Boolean) { _isMentorChatOpen.value = open }

    private val _mentorMessages = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "model" to "Greetings, Engineer. I am your Senior IT & Network Systems Mentor. Ask me any troubleshooting questions, paste CLI error logs, or attach network topologies."
        )
    )
    val mentorMessages: StateFlow<List<Pair<String, String>>> = _mentorMessages.asStateFlow()

    private val _isMentorTyping = MutableStateFlow(false)
    val isMentorTyping: StateFlow<Boolean> = _isMentorTyping.asStateFlow()

    private val _isThinkingEnabled = MutableStateFlow(true)
    val isThinkingEnabled: StateFlow<Boolean> = _isThinkingEnabled.asStateFlow()
    fun toggleThinking() { _isThinkingEnabled.value = !_isThinkingEnabled.value }

    private val _isSearchGroundingEnabled = MutableStateFlow(false)
    val isSearchGroundingEnabled: StateFlow<Boolean> = _isSearchGroundingEnabled.asStateFlow()
    fun toggleSearchGrounding() { _isSearchGroundingEnabled.value = !_isSearchGroundingEnabled.value }

    private val _isLowLatencyEnabled = MutableStateFlow(false)
    val isLowLatencyEnabled: StateFlow<Boolean> = _isLowLatencyEnabled.asStateFlow()
    fun toggleLowLatency() { _isLowLatencyEnabled.value = !_isLowLatencyEnabled.value }

    // Quizzes & Worksheets
    private val _activeQuizQuest = MutableStateFlow<Quest?>(null)
    val activeQuizQuest: StateFlow<Quest?> = _activeQuizQuest.asStateFlow()

    private val _activeQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val activeQuizQuestions: StateFlow<List<QuizQuestion>> = _activeQuizQuestions.asStateFlow()

    private val _isQuizLoading = MutableStateFlow(false)
    val isQuizLoading: StateFlow<Boolean> = _isQuizLoading.asStateFlow()

    // Toast message trigger
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()
    fun clearToast() { _toastMessage.value = null }

    init {
        val user = Firebase.auth.currentUser
        val email = user?.email ?: "engineer@networkteach.local"
        val displayName = user?.displayName ?: "Senior IT Engineer"
        initProfile(email, displayName)
    }

    fun initProfile(email: String, displayName: String) {
        viewModelScope.launch {
            try {
                val profile = repository.getOrCreateUserProfile(email, displayName)
                if (profile.pinHash.isBlank()) {
                    _isPinSetupMode.value = true
                    _isPinLocked.value = false
                } else {
                    _isPinSetupMode.value = false
                    _isPinLocked.value = false
                }
            } catch (e: Exception) {
                _toastMessage.value = "Profile initialized offline: ${e.message}"
            }
        }
    }

    // ==========================================
    // STREAK & XP MULTIPLIER
    // ==========================================

    fun getStreakMultiplier(): Float {
        val count = userProfile.value?.streakCount ?: 1L
        return repository.calculateStreakMultiplier(count)
    }

    // ==========================================
    // PIN SECURITY
    // ==========================================

    fun submitPin(pin: String) {
        viewModelScope.launch {
            if (_isPinSetupMode.value) {
                if (pin.length == 4) {
                    repository.setPin(pin)
                    _isPinSetupMode.value = false
                    _isPinLocked.value = false
                    _toastMessage.value = "4-digit security PIN set successfully!"
                } else {
                    _toastMessage.value = "PIN must be exactly 4 digits."
                }
            } else {
                when (val result = repository.verifyPin(pin)) {
                    is PinVerificationResult.Success -> {
                        _isPinLocked.value = false
                        _pinAttemptsLeft.value = 3
                        _toastMessage.value = "Access Granted! Welcome back."
                    }
                    is PinVerificationResult.Failed -> {
                        _pinAttemptsLeft.value = result.attemptsRemaining
                        _toastMessage.value = "Incorrect PIN! ${result.attemptsRemaining} attempts left."
                    }
                    is PinVerificationResult.LockedOut -> {
                        startLockout(result.secondsRemaining)
                    }
                }
            }
        }
    }

    private fun startLockout(seconds: Int) {
        _lockoutCountdown.value = seconds
        _toastMessage.value = "Security lockout triggered! Please wait $seconds seconds."
        lockoutJob?.cancel()
        lockoutJob = viewModelScope.launch {
            var s = seconds
            while (s > 0) {
                delay(1000)
                s -= 1
                _lockoutCountdown.value = s
            }
        }
    }

    fun lockAppManually() {
        _isPinLocked.value = true
    }

    // ==========================================
    // DICTIONARY ACTIONS & INLINE EDITING
    // ==========================================

    fun addDictionaryTerm(
        term: String,
        command: String,
        description: String,
        category: String,
        difficulty: String,
        requestAiClarification: Boolean = false
    ) {
        viewModelScope.launch {
            var aiNote = ""
            if (requestAiClarification) {
                aiNote = geminiService.clarifyDictionaryCommand(term, command, description)
            }
            val newTerm = DictionaryTerm(
                term = term.trim(),
                command = command.trim(),
                description = description.trim(),
                category = category,
                difficulty = difficulty,
                isMastered = false,
                isAiGenerated = false,
                aiExplanation = aiNote
            )
            repository.addDictionaryTerm(newTerm)
            val mult = getStreakMultiplier()
            _toastMessage.value = "+${(15 * mult).toInt()} XP! Added command with ${String.format("%.2f", mult)}x streak!"
        }
    }

    fun updateDictionaryTerm(term: DictionaryTerm) {
        viewModelScope.launch {
            repository.updateDictionaryTerm(term)
            _toastMessage.value = "Updated: ${term.term}"
        }
    }

    fun toggleDictionaryMastery(term: DictionaryTerm) {
        viewModelScope.launch {
            repository.toggleDictionaryMastered(term)
            if (!term.isMastered) {
                val mult = getStreakMultiplier()
                _toastMessage.value = "+${(25 * mult).toInt()} XP! Command marked as Mastered ⚡"
            }
        }
    }

    fun deleteDictionaryTerm(termId: String) {
        viewModelScope.launch {
            repository.deleteDictionaryTerm(termId)
            _toastMessage.value = "Command deleted"
        }
    }

    // ==========================================
    // JOURNAL ACTIONS & INLINE EDITING
    // ==========================================

    fun addJournalEntry(
        title: String,
        notes: String,
        solution: String,
        tags: List<String>,
        category: String,
        isVerified: Boolean
    ) {
        viewModelScope.launch {
            val entry = JournalEntry(
                title = title.trim(),
                notes = notes.trim(),
                solution = solution.trim(),
                tags = tags,
                category = category,
                isVerified = isVerified,
                isMastered = false
            )
            repository.addJournalEntry(entry)
            val mult = getStreakMultiplier()
            _toastMessage.value = "+${(30 * mult).toInt()} XP! Journal entry logged with ${String.format("%.2f", mult)}x multiplier!"
        }
    }

    fun updateJournalEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.updateJournalEntry(entry)
            _toastMessage.value = "Journal lab entry updated"
        }
    }

    fun toggleJournalVerified(entry: JournalEntry) {
        viewModelScope.launch {
            repository.toggleJournalVerified(entry)
            if (!entry.isVerified) {
                val mult = getStreakMultiplier()
                _toastMessage.value = "+${(40 * mult).toInt()} XP! Lab marked as Verified Working 🛡️"
            }
        }
    }

    fun promoteJournalToPortfolio(entry: JournalEntry) {
        viewModelScope.launch {
            val newProject = PortfolioProject(
                title = entry.title,
                executiveSummary = "Troubleshooting and architecture deployment for ${entry.title}. Verified working under lab conditions.",
                problemBusinessNeed = entry.notes,
                toolsTechnologies = entry.tags.ifEmpty { listOf("Networking", "CLI") },
                architectureSteps = "1. Diagnostic triage and incident capture.\n2. Root cause isolation.\n3. Verified resolution applied: ${entry.solution}",
                resultsMetrics = "Incident MTTR improved. Verified operational with 100% test pass rate.",
                verifiedEvidence = entry.solution,
                lessonsLearned = "Proper documentation and structured validation prevent configuration regressions.",
                isVerified = entry.isVerified,
                isMastered = true,
                linkedJournalIds = listOf(entry.id)
            )
            repository.addPortfolioProject(newProject)
            _toastMessage.value = "+75 XP! Promoted lab to Employer Case Study 🚀"
        }
    }

    fun deleteJournalEntry(entryId: String) {
        viewModelScope.launch {
            repository.deleteJournalEntry(entryId)
            _toastMessage.value = "Journal entry deleted"
        }
    }

    // ==========================================
    // PORTFOLIO ACTIONS & INLINE EDITING
    // ==========================================

    fun addPortfolioProject(project: PortfolioProject) {
        viewModelScope.launch {
            repository.addPortfolioProject(project)
            val mult = getStreakMultiplier()
            _toastMessage.value = "+${(75 * mult).toInt()} XP! Published Employer Case Study 💼"
        }
    }

    fun updatePortfolioProject(project: PortfolioProject) {
        viewModelScope.launch {
            repository.updatePortfolioProject(project)
            _toastMessage.value = "Case study updated"
        }
    }

    fun togglePortfolioMastered(project: PortfolioProject) {
        viewModelScope.launch {
            repository.togglePortfolioMastered(project)
            if (!project.isMastered) {
                _toastMessage.value = "+50 XP! Case Study Mastered"
            }
        }
    }

    fun deletePortfolioProject(projectId: String) {
        viewModelScope.launch {
            repository.deletePortfolioProject(projectId)
            _toastMessage.value = "Case study deleted"
        }
    }

    // ==========================================
    // QUESTS & QUIZZES
    // ==========================================

    fun completeQuestDirect(quest: Quest) {
        viewModelScope.launch {
            repository.completeQuest(quest)
            val mult = getStreakMultiplier()
            val totalXp = (quest.xpReward * mult).toInt()
            _toastMessage.value = "+$totalXp XP! Quest Completed: ${quest.title} 🏆"
        }
    }

    fun startDailyQuiz(quest: Quest) {
        _activeQuizQuest.value = quest
        _isQuizLoading.value = true
        viewModelScope.launch {
            val questions = geminiService.generateDailyQuiz(quest.title)
            _activeQuizQuestions.value = questions
            _isQuizLoading.value = false
        }
    }

    fun startAiAssessment(title: String, moduleType: String) {
        val assessmentQuest = Quest(
            id = UUID.randomUUID().toString(),
            title = title,
            description = "AI-Calibrated $moduleType assessment based on performance history.",
            category = "NETWORKING",
            questType = moduleType,
            xpReward = 60L
        )
        _activeQuizQuest.value = assessmentQuest
        _isQuizLoading.value = true
        viewModelScope.launch {
            val questions = geminiService.generateDailyQuiz(title)
            _activeQuizQuestions.value = questions
            _isQuizLoading.value = false
        }
    }

    fun closeDailyQuiz() {
        _activeQuizQuest.value = null
        _activeQuizQuestions.value = emptyList()
    }

    fun finishDailyQuiz(quest: Quest, correctCount: Int, total: Int) {
        viewModelScope.launch {
            val pct = if (total > 0) (correctCount * 100) / total else 0
            val isPassed = pct >= 60

            val log = ActivityLog(
                id = UUID.randomUUID().toString(),
                title = quest.title,
                moduleType = if (quest.questType.isNotBlank()) quest.questType else "QUIZ",
                status = if (isPassed) "Completed" else "Incomplete",
                gradePercent = pct,
                completedSteps = correctCount,
                totalSteps = total,
                weakTopics = if (isPassed) emptyList() else listOf(quest.category, quest.title.take(25)),
                summary = "Scored $correctCount/$total ($pct%) on ${quest.title}"
            )
            repository.logActivity(log)

            if (isPassed) {
                repository.completeQuest(quest)
                val mult = getStreakMultiplier()
                val earned = (quest.xpReward * mult).toInt()
                _toastMessage.value = "Quiz Passed ($correctCount/$total)! +$earned XP! ⚡"
            } else {
                _toastMessage.value = "Score: $correctCount/$total ($pct%). Logged to Activity Tracker."
            }
            closeDailyQuiz()
        }
    }

    // ==========================================
    // 70/30 HYBRID FLASHCARD ENGINE
    // ==========================================

    fun generateHybridFlashcardDeck(): List<FlashcardItem> {
        val userTerms = dictionaryTerms.value
        val userJournals = journalEntries.value

        // 70% User pool
        val userPool = mutableListOf<FlashcardItem>()
        userTerms.forEach { t ->
            userPool.add(
                FlashcardItem(
                    id = "term_${t.id}",
                    title = t.term,
                    questionOrFront = if (t.command.isNotBlank()) "Command: ${t.command}" else t.term,
                    answerOrBack = "${t.description}\nCategory: ${t.category}",
                    category = t.category,
                    isAiReinforcement = false,
                    toolBadge = t.category
                )
            )
        }
        userJournals.forEach { j ->
            userPool.add(
                FlashcardItem(
                    id = "journal_${j.id}",
                    title = j.title,
                    questionOrFront = "Lab Scenario: ${j.title}\n${j.notes.take(120)}...",
                    answerOrBack = "Verified Solution:\n${j.solution}",
                    category = j.category,
                    isAiReinforcement = false,
                    toolBadge = "Lab Journal"
                )
            )
        }

        val totalTarget = 10
        val targetUserCount = (totalTarget * 0.70).toInt().coerceAtLeast(3)
        val targetAiCount = totalTarget - minOf(userPool.size, targetUserCount)

        val selectedUserItems = userPool.shuffled().take(targetUserCount)

        // 30% Reinforcement pool from 30+ IT tools offline questions
        val offlinePool = OfflineToolQuestions.REINFORCEMENT_QUESTIONS.shuffled().take(targetAiCount).map { q ->
            FlashcardItem(
                id = "tool_${UUID.randomUUID()}",
                title = q.toolName,
                questionOrFront = q.question,
                answerOrBack = "${q.answer}\n${if (q.commandExample.isNotBlank()) "Example: " + q.commandExample else ""}",
                category = q.domain,
                isAiReinforcement = true,
                toolBadge = q.toolName
            )
        }

        return (selectedUserItems + offlinePool).shuffled()
    }

    fun completeFlashcardSession(completedCards: Int, totalCards: Int) {
        viewModelScope.launch {
            val pct = if (totalCards > 0) (completedCards * 100) / totalCards else 0
            val isCompleted = completedCards >= (totalCards * 0.8).toInt()

            val log = ActivityLog(
                id = UUID.randomUUID().toString(),
                title = "70/30 Hybrid Flashcards Session",
                moduleType = "FLASHCARDS",
                status = if (isCompleted) "Completed" else "Incomplete",
                gradePercent = pct,
                completedSteps = completedCards,
                totalSteps = totalCards,
                weakTopics = if (!isCompleted) listOf("Tool Commands", "Network Definitions") else emptyList(),
                summary = "Reviewed $completedCards/$totalCards flashcards (70% user notes, 30% reinforcement)."
            )
            repository.logActivity(log)

            val mult = getStreakMultiplier()
            val xpGain = (40 * mult).toInt()
            _toastMessage.value = "Flashcards session finished ($completedCards/$totalCards)! +$xpGain XP ⚡"
        }
    }

    // ==========================================
    // ACTIVITY LOGS
    // ==========================================

    fun recordManualActivity(
        title: String,
        moduleType: String,
        status: String,
        gradePercent: Int?,
        completedSteps: Int,
        totalSteps: Int,
        weakTopics: List<String>,
        summary: String
    ) {
        viewModelScope.launch {
            val log = ActivityLog(
                id = UUID.randomUUID().toString(),
                title = title,
                moduleType = moduleType,
                status = status,
                gradePercent = gradePercent,
                completedSteps = completedSteps,
                totalSteps = totalSteps,
                weakTopics = weakTopics,
                summary = summary
            )
            repository.logActivity(log)
            _toastMessage.value = "Recorded in Activity Log: $title"
        }
    }

    // ==========================================
    // MENTOR CHAT
    // ==========================================

    fun sendMentorMessage(userText: String, bitmap: Bitmap? = null) {
        if (userText.isBlank() && bitmap == null) return
        val currentList = _mentorMessages.value.toMutableList()
        val promptWithNote = if (bitmap != null && userText.isBlank()) "Analyze this image/screenshot and explain what you see." else userText
        currentList.add("user" to promptWithNote)
        _mentorMessages.value = currentList
        _isMentorTyping.value = true

        viewModelScope.launch {
            val response = geminiService.chatWithArchitect(
                messages = currentList.dropLast(1),
                userPrompt = promptWithNote,
                imageBitmap = bitmap,
                enableHighThinking = _isThinkingEnabled.value,
                useSearchGrounding = _isSearchGroundingEnabled.value,
                useLowLatency = _isLowLatencyEnabled.value
            )
            _mentorMessages.value = _mentorMessages.value + ("model" to response)
            _isMentorTyping.value = false
        }
    }
}
