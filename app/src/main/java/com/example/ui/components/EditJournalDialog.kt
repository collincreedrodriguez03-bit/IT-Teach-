package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntry
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyberCyan

@Composable
fun EditJournalDialog(
    entry: JournalEntry,
    onDismiss: () -> Unit,
    onSave: (JournalEntry) -> Unit
) {
    var title by remember { mutableStateOf(entry.title) }
    var notes by remember { mutableStateOf(entry.notes) }
    var solution by remember { mutableStateOf(entry.solution) }
    var tagsString by remember { mutableStateOf(entry.tags.joinToString(", ")) }
    var category by remember { mutableStateOf(entry.category) }
    var isVerified by remember { mutableStateOf(entry.isVerified) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardNavy,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Edit Journal & Lab Note",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lab Title / Incident Scenario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_journal_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Incident Observations / Problem Notes") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("edit_journal_notes_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = solution,
                    onValueChange = { solution = it },
                    label = { Text("Verified Solution & Command Syntax") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("edit_journal_solution_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = tagsString,
                    onValueChange = { tagsString = it },
                    label = { Text("Tags (comma-separated, e.g. wireshark, nmap)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isVerified,
                        onCheckedChange = { isVerified = it },
                        modifier = Modifier.testTag("edit_journal_verified_checkbox")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mark as Verified Working Solution",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val parsedTags = tagsString.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        onSave(
                            entry.copy(
                                title = title.trim(),
                                notes = notes.trim(),
                                solution = solution.trim(),
                                tags = parsedTags,
                                category = category.trim().ifBlank { "Networking" },
                                isVerified = isVerified
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                modifier = Modifier.testTag("save_edit_journal_button")
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
