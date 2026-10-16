package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityLog
import com.example.data.model.FlashcardItem
import com.example.data.model.Quest
import com.example.data.model.UserProfile
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechGold
import com.example.ui.theme.TechPurple
import com.example.ui.theme.TerminalBg

@Composable
fun QuestsScreen(
    userProfile: UserProfile?,
    quests: List<Quest>,
    activityLogs: List<ActivityLog>,
    flashcardItems: List<FlashcardItem>,
    streakMultiplier: Float,
    onCompleteQuest: (Quest) -> Unit,
    onStartDailyQuiz: (Quest) -> Unit,
    onLaunchFlashcardSession: () -> Unit,
    onLaunchAiAssessment: (title: String, moduleType: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    // 5 Comprehensive sub-tabs for the Learn Section
    val tabTitles = listOf("Missions", "AI Practice", "70/30 Flashcards", "Activity Log", "Badges")

    val mainQuests = quests.filter { it.questType == "MAIN" }
    val dailyQuests = quests.filter { it.questType == "DAILY" }
    val sideQuests = quests.filter { it.questType == "SIDE" }

    // Derive identified weak spots from past incomplete or lower-grade activity logs
    val detectedWeakSpots = activityLogs
        .flatMap { it.weakTopics }
        .groupingBy { it }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
        .map { it.first }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("learn_screen")
    ) {
        // Learn Section Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardNavy)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Learn & Engineering Academy",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "AI-calibrated tests, 70/30 hybrid flashcards & activity logs",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Capped streak chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF261905))
                        .border(1.dp, TechGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = TechGold, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${String.format("%.2f", streakMultiplier)}x",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechGold
                    )
                }
            }
        }

        // Sub-tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardNavyElevated,
            contentColor = CyberCyan,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyberCyan
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("learn_subtab_$index")
                )
            }
        }

        // Sub-tab content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                0 -> MissionsSubTab(
                    mainQuests = mainQuests,
                    dailyQuests = dailyQuests,
                    sideQuests = sideQuests,
                    onCompleteQuest = onCompleteQuest,
                    onStartDailyQuiz = onStartDailyQuiz
                )
                1 -> AiPracticeSubTab(
                    detectedWeakSpots = detectedWeakSpots,
                    activityLogs = activityLogs,
                    onLaunchAiAssessment = onLaunchAiAssessment
                )
                2 -> FlashcardsSubTab(
                    flashcardItems = flashcardItems,
                    streakMultiplier = streakMultiplier,
                    onLaunchSession = onLaunchFlashcardSession
                )
                3 -> ActivityLogSubTab(
                    activityLogs = activityLogs,
                    detectedWeakSpots = detectedWeakSpots
                )
                4 -> BadgesSubTab(
                    userProfile = userProfile,
                    activityLogs = activityLogs
                )
            }
        }
    }
}

// =========================================================================
// SUB-TAB 0: MISSIONS & CERTIFICATION QUESTS
// =========================================================================

@Composable
private fun MissionsSubTab(
    mainQuests: List<Quest>,
    dailyQuests: List<Quest>,
    sideQuests: List<Quest>,
    onCompleteQuest: (Quest) -> Unit,
    onStartDailyQuiz: (Quest) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (dailyQuests.isNotEmpty()) {
            item {
                Text(
                    text = "Daily Certification Trials",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechGold
                )
            }
            items(dailyQuests, key = { it.id }) { quest ->
                QuestItemCard(
                    quest = quest,
                    isDaily = true,
                    onComplete = { onCompleteQuest(quest) },
                    onStartQuiz = { onStartDailyQuiz(quest) }
                )
            }
        }

        item {
            Text(
                text = "Core Certification Paths (A+, CCNA, NetDevOps)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan
            )
        }
        items(mainQuests, key = { it.id }) { quest ->
            QuestItemCard(
                quest = quest,
                isDaily = false,
                onComplete = { onCompleteQuest(quest) },
                onStartQuiz = { onStartDailyQuiz(quest) }
            )
        }

        if (sideQuests.isNotEmpty()) {
            item {
                Text(
                    text = "Hands-on Side Quests & Terminal Tools",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechPurple
                )
            }
            items(sideQuests, key = { it.id }) { quest ->
                QuestItemCard(
                    quest = quest,
                    isDaily = false,
                    onComplete = { onCompleteQuest(quest) },
                    onStartQuiz = { onStartDailyQuiz(quest) }
                )
            }
        }
    }
}

