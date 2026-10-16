package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.TabRow
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
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechGold
import com.example.ui.theme.TechPurple
import com.example.ui.theme.TerminalBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LearnScreen(
    userProfile: UserProfile?,
    quests: List<Quest>,
    activityLogs: List<ActivityLog>,
    onCompleteQuest: (Quest) -> Unit,
    onStartDailyQuiz: (Quest) -> Unit,
    onGenerateFlashcards: () -> List<FlashcardItem>,
    onCompleteFlashcards: (completed: Int, total: Int) -> Unit,
    onRecordActivity: (title: String, module: String, status: String, grade: Int?, comp: Int, tot: Int, weak: List<String>, sum: String) -> Unit
) {
    var selectedSectionTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Curriculum", "70/30 Cards", "Activity Log", "Worksheets")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedSectionTab,
            containerColor = CardNavy,
            contentColor = CyberCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSectionTab]),
                    color = CyberCyan
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSectionTab == index,
                    onClick = { selectedSectionTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedSectionTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSectionTab == index) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("learn_tab_$index")
                )
            }
        }

        when (selectedSectionTab) {
            0 -> LearnCurriculumSection(
                userProfile = userProfile,
                quests = quests,
                onCompleteQuest = onCompleteQuest,
                onStartDailyQuiz = onStartDailyQuiz
            )
            1 -> HybridFlashcardSection(
                onGenerateCards = onGenerateFlashcards,
                onCompleteSession = onCompleteFlashcards
            )
            2 -> ActivityLogSection(
                activityLogs = activityLogs,
                onTriggerCalibration = {
                    val weakTopics = activityLogs.flatMap { it.weakTopics }.distinct()
                    val targetTopic = weakTopics.firstOrNull() ?: "Subnetting & VLSM"
                    onRecordActivity(
                        "AI Calibrated Drill: $targetTopic",
                        "WORKSHEET",
                        "Incomplete",
                        null,
                        0,
                        5,
                        listOf(targetTopic),
                        "Dynamically calibrated from recent performance to strengthen: $targetTopic"
                    )
                }
            )
            3 -> WorksheetsDrillsSection(
                onRecordActivity = onRecordActivity
            )
        }
    }
}

// ==========================================
// 1. CURRICULUM & QUESTS
// ==========================================

@Composable
private fun LearnCurriculumSection(
    userProfile: UserProfile?,
    quests: List<Quest>,
    onCompleteQuest: (Quest) -> Unit,
    onStartDailyQuiz: (Quest) -> Unit
) {
    val mainQuests = quests.filter { it.questType == "MAIN" }
    val dailyQuests = quests.filter { it.questType == "DAILY" }
    val sideQuests = quests.filter { it.questType == "SIDE" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Daily Quick Drill Banner
        item {
            val pendingDaily = dailyQuests.firstOrNull { !it.isCompleted }
            if (pendingDaily != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardNavyElevated),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = TechGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DAILY ACTIVE DRILL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TechGold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = pendingDaily.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = pendingDaily.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onStartDailyQuiz(pendingDaily) },
                                colors = ButtonDefaults.buttonColors(containerColor = TechGold, contentColor = Color(0xFF0B0E17)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("start_daily_quiz_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Launch Quiz (+${pendingDaily.xpReward} XP)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section: Main Certification Roadmap
        item {
            Text(
                text = "CERTIFICATION TRACK ROADMAPS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = CyberCyan,
                letterSpacing = 1.sp
            )
        }

        items(mainQuests) { quest ->
            QuestItemCard(
                quest = quest,
                onComplete = { onCompleteQuest(quest) },
                onLaunchQuiz = { onStartDailyQuiz(quest) }
            )
        }

        // Section: Hands-on Side Quests
        if (sideQuests.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "HANDS-ON LAB SIDE QUESTS (OUTSIDE LEARNING)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TechEmerald,
                    letterSpacing = 1.sp
                )
            }

            items(sideQuests) { quest ->
                QuestItemCard(
                    quest = quest,
                    onComplete = { onCompleteQuest(quest) },
                    onLaunchQuiz = { onStartDailyQuiz(quest) }
                )
            }
        }
    }
}

