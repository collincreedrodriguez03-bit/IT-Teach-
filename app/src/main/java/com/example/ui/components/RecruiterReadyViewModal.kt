package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DictionaryTerm
import com.example.data.model.JournalEntry
import com.example.data.model.PortfolioProject
import com.example.data.model.UserProfile
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechGold
import com.example.ui.theme.TerminalBg

@Composable
fun RecruiterReadyViewModal(
    userProfile: UserProfile?,
    projects: List<PortfolioProject>,
    journalEntries: List<JournalEntry>,
    dictionaryTerms: List<DictionaryTerm>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val verifiedEntries = journalEntries.filter { it.isVerified }
    val masteredTerms = dictionaryTerms.filter { it.isMastered }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, bottom = 16.dp, start = 12.dp, end = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = DeepNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardNavy)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Executive Recruiter View",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Verified Engineering Dossier & Competency Record",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = {
                                val summaryText = buildRecruiterMarkdown(userProfile, projects, verifiedEntries, masteredTerms)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Recruiter Dossier", summaryText))
                                Toast.makeText(context, "Full Engineering Dossier copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_recruiter_dossier_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Dossier", tint = CyberCyan)
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Scrollable Dossier Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Candidate Executive Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardNavyElevated),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TechGold.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = userProfile?.displayName ?: "Senior IT Systems Engineer",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = userProfile?.rankTitle ?: "Network Infrastructure Specialist",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = CyberCyan
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .background(TechEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Lab-Verified", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TechEmerald)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    RecruiterMetric(label = "Case Studies", value = "${projects.size}")
                                    RecruiterMetric(label = "Verified Labs", value = "${verifiedEntries.size}")
                                    RecruiterMetric(label = "Mastered Terms", value = "${masteredTerms.size}")
                                    RecruiterMetric(label = "XP Points", value = "${userProfile?.xp ?: 0}")
                                }
                            }
                        }
                    }

                    // Section 1: Portfolio Case Studies
                    item {
                        Text(
                            text = "ENGINEERING CASE STUDIES & DEPLOYMENTS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    if (projects.isEmpty()) {
                        item {
                            Text(
                                text = "No published case studies yet. Promote verified lab entries or add new case studies.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(projects) { project ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardNavy),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = project.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = project.executiveSummary,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (project.toolsTechnologies.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            project.toolsTechnologies.take(4).forEach { tool ->
                                                Text(
                                                    text = tool,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CyberCyan,
                                                    modifier = Modifier
                                                        .background(CyberCyan.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (project.resultsMetrics.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Measurable Results:\n${project.resultsMetrics}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TechEmerald
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section 2: Verified Lab Logs
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "VERIFIED TROUBLESHOOTING & LAB LOGS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TechEmerald,
                            letterSpacing = 1.sp
                        )
                    }

                    if (verifiedEntries.isEmpty()) {
                        item {
                            Text(
                                text = "No verified lab entries. Mark solutions as verified in the Journal tab.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(verifiedEntries.take(4)) { entry ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CardNavy),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = entry.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = entry.category,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = CyberCyan
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = entry.solution,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                val summaryText = buildRecruiterMarkdown(userProfile, projects, verifiedEntries, masteredTerms)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Recruiter Dossier", summaryText))
                                Toast.makeText(context, "Dossier copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export & Copy Recruiter Dossier", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RecruiterMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = CyberCyan)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun buildRecruiterMarkdown(
    userProfile: UserProfile?,
    projects: List<PortfolioProject>,
    verifiedLabs: List<JournalEntry>,
    masteredTerms: List<DictionaryTerm>
): String {
    val sb = StringBuilder()
    sb.appendLine("# Engineering Technical Portfolio & Verification Dossier")
    sb.appendLine("**Candidate**: ${userProfile?.displayName ?: "Senior IT Systems Engineer"}")
    sb.appendLine("**Title**: ${userProfile?.rankTitle ?: "Network Infrastructure Specialist"}")
    sb.appendLine("**Verification Level**: Level ${userProfile?.level ?: 1} (${userProfile?.xp ?: 0} XP)")
    sb.appendLine()
    sb.appendLine("## Executive Summary")
    sb.appendLine("Demonstrated hands-on competency in Network Systems, CLI troubleshooting, and Infrastructure as Code.")
    sb.appendLine()
    sb.appendLine("## Key Case Studies (${projects.size})")
    projects.forEach { p ->
        sb.appendLine("### ${p.title}")
        sb.appendLine("* **Summary**: ${p.executiveSummary}")
        sb.appendLine("* **Technologies**: ${p.toolsTechnologies.joinToString(", ")}")
        sb.appendLine("* **Steps**: ${p.architectureSteps}")
        sb.appendLine("* **Results**: ${p.resultsMetrics}")
        sb.appendLine()
    }
    sb.appendLine("## Verified Lab Operations (${verifiedLabs.size})")
    verifiedLabs.take(5).forEach { l ->
        sb.appendLine("* **${l.title}** (${l.category}): `${l.solution}`")
    }
    sb.appendLine()
    sb.appendLine("## Mastered Technical Commands (${masteredTerms.size})")
    masteredTerms.take(8).forEach { m ->
        sb.appendLine("* `${m.command}`: ${m.description}")
    }
    return sb.toString()
}
