package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import com.example.ui.components.EditPortfolioDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PortfolioProject
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechGold
import com.example.ui.theme.TechPurple
import com.example.ui.theme.TerminalBg

@Composable
fun PortfolioScreen(
    projects: List<PortfolioProject>,
    isEmployerPreview: Boolean,
    onToggleEmployerPreview: () -> Unit,
    onAddProject: (PortfolioProject) -> Unit,
    onUpdateProject: (PortfolioProject) -> Unit = {},
    onToggleMastered: (PortfolioProject) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<PortfolioProject?>(null) }

    Scaffold(
        floatingActionButton = {
            if (!isEmployerPreview) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CyberCyan,
                    contentColor = Color(0xFF0B0E17),
                    modifier = Modifier.testTag("add_portfolio_project_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Case Study")
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Header with Employer View Eye Toggle (matching video)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isEmployerPreview) "Employer Showcase View" else "Professional Portfolio",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isEmployerPreview) "Production architectures, metrics & verified evidence" else "Verified IT projects & infrastructure deployments",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onToggleEmployerPreview,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isEmployerPreview) CyberCyan.copy(alpha = 0.2f) else CardNavy)
                        .border(1.dp, if (isEmployerPreview) CyberCyan else MaterialTheme.colorScheme.outline, CircleShape)
                        .testTag("toggle_employer_preview_button")
                ) {
                    Icon(
                        imageVector = if (isEmployerPreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Employer Preview Toggle",
                        tint = if (isEmployerPreview) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isEmployerPreview) {
                // Employer Banner with Copy Markdown Export
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2338)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Employer Ready Mode Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                            Text("Presents your case studies with executive ROI, metrics & verified proofs.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {
                            val fullSummary = buildString {
                                appendLine("# IT & Network Engineering Case Studies")
                                for (p in projects) {
                                    appendLine("\n## ${p.title}")
                                    appendLine("**Summary:** ${p.executiveSummary}")
                                    appendLine("**Need:** ${p.problemBusinessNeed}")
                                    appendLine("**Tech:** ${p.toolsTechnologies.joinToString(", ")}")
                                    appendLine("**Metrics:** ${p.resultsMetrics}")
                                    appendLine("**Evidence:** ${p.verifiedEvidence}")
                                }
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("CaseStudies", fullSummary))
                            Toast.makeText(context, "Full portfolio copied to clipboard for resume!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Markdown", tint = CyberCyan)
                        }
                    }
                }
            }

            if (projects.isEmpty()) {
                // Empty state matching video
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.BusinessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No portfolio case studies yet",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add your verified IT projects and infrastructure deployments.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(projects, key = { it.id }) { project ->
                        CaseStudyCard(
                            project = project,
                            isEmployerPreview = isEmployerPreview,
                            onEdit = { editingProject = project },
                            onToggleMastered = { onToggleMastered(project) },
                            onDelete = { onDeleteProject(project.id) },
                            onShare = {
                                val text = "${project.title}\n${project.executiveSummary}\nTech Stack: ${project.toolsTechnologies.joinToString(", ")}\nResults: ${project.resultsMetrics}"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("CaseStudy", text))
                                Toast.makeText(context, "Case study copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (editingProject != null) {
        EditPortfolioDialog(
            project = editingProject!!,
            onDismiss = { editingProject = null },
            onSave = { updated ->
                onUpdateProject(updated)
                editingProject = null
            }
        )
    }

    if (showAddDialog) {
        NewPortfolioCaseStudyDialog(
            onDismiss = { showAddDialog = false },
            onSave = { project ->
                onAddProject(project)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CaseStudyCard(
    project: PortfolioProject,
    isEmployerPreview: Boolean,
    onEdit: () -> Unit,
    onToggleMastered: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (project.isMastered) TechGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (project.isMastered) "Mastered Project" else "Verified Case Study",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (project.isMastered) TechGold else TechEmerald,
                        modifier = Modifier
                            .background(
                                if (project.isMastered) TechGold.copy(alpha = 0.15f) else TechEmerald.copy(alpha = 0.15f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row {
                    IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = CyberCyan, modifier = Modifier.size(18.dp))
                    }
                    if (!isEmployerPreview) {
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Tech Stack Pills
            if (project.toolsTechnologies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (tool in project.toolsTechnologies) {
                        Text(
                            text = tool,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberCyan,
                            modifier = Modifier
                                .background(CardNavyElevated, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Executive Summary
            CaseStudySection(label = "EXECUTIVE SUMMARY", content = project.executiveSummary, color = MaterialTheme.colorScheme.onSurface)

            if (project.problemBusinessNeed.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                CaseStudySection(label = "THE PROBLEM / BUSINESS NEED", content = project.problemBusinessNeed, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (project.architectureSteps.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                CaseStudySection(label = "STEPS TAKEN / ARCHITECTURE", content = project.architectureSteps, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (project.resultsMetrics.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                CaseStudySection(label = "RESULTS & METRICS (ROI, UPTIME)", content = project.resultsMetrics, color = TechEmerald)
            }

            if (project.verifiedEvidence.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TerminalBg)
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text("VERIFIED EVIDENCE (LOGS / COMMANDS):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TechPurple, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(project.verifiedEvidence, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF4ADE80))
                }
            }

            if (project.lessonsLearned.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                CaseStudySection(label = "LESSONS LEARNED", content = project.lessonsLearned, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mastery button
            Row(
                modifier = Modifier
                    .clickable { onToggleMastered() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (project.isMastered) Icons.Default.Star else Icons.Default.Star,
                    contentDescription = "Mastered",
                    tint = if (project.isMastered) TechGold else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (project.isMastered) "Mastered Case Study ⭐" else "Mark Mastered (+50 XP)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (project.isMastered) TechGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CaseStudySection(label: String, content: String, color: Color) {
    Column {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = content, fontSize = 12.sp, color = color, lineHeight = 17.sp)
    }
}

@Composable
private fun NewPortfolioCaseStudyDialog(
    onDismiss: () -> Unit,
    onSave: (PortfolioProject) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var tools by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var metrics by remember { mutableStateOf("") }
    var evidence by remember { mutableStateOf("") }
    var lessons by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "New Portfolio Case Study",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Executive Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                OutlinedTextField(
                    value = problem,
                    onValueChange = { problem = it },
                    label = { Text("The Problem / Business Need") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                OutlinedTextField(
                    value = tools,
                    onValueChange = { tools = it },
                    label = { Text("Tools & Technologies (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("Steps Taken / Architecture") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = metrics,
                    onValueChange = { metrics = it },
                    label = { Text("Results & Metrics (ROI, Uptime)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = evidence,
                    onValueChange = { evidence = it },
                    label = { Text("Verified Evidence (Logs, Commands)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                OutlinedTextField(
                    value = lessons,
                    onValueChange = { lessons = it },
                    label = { Text("Lessons Learned") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val techList = tools.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        onSave(
                            PortfolioProject(
                                title = title,
                                executiveSummary = summary,
                                problemBusinessNeed = problem,
                                toolsTechnologies = techList,
                                architectureSteps = steps,
                                resultsMetrics = metrics,
                                verifiedEvidence = evidence,
                                lessonsLearned = lessons,
                                isVerified = true,
                                isMastered = false
                            )
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17))
            ) {
                Text("Save Case Study")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
