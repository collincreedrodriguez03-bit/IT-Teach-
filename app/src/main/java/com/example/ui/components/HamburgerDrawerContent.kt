package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityLog
import com.example.data.model.CareerPosition
import com.example.data.model.CareerRoadmapCatalog
import com.example.data.model.DictionaryTerm
import com.example.data.model.JournalEntry
import com.example.data.model.PortfolioProject
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

@Composable
fun HamburgerDrawerContent(
    userProfile: UserProfile?,
    journalEntries: List<JournalEntry>,
    portfolioProjects: List<PortfolioProject>,
    dictionaryTerms: List<DictionaryTerm>,
    quests: List<Quest>,
    activityLogs: List<ActivityLog>,
    streakMultiplier: Float,
    onNavigateTab: (Int) -> Unit,
    onOpenMentor: () -> Unit,
    onOpenRecruiterDossier: () -> Unit,
    onLockApp: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current
    var drawerSubTab by remember { mutableIntStateOf(0) } // 0: KPI Metrics, 1: Career Roadmap

    val verifiedLabsCount = journalEntries.count { it.isVerified }
    val masteredDictCount = dictionaryTerms.count { it.isMastered }
    val totalTerms = dictionaryTerms.size
    val totalProjects = portfolioProjects.size

    val readinessScore = when {
        totalTerms == 0 -> 18
        else -> {
            val termWeight = (masteredDictCount.toFloat() / maxOf(1, totalTerms) * 40f).toInt()
            val labWeight = (minOf(10, verifiedLabsCount) * 3)
            val projectWeight = (minOf(5, totalProjects) * 6)
            minOf(99, 18 + termWeight + labWeight + projectWeight)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(340.dp)
            .background(DeepNavy)
            .testTag("hamburger_drawer_content")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Engineer Identity & Level
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardNavy)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan.copy(alpha = 0.2f))
                                    .border(1.5.dp, CyberCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Engineer",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = userProfile?.rankTitle ?: "Junior Sysadmin",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Level ${userProfile?.level ?: 1} Engineer",
                                    fontSize = 12.sp,
                                    color = TechGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Close button
                        IconButton(
                            onClick = onCloseDrawer,
                            modifier = Modifier.testTag("close_drawer_button")
                        ) {
                            Text("✕", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Capped Streak Multiplier Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF261905))
                            .border(1.dp, TechGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = TechGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "${userProfile?.streakCount ?: 1} Day Streak Active",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TechGold
                                )
                                Text(
                                    text = if (streakMultiplier >= 2.5f) "Max Ceiling Multiplier Reached" else "+0.05x/day, capped at 2.5x",
                                    fontSize = 10.sp,
                                    color = Color(0xFFD4A373)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TechGold)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${String.format("%.2f", streakMultiplier)}x XP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1100)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // XP Progress bar
                    val currentXp = userProfile?.xp ?: 0L
                    val nextLevelXp = (userProfile?.level ?: 1) * 350L
                    val progress = (currentXp % 350L).toFloat() / 350f
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Progression XP", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$currentXp / $nextLevelXp XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = CyberCyan,
                            trackColor = CardNavyElevated
                        )
                    }
                }
            }

            // Tab switch between KPI Metrics and Career Roadmap
            TabRow(
                selectedTabIndex = drawerSubTab,
                containerColor = CardNavyElevated,
                contentColor = CyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[drawerSubTab]),
                        color = CyberCyan
                    )
                }
            ) {
                Tab(
                    selected = drawerSubTab == 0,
                    onClick = { drawerSubTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("KPI Dashboard", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("drawer_tab_kpi")
                )
                Tab(
                    selected = drawerSubTab == 1,
                    onClick = { drawerSubTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MilitaryTech, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Career Roadmap", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("drawer_tab_roadmap")
                )
            }

            // Scrollable Content
            Box(modifier = Modifier.weight(1f)) {
                if (drawerSubTab == 0) {
                    // KPI Metrics & Shortcuts
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Consistency Graph
                        item {
                            ConsistencyGraphCard(
                                streakCount = userProfile?.streakCount ?: 1,
                                verifiedLabs = verifiedLabsCount
                            )
                        }

                        // Readiness Score Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardNavy),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("IT Readiness Index", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$readinessScore%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TechGold)
                                        Text("Calculated from labs, terms & projects", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(TechGold.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TechGold, modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        }

                        // Grid of KPI counts
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    DrawerKpiTile(
                                        modifier = Modifier.weight(1f),
                                        title = "Dictionary",
                                        value = "$totalTerms",
                                        subtitle = "$masteredDictCount mastered",
                                        icon = Icons.Default.Book,
                                        tint = TechEmerald,
                                        onClick = {
                                            onCloseDrawer()
                                            onNavigateTab(3) // Dictionary
                                        }
                                    )
                                    DrawerKpiTile(
                                        modifier = Modifier.weight(1f),
                                        title = "Lab Journals",
                                        value = "${journalEntries.size}",
                                        subtitle = "$verifiedLabsCount verified",
                                        icon = Icons.AutoMirrored.Filled.Assignment,
                                        tint = CyberCyan,
                                        onClick = {
                                            onCloseDrawer()
                                            onNavigateTab(1) // Journal
                                        }
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    DrawerKpiTile(
                                        modifier = Modifier.weight(1f),
                                        title = "Portfolio",
                                        value = "$totalProjects",
                                        subtitle = "Case studies",
                                        icon = Icons.Default.Work,
                                        tint = TechPurple,
                                        onClick = {
                                            onCloseDrawer()
                                            onNavigateTab(2) // Portfolio
                                        }
                                    )
                                    DrawerKpiTile(
                                        modifier = Modifier.weight(1f),
                                        title = "Activity Logs",
                                        value = "${activityLogs.size}",
                                        subtitle = "AI & lab trials",
                                        icon = Icons.Default.Bolt,
                                        tint = TechGold,
                                        onClick = {
                                            onCloseDrawer()
                                            onNavigateTab(0) // Learn
                                        }
                                    )
                                }
                            }
                        }

                        // Quick Actions & Recruiter Export
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Quick Tools & Career Actions",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Button(
                                    onClick = {
                                        onCloseDrawer()
                                        onOpenRecruiterDossier()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("drawer_recruiter_export_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Recruiter Ready Export", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        onCloseDrawer()
                                        onOpenMentor()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("drawer_ai_mentor_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("AI Architect Mentor", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        onCloseDrawer()
                                        onLockApp()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("drawer_pin_lock_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Lock App (Security PIN)", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    // Career Roadmap: 10 IT Job Positions & Micro-Badges
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardNavy),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Omni-Domain IT Career Track",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberCyan
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "From Helpdesk to Vanguard Architect across Python, Windows, Linux, Termux, PowerShell, ~30 IT tools, CCNA & CompTIA.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        items(CareerRoadmapCatalog.positions, key = { it.level }) { pos ->
                            CareerPositionCard(
                                position = pos,
                                currentLevel = userProfile?.level ?: 1,
                                userXp = userProfile?.xp ?: 0L,
                                verifiedLabs = verifiedLabsCount
                            )
                        }
                    }
                }
            }

            // Bottom drawer footer: Offline status
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TechEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Offline-First SQLite Active", fontSize = 11.sp, color = TechEmerald, fontWeight = FontWeight.SemiBold)
                }
                Text("v2.4", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DrawerKpiTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 10.sp, color = tint)
        }
    }
}

