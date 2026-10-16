package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.TerminalBg

@Composable
fun RecruiterDossierDialog(
    userProfile: UserProfile?,
    portfolioProjects: List<PortfolioProject>,
    journalEntries: List<JournalEntry>,
    dictionaryTerms: List<DictionaryTerm>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val verifiedLabs = journalEntries.filter { it.isVerified }
    val masteredTerms = dictionaryTerms.filter { it.isMastered }

    val formattedMarkdown = buildString {
        appendLine("# PROFESSIONAL IT CANDIDATE DOSSIER")
        appendLine("### Target Track: ${userProfile?.rankTitle ?: "Junior Systems Administrator / Network Engineer"}")
        appendLine("- **Level:** Level ${userProfile?.level ?: 1} Engineer")
        appendLine("- **Active Daily Streak:** ${userProfile?.streakCount ?: 1} Days (Consistent Study & Hands-on Practice)")
        appendLine("- **Total Verified XP:** ${userProfile?.xp ?: 0} XP")
        appendLine("- **Verified Hands-on Lab Proofs:** ${verifiedLabs.size} Labs")
        appendLine("- **Mastered Technical Commands & Standards:** ${masteredTerms.size} Items")
        appendLine()
        appendLine("---")
        appendLine("## 🚀 PRODUCTION PORTFOLIO CASE STUDIES")
        if (portfolioProjects.isEmpty()) {
            appendLine("*(No public case studies added yet)*")
        } else {
            portfolioProjects.forEachIndexed { idx, p ->
                appendLine("### ${idx + 1}. ${p.title}")
                appendLine("- **Executive Summary:** ${p.executiveSummary}")
                appendLine("- **Business Problem:** ${p.problemBusinessNeed}")
                appendLine("- **Key Metrics / ROI:** ${p.resultsMetrics}")
                appendLine("- **Technologies Deployed:** ${p.toolsTechnologies.joinToString(", ")}")
                if (p.architectureSteps.isNotBlank()) {
                    appendLine("- **Architecture & Implementation:** ${p.architectureSteps}")
                }
                appendLine("- **Verified Evidence:** ${p.verifiedEvidence}")
                if (p.lessonsLearned.isNotBlank()) {
                    appendLine("- **Lessons Learned:** ${p.lessonsLearned}")
                }
                appendLine()
            }
        }
        appendLine("---")
        appendLine("## 🔬 VERIFIED HANDS-ON LAB JOURNALS")
        if (verifiedLabs.isEmpty()) {
            appendLine("*(No verified lab logs logged yet)*")
        } else {
            verifiedLabs.take(5).forEach { lab ->
                appendLine("- **[${lab.category}] ${lab.title}**")
                appendLine("  * *Investigation:* ${lab.notes}")
                appendLine("  * *Resolution / Proof:* ${lab.solution}")
                appendLine("  * *Skills Tagged:* ${lab.tags.joinToString(", ")}")
            }
        }
        appendLine()
        appendLine("---")
        appendLine("## 🛠️ MASTERED TECHNICAL COMMANDS & TOOLS")
        val sampleTools = masteredTerms.take(8).map { "${it.term} (${it.category})" }
        appendLine(sampleTools.ifEmpty { listOf("Wireshark", "Nmap", "Linux bash", "PowerShell", "CCNA VLANs") }.joinToString(" • "))
        appendLine()
        appendLine("---")
        appendLine("*Compiled from Network Teach verified engineering repository.*")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .fillMaxHeight(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(DeepNavy)
            .border(1.5.dp, CyberCyan, RoundedCornerShape(16.dp))
            .testTag("recruiter_dossier_modal"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recruiter Ready Dossier", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header callout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardNavyElevated)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Clean, professional Markdown output formatted for LinkedIn, GitHub README, or sending directly to hiring managers.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Markdown preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = TerminalBg),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = formattedMarkdown,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = CyberCyan,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, formattedMarkdown)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Candidate Dossier"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_dossier_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("RecruiterDossier", formattedMarkdown))
                        Toast.makeText(context, "Dossier copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_dossier_clipboard_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Markdown", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {}
    )
}
