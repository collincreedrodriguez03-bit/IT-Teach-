package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DictionaryTerm
import com.example.data.model.JournalEntry
import com.example.data.model.PortfolioProject
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
fun DashboardScreen(
    userProfile: UserProfile?,
    journalEntries: List<JournalEntry>,
    portfolioProjects: List<PortfolioProject>,
    dictionaryTerms: List<DictionaryTerm>,
    quests: List<Quest>,
    onNavigateTab: (Int) -> Unit,
    onOpenMentor: () -> Unit,
    onStartDailyQuiz: (Quest) -> Unit
) {
    val verifiedLabsCount = journalEntries.count { it.isVerified }
    val masteredDictCount = dictionaryTerms.count { it.isMastered }
    val totalTerms = dictionaryTerms.size
    val totalProjects = portfolioProjects.size

    // Calculate dynamic readiness score
    val readinessScore = when {
        totalTerms == 0 -> 15
        else -> {
            val termWeight = (masteredDictCount.toFloat() / maxOf(1, totalTerms) * 40f).toInt()
            val labWeight = (minOf(10, verifiedLabsCount) * 3)
            val projectWeight = (minOf(5, totalProjects) * 6)
            minOf(99, 15 + termWeight + labWeight + projectWeight)
        }
    }

    val dailyQuest = quests.firstOrNull { it.questType == "DAILY" && !it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Gamified XP & Streak Header
        item {
            GamifiedHeaderCard(
                userProfile = userProfile,
                onOpenMentor = onOpenMentor
            )
        }

        // Welcome Banner (from Video)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Welcome Back, ${userProfile?.displayName ?: "IT Engineer"}!",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Document your daily labs, build an employer-ready portfolio case study archive, and master technical terms with 80% enhanced gamified quests.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Active Daily Quest Callout
        if (dailyQuest != null) {
            item {
                DailyChallengeBanner(
                    quest = dailyQuest,
                    onStartQuiz = { onStartDailyQuiz(dailyQuest) }
                )
            }
        }

        // Professional Progress Overview Header
        item {
            Text(
                text = "Professional Progress Overview",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 2x2 Overview Metrics Grid (matching video layout with enhanced UI)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Journal Labs",
                        count = "${journalEntries.size}",
                        subtitle = "$verifiedLabsCount verified",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        iconTint = CyberCyan
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Case Studies",
                        count = "$totalProjects",
                        subtitle = if (totalProjects > 0) "Employer Ready" else "Ready for Review",
                        icon = Icons.Default.Work,
                        iconTint = TechPurple
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Dictionary Terms",
                        count = "$totalTerms",
                        subtitle = "$masteredDictCount mastered",
                        icon = Icons.Default.Book,
                        iconTint = TechEmerald
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Readiness Score",
                        count = "$readinessScore%",
                        subtitle = "Knowledge Index",
                        icon = Icons.Default.CheckCircle,
                        iconTint = TechGold
                    )
                }
            }
        }

        // Visual Growth & Study Consistency Chart
        item {
            GrowthConsistencyCard(
                userProfile = userProfile,
                verifiedLabsCount = verifiedLabsCount
            )
        }

        // Quick Navigation Buttons (from Video)
        item {
            Column {
                Text(
                    text = "Quick Navigation",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onNavigateTab(2) }, // Journal
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_journal_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Open Journal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { onNavigateTab(4) }, // Portfolio
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_portfolio_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Portfolio", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { onNavigateTab(3) }, // Dictionary
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_dictionary_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TechEmerald)
                    ) {
                        Text("Dictionary", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Recent Journal Labs section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Journal Labs",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "View All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyberCyan,
                    modifier = Modifier
                        .clickable { onNavigateTab(2) }
                        .padding(4.dp)
                )
            }
        }

        if (journalEntries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No recent labs recorded",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { onNavigateTab(2) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ Log Your First Lab", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(journalEntries.take(3)) { entry ->
                RecentLabItem(entry = entry, onClick = { onNavigateTab(2) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GamifiedHeaderCard(
    userProfile: UserProfile?,
    onOpenMentor: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavyElevated),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(TechGold.copy(alpha = 0.15f))
                            .border(1.5.dp, TechGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "L${userProfile?.level ?: 1}",
                            fontWeight = FontWeight.ExtraBold,
                            color = TechGold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = userProfile?.rankTitle ?: "Script Apprentice",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${userProfile?.xp ?: 0} Total XP",
                            fontSize = 12.sp,
                            color = CyberCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Streak Pill
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF2E1905), RoundedCornerShape(20.dp))
                            .border(1.dp, TechGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = TechGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${userProfile?.streakCount ?: 1}d",
                            fontWeight = FontWeight.Bold,
                            color = TechGold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // AI Architect Mentor Button
                    IconButton(
                        onClick = onOpenMentor,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.15f))
                            .testTag("mentor_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Mentor",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Level Progress Bar
            val progress = userProfile?.progressFraction ?: 0.2f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleCornerShape()),
                color = TechGold,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current: ${userProfile?.xp ?: 0} XP",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Next Rank: ${userProfile?.xpForNextLevel ?: 250} XP",
                    fontSize = 11.sp,
                    color = TechGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = count,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = iconTint,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DailyChallengeBanner(
    quest: Quest,
    onStartQuiz: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF142436)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = CyberCyan)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Challenge Active",
                    fontSize = 11.sp,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = quest.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "+${quest.xpReward} XP upon passing adaptive quiz",
                    fontSize = 11.sp,
                    color = TechGold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onStartQuiz,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Quiz", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun GrowthConsistencyCard(
    userProfile: UserProfile?,
    verifiedLabsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Growth & Study Consistency",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Last 7 Days",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Bar Chart of Study Consistency
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val barWidth = size.width / (days.size * 2f)
                    val maxVal = 100f
                    val currentDayIndex = 3
                    val sampleScores = listOf(40f, 65f, 50f, 85f, 70f, 95f, 60f)

                    for (i in days.indices) {
                        val x = (i * 2 + 1) * barWidth
                        val barHeight = (sampleScores[i] / maxVal) * (size.height - 20.dp.toPx())
                        val top = size.height - 20.dp.toPx() - barHeight
                        val isToday = i == currentDayIndex

                        drawRoundRect(
                            color = if (isToday) CyberCyan else CardNavyElevated,
                            topLeft = Offset(x - barWidth / 2f, top),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                for (d in days) {
                    Text(
                        text = d,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentLabItem(
    entry: JournalEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        modifier = Modifier
                            .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    if (entry.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Verified",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechEmerald,
                            modifier = Modifier
                                .background(TechEmerald.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = entry.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (entry.solution.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.solution,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF4ADE80),
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun CircleCornerShape() = RoundedCornerShape(50)