@Composable
private fun ConsistencyGraphCard(
    streakCount: Long,
    verifiedLabs: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Study Consistency",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$streakCount Days Active",
                    fontSize = 11.sp,
                    color = TechGold,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Line/Area Graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // 7 points of historical study activity
                    val points = listOf(
                        0.35f, 0.45f, 0.30f, 0.65f, 0.55f, 0.85f, 1.0f
                    )

                    val path = Path()
                    val fillPath = Path()
                    val step = w / (points.size - 1)

                    points.forEachIndexed { i, p ->
                        val x = i * step
                        val y = h - (p * (h - 10f)) - 5f
                        if (i == 0) {
                            path.moveTo(x, y)
                            fillPath.moveTo(x, h)
                            fillPath.lineTo(x, y)
                        } else {
                            path.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                    }

                    fillPath.lineTo(w, h)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(CyberCyan.copy(alpha = 0.25f), Color.Transparent),
                            startY = 0f,
                            endY = h
                        )
                    )

                    drawPath(
                        path = path,
                        color = CyberCyan,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    points.forEachIndexed { i, p ->
                        val x = i * step
                        val y = h - (p * (h - 10f)) - 5f
                        drawCircle(color = CyberCyan, radius = 3.dp.toPx(), center = Offset(x, y))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("M", "T", "W", "T", "F", "S", "Today").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 10.sp,
                        color = if (day == "Today") CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (day == "Today") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun CareerPositionCard(
    position: CareerPosition,
    currentLevel: Long,
    userXp: Long,
    verifiedLabs: Int
) {
    val isUnlocked = currentLevel >= position.level
    val isCurrent = currentLevel == position.level.toLong()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) CardNavyElevated else CardNavy
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCurrent) CyberCyan else if (isUnlocked) TechEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrent) CyberCyan else if (isUnlocked) TechEmerald else CardNavyElevated
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${position.level}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color(0xFF0B0E17) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = position.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = position.salaryRange,
                            fontSize = 10.sp,
                            color = if (isUnlocked) TechGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isCurrent) {
                    Text(
                        text = "CURRENT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        modifier = Modifier
                            .background(CyberCyan.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                } else if (!isUnlocked) {
                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                } else {
                    Icon(Icons.Default.Verified, contentDescription = "Mastered", tint = TechEmerald, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = position.summary,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Primary domains & Core tools
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                position.primaryDomains.take(3).forEach { dom ->
                    Text(
                        text = dom,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TechPurple,
                        modifier = Modifier
                            .background(TechPurple.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Key Tools: " + position.coreTools.joinToString(", "),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = CyberCyan.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3 Micro-Badges
            Text(
                text = "Micro-Credentials & Badges:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                position.microBadges.forEach { badge ->
                    val badgeEarned = isUnlocked || (badge.requiredXp <= userXp && verifiedLabs >= badge.requiredLabs)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (badgeEarned) CardNavyElevated else TerminalBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(badge.iconEmoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = badge.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (badgeEarned) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = badge.criteria,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (badgeEarned) {
                            Text("EARNED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TechEmerald)
                        } else {
                            Text("REQ: ${badge.requiredXp} XP", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