@Composable
private fun QuestItemCard(
    quest: Quest,
    onComplete: () -> Unit,
    onLaunchQuiz: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (quest.isCompleted) TechEmerald.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = quest.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        modifier = Modifier
                            .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+${quest.xpReward} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechGold
                    )
                }

                if (quest.isCompleted) {
                    Row(
                        modifier = Modifier
                            .background(TechEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Completed", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TechEmerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = quest.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = quest.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (quest.outsideLearningRequirement.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Hardware Lab: ${quest.outsideLearningRequirement}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberCyan.copy(alpha = 0.9f)
                )
            }

            if (!quest.isCompleted) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onLaunchQuiz,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Practice Quiz", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = onComplete,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mark Verified", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. 70/30 HYBRID FLASHCARD ENGINE
// ==========================================

@Composable
private fun HybridFlashcardSection(
    onGenerateCards: () -> List<FlashcardItem>,
    onCompleteSession: (completed: Int, total: Int) -> Unit
) {
    var cards by remember { mutableStateOf(onGenerateCards()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var masteredInSession by remember { mutableIntStateOf(0) }
    var isSessionDone by remember { mutableStateOf(false) }

    var isFullscreenSession by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        label = "flashcard_rotation"
    )

    if (isFullscreenSession) {
        FullscreenFlashcardOverlay(
            cards = cards,
            onDismiss = { isFullscreenSession = false },
            onCompleteSession = { comp, tot ->
                onCompleteSession(comp, tot)
                isFullscreenSession = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pool Ratio Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Style, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "70% User Notes • 30% IT Tools",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isFullscreenSession = true },
                        modifier = Modifier.testTag("launch_fullscreen_flashcards_button")
                    ) {
                        Icon(Icons.Default.OpenInFull, contentDescription = "Fullscreen Mode", tint = TechGold, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = {
                            cards = onGenerateCards()
                            currentIndex = 0
                            isFlipped = false
                            masteredInSession = 0
                            isSessionDone = false
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate Deck", tint = CyberCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (cards.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No flashcards generated. Add dictionary terms or journal entries to start!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else if (isSessionDone) {
            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavyElevated),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechEmerald)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Session Completed!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Reviewed $masteredInSession of ${cards.size} flashcards. Recorded to your Activity Log.",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            cards = onGenerateCards()
                            currentIndex = 0
                            isFlipped = false
                            masteredInSession = 0
                            isSessionDone = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start Another Round", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            val currentCard = cards[currentIndex]

            // Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card ${currentIndex + 1} of ${cards.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (currentCard.isAiReinforcement) "30% Tool Challenge" else "70% User Field Note",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentCard.isAiReinforcement) TechGold else CyberCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Flashcard Box with Flip
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { isFlipped = !isFlipped }
                    .testTag("flashcard_flip_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFlipped) CardNavyElevated else CardNavy
                ),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isFlipped) TechEmerald.copy(alpha = 0.7f) else CyberCyan.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation <= 90f) {
                        // Front
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentCard.title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentCard.questionOrFront,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tap card to flip for answer",
                                fontSize = 11.sp,
                                color = CyberCyan.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        // Back (mirrored rotation)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.graphicsLayer { rotationY = 180f }
                        ) {
                            Text(
                                text = "ANSWER & SOLUTION",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TechEmerald,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currentCard.answerOrBack,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation & Mastery Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        isFlipped = false
                        if (currentIndex + 1 < cards.size) {
                            currentIndex += 1
                        } else {
                            isSessionDone = true
                            onCompleteSession(masteredInSession, cards.size)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Needs Practice")
                }

                Button(
                    onClick = {
                        isFlipped = false
                        masteredInSession += 1
                        if (currentIndex + 1 < cards.size) {
                            currentIndex += 1
                        } else {
                            isSessionDone = true
                            onCompleteSession(masteredInSession, cards.size)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = TechEmerald, contentColor = Color(0xFF0B0E17)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mastered!", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 3. ACTIVITY LOG TRACKER
// ==========================================

@Composable
private fun ActivityLogSection(
    activityLogs: List<ActivityLog>,
    onTriggerCalibration: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Adaptive Calibration Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavyElevated),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DYNAMIC AI CALIBRATION",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CyberCyan,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your past drill scores calibrate future worksheets and tests. Target weak spots with one tap.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onTriggerCalibration,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("calibrate_future_tests_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calibrate Next Drill", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "INTERACTION HISTORY & LOGS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }

        if (activityLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No activities recorded yet.\nTake a quiz, review 70/30 flashcards, or complete a worksheet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(activityLogs) { log ->
                val isCompleted = log.status.equals("Completed", ignoreCase = true)
                val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.US)
                val timeStr = sdf.format(Date(log.timestamp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCompleted) TechEmerald.copy(alpha = 0.4f) else TechGold.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = log.moduleType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                modifier = Modifier
                                    .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )

                            // Status badge: Completed (%) or Incomplete (4/10 steps)
                            Text(
                                text = log.displayStatus,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isCompleted) TechEmerald else TechGold,
                                modifier = Modifier
                                    .background(
                                        if (isCompleted) TechEmerald.copy(alpha = 0.15f) else TechGold.copy(alpha = 0.15f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = log.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)

                        if (log.summary.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = log.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (log.weakTopics.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Weak spots:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                log.weakTopics.forEach { topic ->
                                    Text(
                                        text = topic,
                                        fontSize = 10.sp,
                                        color = TechGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = timeStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. WORKSHEETS & DRILLS
// ==========================================

@Composable
private fun WorksheetsDrillsSection(
    onRecordActivity: (title: String, module: String, status: String, grade: Int?, comp: Int, tot: Int, weak: List<String>, sum: String) -> Unit
) {
    val worksheets = listOf(
        WorksheetItem(
            id = "ws_subnet",
            title = "Subnetting VLSM Fast-Calculation Worksheet",
            topic = "CCNA Routing",
            totalSteps = 5,
            description = "Calculate host ranges, prefix lengths, and broadcast boundaries for 5 enterprise scenarios."
        ),
        WorksheetItem(
            id = "ws_wireshark",
            title = "Wireshark Display Filter Syntax Drill",
            topic = "Packet Analysis",
            totalSteps = 6,
            description = "Construct exact capture and display filter strings for TCP resets, ARP poisoning, and TLS handshakes."
        ),
        WorksheetItem(
            id = "ws_nmap",
            title = "Nmap CLI Reconnaissance Worksheet",
            topic = "Cybersecurity",
            totalSteps = 5,
            description = "Identify stealth scan parameters, service fingerprint flags, and NSE scripts."
        ),
        WorksheetItem(
            id = "ws_powershell",
            title = "PowerShell Active Directory Automation Drill",
            topic = "Sysadmin",
            totalSteps = 4,
            description = "Formulate cmdlets to query locked AD user accounts and bulk reset credentials."
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "INTERACTIVE TECHNICAL WORKSHEETS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = CyberCyan,
                letterSpacing = 1.sp
            )
        }

        items(worksheets) { item ->
            var completedSteps by remember { mutableIntStateOf(0) }
            val isComplete = completedSteps >= item.totalSteps

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isComplete) TechEmerald.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.topic,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechGold,
                            modifier = Modifier
                                .background(TechGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Text(
                            text = if (isComplete) "Completed (100%)" else "Progress: $completedSteps/${item.totalSteps} steps",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isComplete) TechEmerald else CyberCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (completedSteps < item.totalSteps) {
                                    completedSteps += 1
                                    val isNowDone = completedSteps == item.totalSteps
                                    val grade = if (isNowDone) 100 else (completedSteps * 100 / item.totalSteps)
                                    onRecordActivity(
                                        item.title,
                                        "WORKSHEET",
                                        if (isNowDone) "Completed" else "Incomplete",
                                        grade,
                                        completedSteps,
                                        item.totalSteps,
                                        if (isNowDone) emptyList() else listOf(item.topic),
                                        "Completed $completedSteps/${item.totalSteps} steps on ${item.title}"
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isComplete) TechEmerald else CyberCyan,
                                contentColor = Color(0xFF0B0E17)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isComplete) "Drill Completed ✓" else "Complete Step (${completedSteps + 1}/${item.totalSteps})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

private data class WorksheetItem(
    val id: String,
    val title: String,
    val topic: String,
    val totalSteps: Int,
    val description: String
)

@Composable
private fun FullscreenFlashcardOverlay(
    cards: List<FlashcardItem>,
    onDismiss: () -> Unit,
    onCompleteSession: (completed: Int, total: Int) -> Unit
) {
    if (cards.isEmpty()) {
        onDismiss()
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var masteredCount by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        label = "fullscreen_card_rotation"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DeepNavy
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "70/30 Focus Drill",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = CyberCyan
                        )
                        Text(
                            text = "Card ${currentIndex + 1} of ${cards.size}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (currentIndex.toFloat() + 1f) / cards.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CyberCyan,
                    trackColor = CardNavy
                )

                Spacer(modifier = Modifier.height(30.dp))

                if (isFinished) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        colors = CardDefaults.cardColors(containerColor = CardNavyElevated),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechEmerald)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Focus Session Completed!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Mastered $masteredCount out of ${cards.size} flashcards. Recorded to your Activity Log.",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { onCompleteSession(masteredCount, cards.size) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("Finish & Save XP", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                } else {
                    val card = cards[currentIndex]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            }
                            .clickable { isFlipped = !isFlipped },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFlipped) CardNavyElevated else CardNavy
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isFlipped) TechEmerald.copy(alpha = 0.8f) else CyberCyan.copy(alpha = 0.6f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation <= 90f) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (card.isAiReinforcement) "30% TOOL CHALLENGE" else "70% USER NOTE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (card.isAiReinforcement) TechGold else CyberCyan,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = card.title,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = card.questionOrFront,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Tap to reveal answer",
                                        fontSize = 12.sp,
                                        color = CyberCyan.copy(alpha = 0.7f)
                                    )
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                                ) {
                                    Text(
                                        text = "ANSWER & SOLUTION",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TechEmerald,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = card.answerOrBack,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isFlipped = false
                                if (currentIndex + 1 < cards.size) {
                                    currentIndex += 1
                                } else {
                                    isFinished = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Needs Practice")
                        }

                        Button(
                            onClick = {
                                isFlipped = false
                                masteredCount += 1
                                if (currentIndex + 1 < cards.size) {
                                    currentIndex += 1
                                } else {
                                    isFinished = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TechEmerald, contentColor = Color(0xFF0B0E17)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mastered!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
