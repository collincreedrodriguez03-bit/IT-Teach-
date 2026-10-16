package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechGold
import com.example.ui.theme.TechPurple

data class SurveyResult(
    val category: String,
    val subRelation: String,
    val certTarget: String,
    val preliminaryTitle: String,
    val skipSurveyPermanently: Boolean
)

@Composable
fun SurveyEntryDialog(
    targetType: String, // "DICTIONARY" or "JOURNAL"
    existingItemTitles: List<String>,
    initialCategory: String = "Networking",
    onDismiss: () -> Unit,
    onProceedToEditor: (SurveyResult) -> Unit
) {
    var titleInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var customCategory by remember { mutableStateOf("") }
    var isCustomCategoryMode by remember { mutableStateOf(false) }

    var selectedSubRelation by remember { mutableStateOf("Tools & CLI") }
    var selectedCertTarget by remember { mutableStateOf("CompTIA Network+") }
    var skipSurveyFuture by remember { mutableStateOf(false) }

    val presetCategories = listOf(
        "Networking", "Security", "Linux", "Windows", "Python", "Termux", "PowerShell", "Cloud", "Custom+"
    )
    val subRelations = listOf(
        "Tools & CLI", "Operating System", "Protocols & RFCs", "Troubleshooting Lab", "Architecture / Hardware"
    )
    val certTargets = listOf(
        "CompTIA A+", "CompTIA Network+", "CompTIA Security+", "Cisco CCNA", "CEH / Pentest+", "General IT Knowledge"
    )

    // Smart Duplicate Detection
    val duplicateFound = titleInput.isNotBlank() && existingItemTitles.any {
        it.equals(titleInput.trim(), ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (targetType == "DICTIONARY") Icons.Default.Book else Icons.Default.Assignment,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (targetType == "DICTIONARY") "Survey: New Command Entry" else "Survey: New Lab Note",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Classify this record into your engineering graph before drafting:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Preliminary Item Name with Duplicate Detection
                Text(
                    text = if (targetType == "DICTIONARY") "Command or Term Name:" else "Lab or Issue Subject:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    placeholder = {
                        Text(
                            if (targetType == "DICTIONARY") "e.g., nmap, traceroute, iptables" else "e.g., VLAN Trunking failure on Switch 2",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("survey_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy,
                        focusedBorderColor = if (duplicateFound) TechGold else CyberCyan,
                        unfocusedBorderColor = if (duplicateFound) TechGold.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline
                    )
                )

                // Duplicate Alert Warning
                if (duplicateFound) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF332005), RoundedCornerShape(8.dp))
                            .border(1.dp, TechGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WarningAmber,
                            contentDescription = "Duplicate Warning",
                            tint = TechGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Duplicate Alert: An entry named '$titleInput' already exists in your local library!",
                            fontSize = 11.sp,
                            color = TechGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Selector
                Text(
                    text = "Category (Domain):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (cat in presetCategories) {
                        val isSelected = (cat == "Custom+" && isCustomCategoryMode) || (!isCustomCategoryMode && selectedCategory == cat)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CyberCyan else CardNavy)
                                .border(1.dp, if (isSelected) CyberCyan else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (cat == "Custom+") {
                                        isCustomCategoryMode = true
                                    } else {
                                        isCustomCategoryMode = false
                                        selectedCategory = cat
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0B0E17) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (isCustomCategoryMode) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        placeholder = { Text("Enter custom category (e.g. Wireshark, Termux)...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardNavy,
                            unfocusedContainerColor = CardNavy
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sub-relation
                Text(
                    text = "Sub-Relation Context:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (sub in subRelations) {
                        val isSelected = selectedSubRelation == sub
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) TechPurple else CardNavy)
                                .border(1.dp, if (isSelected) TechPurple else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .clickable { selectedSubRelation = sub }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = sub,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Certification Target
                Text(
                    text = "Certification Target Mapping:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (cert in certTargets) {
                        val isSelected = selectedCertTarget == cert
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) TechGold else CardNavy)
                                .border(1.dp, if (isSelected) TechGold else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .clickable { selectedCertTarget = cert }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cert,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0B0E17) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Permanent Skip Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { skipSurveyFuture = !skipSurveyFuture },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = skipSurveyFuture,
                        onCheckedChange = { skipSurveyFuture = it },
                        colors = CheckboxDefaults.colors(checkedColor = CyberCyan)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Skip Survey for Future Entries",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Bypass straight to full text editor on next creation",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalCategory = if (isCustomCategoryMode && customCategory.isNotBlank()) customCategory.trim() else selectedCategory
                    onProceedToEditor(
                        SurveyResult(
                            category = finalCategory,
                            subRelation = selectedSubRelation,
                            certTarget = selectedCertTarget,
                            preliminaryTitle = titleInput.trim(),
                            skipSurveyPermanently = skipSurveyFuture
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                modifier = Modifier.testTag("survey_proceed_button")
            ) {
                Text("Proceed to Editor", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = CardNavy,
        shape = RoundedCornerShape(18.dp)
    )
}