// =========================================================================
// SUB-TAB 1: AI PRACTICE & DYNAMIC ASSESSMENTS
// =========================================================================

@Composable
private fun AiPracticeSubTab(
    detectedWeakSpots: List<String>,
    activityLogs: List<ActivityLog>,
    onLaunchAiAssessment: (title: String, moduleType: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Dynamic Calibration Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Dynamic AI Assessment Engine",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Calibrated from your Activity Log performance history",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (detectedWeakSpots.isNotEmpty()) {
                        Text(
                            text = "Calibrated Weak Topics Targeted:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TechGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            detectedWeakSpots.take(3).forEach { topic ->
                                Text(
                                    text = "• $topic",
                                    fontSize = 10.sp,
                                    color = TechGold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TechGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No critical weak spots detected! Practice modules will test broad omni-domain knowledge.",
                            fontSize = 11.sp,
                            color = TechEmerald
                        )
                    }
                }
            }
        }

        // Assessment Modules
        item {
            Text(
                text = "Available AI-Assisted Modules",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val practiceModules = listOf(
            Triple("Dynamic Pop Quiz: Weak Spot Sprint", "QUIZ", "10 targeted rapid-fire questions concentrating on topics with past lower scores."),
            Triple("Hands-on IT Lab Worksheet", "WORKSHEET", "Step-by-step problem-solving worksheet simulating real server and network incidents."),
            Triple("Comprehensive Mid-Tier IT Exam", "TEST", "Comprehensive 20-question practice test covering Windows, Linux, and Subnetting."),
            Triple("Scenario-Based Infrastructure Challenge", "PROJECT", "End-to-end network deployment scenario to test architecture decisions.")
        )

        items(practiceModules) { (title, type, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = type,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (type) {
                                "QUIZ" -> CyberCyan
                                "WORKSHEET" -> TechEmerald
                                "TEST" -> TechGold
                                else -> TechPurple
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CardNavyElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Text("+50 XP base", fontSize = 10.sp, color = TechGold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = desc,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onLaunchAiAssessment(title, type) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_ai_module_${type.lowercase()}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17))
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start $type Session", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// SUB-TAB 2: 70/30 HYBRID FLASHCARDS
// =========================================================================

@Composable
private fun FlashcardsSubTab(
    flashcardItems: List<FlashcardItem>,
    streakMultiplier: Float,
    onLaunchSession: () -> Unit
) {
    var previewIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "subtab_flashcard_rotation"
    )

    val currentCard = flashcardItems.getOrNull(previewIndex)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Explanatory Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "70/30 Hybrid Flashcard Engine",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TechGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("${String.format("%.2f", streakMultiplier)}x XP", fontSize = 10.sp, color = TechGold, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Combines 70% your personal dictionary & lab notes with 30% AI/offline tool reinforcement questions to solidify weak points.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onLaunchSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("launch_fullscreen_flashcards_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17))
                    ) {
                        Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Launch Fullscreen Session Overlay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Inline Card Flip Preview
        if (currentCard != null) {
            item {
                Text(
                    text = "Interactive Card Preview (Card ${previewIndex + 1} of ${flashcardItems.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardNavyElevated)
                        .border(
                            1.dp,
                            if (currentCard.isAiReinforcement) TechGold.copy(alpha = 0.6f) else CyberCyan.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { isFlipped = !isFlipped }
                        .padding(16.dp)
                        .testTag("preview_flashcard_card")
                ) {
                    if (rotation <= 90f) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (currentCard.isAiReinforcement) "30% AI REINFORCEMENT" else "70% USER NOTES",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentCard.isAiReinforcement) TechGold else CyberCyan
                                )
                                Text(currentCard.category, fontSize = 10.sp, color = TechPurple)
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentCard.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentCard.questionOrFront,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Tap card to flip for answer", fontSize = 10.sp, color = CyberCyan.copy(alpha = 0.7f))
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { rotationY = 180f },
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ANSWER & EXPLANATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TechEmerald)
                            Text(
                                text = currentCard.answerOrBack,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyberCyan,
                                lineHeight = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TerminalBg)
                                    .padding(10.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Tap again to flip front", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isFlipped = false
                            if (previewIndex > 0) previewIndex--
                        },
                        enabled = previewIndex > 0,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Previous")
                    }

                    OutlinedButton(
                        onClick = {
                            isFlipped = false
                            if (previewIndex + 1 < flashcardItems.size) previewIndex++
                        },
                        enabled = previewIndex + 1 < flashcardItems.size,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Next Card")
                    }
                }
            }
        }
    }
}

// =========================================================================
// SUB-TAB 3: ACTIVITY LOG (AI-DRIVEN TRACKER)
// =========================================================================

@Composable
private fun ActivityLogSubTab(
    activityLogs: List<ActivityLog>,
    detectedWeakSpots: List<String>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Engineering Activity Tracker",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Every quiz, worksheet, flashcard trial, and lab interaction is recorded here with completion ratings and score percentages.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        if (activityLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(42.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No activity logged yet", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Complete a quiz, lab, or flashcard review to populate your log.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(activityLogs, key = { it.id }) { log ->
                ActivityLogCard(log = log)
            }
        }
    }
}

