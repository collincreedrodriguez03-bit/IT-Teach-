package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Terminal
import com.example.ui.components.EditDictionaryDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DictionaryTerm
import com.example.data.model.UserProfile
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechGold
import com.example.ui.theme.TechPurple
import com.example.ui.theme.TerminalBg

@Composable
fun DictionaryScreen(
    userProfile: UserProfile?,
    terms: List<DictionaryTerm>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    isFlashcardMode: Boolean,
    onToggleFlashcardMode: () -> Unit,
    onAddTerm: (term: String, cmd: String, desc: String, cat: String, diff: String, aiCheck: Boolean) -> Unit,
    onUpdateTerm: (DictionaryTerm) -> Unit = {},
    onToggleMastery: (DictionaryTerm) -> Unit,
    onDeleteTerm: (String) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTerm by remember { mutableStateOf<DictionaryTerm?>(null) }

    val categories = listOf("All", "Linux", "Networking", "Security", "Cloud/DevOps", "Python", "Troubleshooting")

    val filteredTerms = terms.filter { term ->
        val matchesCategory = (selectedCategory == "All" || term.category.equals(selectedCategory, ignoreCase = true))
        val matchesSearch = searchQuery.isBlank() ||
                term.term.contains(searchQuery, ignoreCase = true) ||
                term.command.contains(searchQuery, ignoreCase = true) ||
                term.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val userLevel = userProfile?.level ?: 1L

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CyberCyan,
                contentColor = Color(0xFF0B0E17),
                modifier = Modifier.testTag("add_dictionary_term_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Command")
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
            // Header with Flashcard Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "IT Dictionary & Flashcards",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "95% Personal Knowledge Base + 5% Progression Blueprints",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onToggleFlashcardMode,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isFlashcardMode) CyberCyan.copy(alpha = 0.2f) else CardNavy)
                        .border(1.dp, if (isFlashcardMode) CyberCyan else MaterialTheme.colorScheme.outline, CircleShape)
                        .testTag("toggle_flashcard_mode_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = "Flashcard Mode",
                        tint = if (isFlashcardMode) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search Bar (Matching Video)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dictionary_search_input"),
                placeholder = {
                    Text(
                        "Search terms, commands, definitions...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardNavy,
                    unfocusedContainerColor = CardNavy,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips (Matching Video)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (cat in categories) {
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) CyberCyan else CardNavy)
                            .border(1.dp, if (isSelected) CyberCyan else MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            .clickable { onSelectCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("filter_chip_$cat")
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF0B0E17) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isFlashcardMode) {
                // Interactive Flashcard Mode
                FlashcardModeView(
                    terms = filteredTerms,
                    onToggleMastery = onToggleMastery
                )
            } else {
                // Standard Command Center List (Matching Video)
                if (filteredTerms.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Terminal,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No commands found in $selectedCategory",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredTerms, key = { it.id }) { term ->
                            DictionaryItemCard(
                                term = term,
                                userLevel = userLevel,
                                onCopy = { cmd ->
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Command", cmd))
                                    Toast.makeText(context, "Copied: $cmd", Toast.LENGTH_SHORT).show()
                                },
                                onEdit = { editingTerm = term },
                                onToggleMastery = { onToggleMastery(term) },
                                onDelete = { onDeleteTerm(term.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (editingTerm != null) {
        EditDictionaryDialog(
            term = editingTerm!!,
            onDismiss = { editingTerm = null },
            onSave = { updated ->
                onUpdateTerm(updated)
                editingTerm = null
            }
        )
    }

    if (showAddDialog) {
        AddDictionaryTermDialog(
            initialCategory = if (selectedCategory != "All") selectedCategory else "Networking",
            onDismiss = { showAddDialog = false },
            onAdd = { term, cmd, desc, cat, diff, aiCheck ->
                onAddTerm(term, cmd, desc, cat, diff, aiCheck)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun DictionaryItemCard(
    term: DictionaryTerm,
    userLevel: Long,
    onCopy: (String) -> Unit,
    onEdit: () -> Unit,
    onToggleMastery: () -> Unit,
    onDelete: () -> Unit
) {
    val isLockedPro = term.isAiGenerated && userLevel < 3

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (term.isMastered) TechEmerald.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges & Delete Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Category pill
                    Text(
                        text = term.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechPurple,
                        modifier = Modifier
                            .background(TechPurple.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                    // Difficulty pill
                    Text(
                        text = term.difficulty,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .background(CardNavyElevated, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                    if (term.isAiGenerated) {
                        Text(
                            text = if (isLockedPro) "Locked Pro" else "5% Pro Blueprint",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechGold,
                            modifier = Modifier
                                .background(TechGold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp).testTag("edit_term_button_${term.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Term",
                            tint = CyberCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).testTag("delete_term_button_${term.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isLockedPro) {
                // Locked Pro State
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF261905), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TechGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Locked Progression Blueprint", color = TechGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Reach Level 3 (Junior Sysadmin) to unlock this advanced definition.", color = Color(0xFFD4A373), fontSize = 11.sp)
                    }
                }
            } else {
                // Title / Terminal identifier
                Text(
                    text = term.term,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description (User's personal input)
                Text(
                    text = term.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                // Command box
                if (term.command.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TerminalBg)
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .clickable { onCopy(term.command) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cmd : ${term.command}",
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Command",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // AI Clarification Pill (Minor correction / syntax alert)
                if (term.aiExplanation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberCyan.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Clarifier",
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = term.aiExplanation,
                            fontSize = 11.sp,
                            color = CyberCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mastered Toggle (Matching Video)
                Row(
                    modifier = Modifier
                        .clickable { onToggleMastery() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (term.isMastered) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (term.isMastered) "Mastered" else "Mark Mastered",
                        tint = if (term.isMastered) TechEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (term.isMastered) "Mastered" else "Mark Mastered",
                        fontSize = 13.sp,
                        fontWeight = if (term.isMastered) FontWeight.Bold else FontWeight.Medium,
                        color = if (term.isMastered) TechEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardModeView(
    terms: List<DictionaryTerm>,
    onToggleMastery: (DictionaryTerm) -> Unit
) {
    if (terms.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No flashcards available in this category.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val currentTerm = terms.getOrNull(currentIndex % terms.size) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Flashcard ${currentIndex + 1} of ${terms.size}",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Flippable Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clickable { isFlipped = !isFlipped }
                .testTag("flashcard_card"),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCyan)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isFlipped) {
                    // Front side: Command / Term
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentTerm.category.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechPurple
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentTerm.command.ifBlank { currentTerm.term },
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Tap to reveal technical definition",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Back side: Description & explanation
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DEFINITION & NOTES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechEmerald
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentTerm.description,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        if (currentTerm.aiExplanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Pro Tip: ${currentTerm.aiExplanation}",
                                fontSize = 12.sp,
                                color = TechGold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation & Mastery Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    isFlipped = false
                    currentIndex = if (currentIndex > 0) currentIndex - 1 else terms.size - 1
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Previous")
            }

            Button(
                onClick = { onToggleMastery(currentTerm) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentTerm.isMastered) TechEmerald else CardNavyElevated,
                    contentColor = if (currentTerm.isMastered) Color(0xFF0B0E17) else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (currentTerm.isMastered) "Mastered ✓" else "Mark Mastered")
            }

            OutlinedButton(
                onClick = {
                    isFlipped = false
                    currentIndex = (currentIndex + 1) % terms.size
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
private fun AddDictionaryTermDialog(
    initialCategory: String,
    onDismiss: () -> Unit,
    onAdd: (term: String, cmd: String, desc: String, cat: String, diff: String, aiCheck: Boolean) -> Unit
) {
    var term by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(initialCategory) }
    var difficulty by remember { mutableStateOf("Beginner") }
    var requestAiCheck by remember { mutableStateOf(true) }

    val categories = listOf("Networking", "Linux", "Security", "Cloud/DevOps", "Python", "Troubleshooting")
    val difficulties = listOf("Beginner", "Intermediate", "Advanced")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "Add Technical Command / Term",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = term,
                    onValueChange = { term = it },
                    label = { Text("Utility / Title (e.g. cmd, termux, nmap)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    label = { Text("Command Syntax (e.g. ipconfig /all)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Your Notes / Purpose (95% User Owned)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Text("Category:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (c in categories) {
                        val isSel = category == c
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CyberCyan else CardNavyElevated)
                                .clickable { category = c }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = c,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFF0B0E17) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = requestAiCheck,
                        onCheckedChange = { requestAiCheck = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Enable AI Clarifier (syntax checks & flags)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (term.isNotBlank() || command.isNotBlank()) {
                        onAdd(term.ifBlank { "cmd" }, command, description, category, difficulty, requestAiCheck)
                    }
                },
                enabled = term.isNotBlank() || command.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17))
            ) {
                Text("Save Command")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
