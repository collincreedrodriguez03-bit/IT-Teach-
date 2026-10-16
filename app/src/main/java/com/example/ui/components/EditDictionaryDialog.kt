package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DictionaryTerm
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyberCyan

@Composable
fun EditDictionaryDialog(
    term: DictionaryTerm,
    onDismiss: () -> Unit,
    onSave: (DictionaryTerm) -> Unit
) {
    var termName by remember { mutableStateOf(term.term) }
    var command by remember { mutableStateOf(term.command) }
    var description by remember { mutableStateOf(term.description) }
    var category by remember { mutableStateOf(term.category) }
    var difficulty by remember { mutableStateOf(term.difficulty) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardNavy,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Edit Technical Term",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = termName,
                    onValueChange = { termName = it },
                    label = { Text("Command / Term Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_term_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    label = { Text("Exact CLI Syntax") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_term_command_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Engineering Description") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("edit_term_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = { difficulty = it },
                        label = { Text("Difficulty") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (termName.isNotBlank() || command.isNotBlank()) {
                        onSave(
                            term.copy(
                                term = termName.trim(),
                                command = command.trim(),
                                description = description.trim(),
                                category = category.trim().ifBlank { "Networking" },
                                difficulty = difficulty.trim().ifBlank { "Beginner" }
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17)),
                modifier = Modifier.testTag("save_edit_term_button")
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