@Composable
private fun ActivityLogCard(log: ActivityLog) {
    val isCompleted = log.status.equals("Completed", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) TechEmerald.copy(alpha = 0.4f) else TechGold.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header row: Type, Date, Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.moduleType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (log.moduleType) {
                            "QUIZ" -> CyberCyan
                            "FLASHCARDS" -> TechGold
                            "WORKSHEET" -> TechEmerald
                            else -> TechPurple
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CardNavyElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Status chip with score or step rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isCompleted) TechEmerald.copy(alpha = 0.15f) else TechGold.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isCompleted) TechEmerald else TechGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCompleted) {
                            "Completed (${log.gradePercent ?: 100}%)"
                        } else {
                            "Incomplete (${log.completedSteps}/${log.totalSteps} steps)"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) TechEmerald else TechGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (log.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = log.summary,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }

            if (log.weakTopics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Weak spots: ", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    log.weakTopics.forEach { topic ->
                        Text(
                            text = topic,
                            fontSize = 9.sp,
                            color = TechGold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(TechGold.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// SUB-TAB 4: BADGES & CERTIFICATIONS
// =========================================================================

@Composable
private fun BadgesSubTab(
    userProfile: UserProfile?,
    activityLogs: List<ActivityLog>
) {
    val badges = listOf(
        Triple("Rookie Engineer", "Started IT certification & networking track", "🎓"),
        Triple("Packet Analyzer", "Completed Wireshark packet capture trials", "🦈"),
        Triple("Subnet Architect", "Mastered IPv4 VLSM & CIDR subnet calculations", "🌐"),
        Triple("Terminal Vanguard", "Logged verified labs and command dictionary notes", "💻"),
        Triple("Streak Guardian", "Maintained consecutive study days with capped XP multiplier", "🔥"),
        Triple("Cloud Deployer", "Deployed mock microservices and network topologies", "☁️"),
        Triple("Security+ Defender", "Completed incident response and threat analysis quests", "🛡️"),
        Triple("Lead Vanguard", "Achieved Level 5+ Systems Engineer status", "⭐")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(badges) { (title, desc, emoji) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("EARNED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TechEmerald)
                }
            }
        }
    }
}

// =========================================================================
// COMMON QUEST ITEM CARD
// =========================================================================

@Composable
private fun QuestItemCard(
    quest: Quest,
    isDaily: Boolean,
    onComplete: () -> Unit,
    onStartQuiz: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (quest.isCompleted) TechEmerald.copy(alpha = 0.4f) else if (isDaily) TechGold.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = quest.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDaily) TechGold else CyberCyan,
                        modifier = Modifier
                            .background(
                                (if (isDaily) TechGold else CyberCyan).copy(alpha = 0.15f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                    Text(
                        text = "+${quest.xpReward} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechGold
                    )
                }

                if (quest.isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("COMPLETED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TechEmerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = quest.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = quest.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            if (!quest.isCompleted) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStartQuiz,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("start_quest_quiz_${quest.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17))
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Take Quiz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onComplete,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("complete_quest_direct_${quest.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text("Verify Lab", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
