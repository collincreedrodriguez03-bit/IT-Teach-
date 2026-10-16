package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.components.FlashcardSessionOverlay
import com.example.ui.components.HamburgerDrawerContent
import com.example.ui.components.MentorChatSheet
import com.example.ui.components.PinLockScreen
import com.example.ui.components.QuizDialog
import com.example.ui.components.RecruiterDossierDialog
import com.example.ui.screens.DictionaryScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.PortfolioScreen
import com.example.ui.screens.QuestsScreen
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TechGold
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppRootContent()
            }
        }
    }
}

@Composable
fun AppRootContent() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isOfflineMode by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null && !isOfflineMode) {
        AuthScreen(
            onAuthSuccess = { _, _ ->
                currentUser = Firebase.auth.currentUser
            },
            onContinueOffline = {
                isOfflineMode = true
            }
        )
    } else {
        MainAppScreen(
            currentUserId = currentUser?.uid ?: "local_offline_engineer"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    currentUserId: String,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val dictionaryTerms by viewModel.dictionaryTerms.collectAsStateWithLifecycle()
    val journalEntries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val portfolioProjects by viewModel.portfolioProjects.collectAsStateWithLifecycle()
    val quests by viewModel.quests.collectAsStateWithLifecycle()
    val activityLogs by viewModel.activityLogs.collectAsStateWithLifecycle()

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isFlashcardMode by viewModel.isFlashcardMode.collectAsStateWithLifecycle()
    val isEmployerPreview by viewModel.isEmployerPreview.collectAsStateWithLifecycle()

    val isPinLocked by viewModel.isPinLocked.collectAsStateWithLifecycle()
    val isPinSetupMode by viewModel.isPinSetupMode.collectAsStateWithLifecycle()
    val pinAttemptsLeft by viewModel.pinAttemptsLeft.collectAsStateWithLifecycle()
    val lockoutCountdown by viewModel.lockoutCountdown.collectAsStateWithLifecycle()

    val isMentorChatOpen by viewModel.isMentorChatOpen.collectAsStateWithLifecycle()
    val mentorMessages by viewModel.mentorMessages.collectAsStateWithLifecycle()
    val isMentorTyping by viewModel.isMentorTyping.collectAsStateWithLifecycle()
    val isThinkingEnabled by viewModel.isThinkingEnabled.collectAsStateWithLifecycle()
    val isSearchGroundingEnabled by viewModel.isSearchGroundingEnabled.collectAsStateWithLifecycle()
    val isLowLatencyEnabled by viewModel.isLowLatencyEnabled.collectAsStateWithLifecycle()

    val activeQuizQuest by viewModel.activeQuizQuest.collectAsStateWithLifecycle()
    val activeQuizQuestions by viewModel.activeQuizQuestions.collectAsStateWithLifecycle()
    val isQuizLoading by viewModel.isQuizLoading.collectAsStateWithLifecycle()

    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    var showRecruiterDossier by remember { mutableStateOf(false) }
    var isFullscreenFlashcardSession by remember { mutableStateOf(false) }

    val streakMultiplier = viewModel.getStreakMultiplier()
    val hybridFlashcards = remember(dictionaryTerms, journalEntries) {
        viewModel.generateHybridFlashcardDeck()
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Handle back button: close drawer first, or return to Learn tab (0)
    BackHandler(enabled = drawerState.isOpen || selectedTab != 0 || isFullscreenFlashcardSession || showRecruiterDossier) {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            isFullscreenFlashcardSession -> isFullscreenFlashcardSession = false
            showRecruiterDossier -> showRecruiterDossier = false
            selectedTab != 0 -> viewModel.setSelectedTab(0)
        }
    }

    // 4-Digit Security PIN Gate
    if (isPinLocked) {
        PinLockScreen(
            isSetupMode = isPinSetupMode,
            attemptsLeft = pinAttemptsLeft,
            lockoutSeconds = lockoutCountdown,
            onSubmitPin = { pin ->
                viewModel.submitPin(pin)
            }
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HamburgerDrawerContent(
                userProfile = userProfile,
                journalEntries = journalEntries,
                portfolioProjects = portfolioProjects,
                dictionaryTerms = dictionaryTerms,
                quests = quests,
                activityLogs = activityLogs,
                streakMultiplier = streakMultiplier,
                onNavigateTab = { tab ->
                    viewModel.setSelectedTab(tab)
                    scope.launch { drawerState.close() }
                },
                onOpenMentor = {
                    scope.launch { drawerState.close() }
                    viewModel.setMentorChatOpen(true)
                },
                onOpenRecruiterDossier = {
                    scope.launch { drawerState.close() }
                    showRecruiterDossier = true
                },
                onLockApp = {
                    scope.launch { drawerState.close() }
                    viewModel.lockAppManually()
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DeepNavy,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (selectedTab) {
                                0 -> "Learn Academy"
                                1 -> "IT Lab Journal"
                                2 -> "Portfolio"
                                3 -> "IT Dictionary"
                                else -> "Network Teach"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            modifier = Modifier.testTag("hamburger_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = CyberCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CardNavy,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        // Compact Streak & Multiplier Chip
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF261905), RoundedCornerShape(14.dp))
                                .border(1.dp, TechGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = TechGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${userProfile?.streakCount ?: 1}d • ${String.format("%.2f", streakMultiplier)}x",
                                fontWeight = FontWeight.Bold,
                                color = TechGold,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Compact XP Chip
                        Row(
                            modifier = Modifier
                                .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "XP",
                                tint = CyberCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${userProfile?.xp ?: 0}",
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                fontSize = 10.sp
                            )
                        }

                        // Recruiter Dossier Icon Action
                        IconButton(
                            onClick = { showRecruiterDossier = true },
                            modifier = Modifier.testTag("topbar_recruiter_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Recruiter Dossier View",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // AI Architect Button
                        IconButton(
                            onClick = { viewModel.setMentorChatOpen(true) },
                            modifier = Modifier.testTag("mentor_icon_topbar")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Open AI Architect Mentor",
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Manual PIN Lock Button
                        IconButton(
                            onClick = { viewModel.lockAppManually() },
                            modifier = Modifier.testTag("lock_app_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock App",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                // Strictly 4 items: [ Learn | Journal | Portfolio | Dictionary ]
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = CardNavy,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val navItems = listOf(
                        Triple(0, "Learn", Icons.Default.School),
                        Triple(1, "Journal", Icons.AutoMirrored.Filled.Assignment),
                        Triple(2, "Portfolio", Icons.Default.Work),
                        Triple(3, "Dictionary", Icons.Default.Book)
                    )

                    navItems.forEach { (index, title, icon) ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedTab(index) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DeepNavy,
                                selectedTextColor = CyberCyan,
                                indicatorColor = CyberCyan,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${title.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> QuestsScreen(
                        userProfile = userProfile,
                        quests = quests,
                        activityLogs = activityLogs,
                        flashcardItems = hybridFlashcards,
                        streakMultiplier = streakMultiplier,
                        onCompleteQuest = { quest -> viewModel.completeQuestDirect(quest) },
                        onStartDailyQuiz = { quest -> viewModel.startDailyQuiz(quest) },
                        onLaunchFlashcardSession = { isFullscreenFlashcardSession = true },
                        onLaunchAiAssessment = { title, moduleType ->
                            viewModel.startAiAssessment(title, moduleType)
                        }
                    )
                    1 -> JournalScreen(
                        entries = journalEntries,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onSelectCategory = { viewModel.setSelectedCategory(it) },
                        onAddEntry = { title, notes, solution, tags, cat, verified ->
                            viewModel.addJournalEntry(title, notes, solution, tags, cat, verified)
                        },
                        onUpdateEntry = { entry ->
                            viewModel.updateJournalEntry(entry)
                        },
                        onToggleVerified = { entry -> viewModel.toggleJournalVerified(entry) },
                        onPromoteToPortfolio = { entry -> viewModel.promoteJournalToPortfolio(entry) },
                        onDeleteEntry = { id -> viewModel.deleteJournalEntry(id) }
                    )
                    2 -> PortfolioScreen(
                        projects = portfolioProjects,
                        isEmployerPreview = isEmployerPreview,
                        onToggleEmployerPreview = { viewModel.toggleEmployerPreview() },
                        onAddProject = { project -> viewModel.addPortfolioProject(project) },
                        onUpdateProject = { project -> viewModel.updatePortfolioProject(project) },
                        onToggleMastered = { project -> viewModel.togglePortfolioMastered(project) },
                        onDeleteProject = { id -> viewModel.deletePortfolioProject(id) }
                    )
                    3 -> DictionaryScreen(
                        userProfile = userProfile,
                        terms = dictionaryTerms,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onSelectCategory = { viewModel.setSelectedCategory(it) },
                        isFlashcardMode = isFlashcardMode,
                        onToggleFlashcardMode = { viewModel.toggleFlashcardMode() },
                        onAddTerm = { term, cmd, desc, cat, diff, aiCheck ->
                            viewModel.addDictionaryTerm(term, cmd, desc, cat, diff, aiCheck)
                        },
                        onUpdateTerm = { term ->
                            viewModel.updateDictionaryTerm(term)
                        },
                        onToggleMastery = { term -> viewModel.toggleDictionaryMastery(term) },
                        onDeleteTerm = { id -> viewModel.deleteDictionaryTerm(id) }
                    )
                }

                // Adaptive Quiz Modal
                if (activeQuizQuest != null) {
                    QuizDialog(
                        quest = activeQuizQuest!!,
                        questions = activeQuizQuestions,
                        isLoading = isQuizLoading,
                        onDismiss = { viewModel.closeDailyQuiz() },
                        onFinish = { correct, total ->
                            viewModel.finishDailyQuiz(activeQuizQuest!!, correct, total)
                        }
                    )
                }

                // AI Mentor Chat Bottom Sheet
                if (isMentorChatOpen) {
                    MentorChatSheet(
                        messages = mentorMessages,
                        isTyping = isMentorTyping,
                        isThinkingEnabled = isThinkingEnabled,
                        onToggleThinking = { viewModel.toggleThinking() },
                        isSearchGroundingEnabled = isSearchGroundingEnabled,
                        onToggleSearchGrounding = { viewModel.toggleSearchGrounding() },
                        isLowLatencyEnabled = isLowLatencyEnabled,
                        onToggleLowLatency = { viewModel.toggleLowLatency() },
                        onSendMessage = { text, bmp ->
                            viewModel.sendMentorMessage(text, bmp)
                        },
                        onDismiss = { viewModel.setMentorChatOpen(false) }
                    )
                }

                // Recruiter Ready Markdown Dossier Dialog
                if (showRecruiterDossier) {
                    RecruiterDossierDialog(
                        userProfile = userProfile,
                        portfolioProjects = portfolioProjects,
                        journalEntries = journalEntries,
                        dictionaryTerms = dictionaryTerms,
                        onDismiss = { showRecruiterDossier = false }
                    )
                }

                // Fullscreen 70/30 Hybrid Flashcard Session
                if (isFullscreenFlashcardSession) {
                    FlashcardSessionOverlay(
                        cards = hybridFlashcards,
                        streakMultiplier = streakMultiplier,
                        onDismiss = { isFullscreenFlashcardSession = false },
                        onFinishSession = { completed, total ->
                            viewModel.completeFlashcardSession(completed, total)
                        }
                    )
                }
            }
        }
    }
}
