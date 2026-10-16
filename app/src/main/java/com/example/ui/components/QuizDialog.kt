package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Quest
import com.example.data.service.QuizQuestion
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechGold

@Composable
fun QuizDialog(
    quest: Quest,
    questions: List<QuizQuestion>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onFinish: (correct: Int, total: Int) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnsweredCurrent by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = TechGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daily Adaptive Drill",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (!isLoading && questions.isNotEmpty() && !isFinished) {
                    Text(
                        text = "Q ${currentIndex + 1}/${questions.size}",
                        fontSize = 12.sp,
                        color = CyberCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CyberCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("AI Generating adaptive retention drill...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            } else if (isFinished) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val passed = (correctCount.toFloat() / maxOf(1, questions.size)) >= 0.6f
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (passed) TechEmerald.copy(alpha = 0.2f) else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (passed) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (passed) TechEmerald else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (passed) "Assessment Passed!" else "Review Required",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (passed) TechEmerald else MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You scored $correctCount out of ${questions.size} questions correct.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (passed) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "+${quest.xpReward} XP Awarded to your profile!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechGold
                        )
                    }
                }
            } else {
                val currentQ = questions.getOrNull(currentIndex)
                if (currentQ != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = currentQ.question,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        currentQ.options.forEachIndexed { idx, opt ->
                            val isSelected = selectedOptionIndex == idx
                            val isCorrect = idx == currentQ.correctIndex
                            val bg = when {
                                hasAnsweredCurrent && isCorrect -> TechEmerald.copy(alpha = 0.25f)
                                hasAnsweredCurrent && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
                                isSelected -> CyberCyan.copy(alpha = 0.2f)
                                else -> CardNavyElevated
                            }
                            val border = when {
                                hasAnsweredCurrent && isCorrect -> TechEmerald
                                hasAnsweredCurrent && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                                isSelected -> CyberCyan
                                else -> MaterialTheme.colorScheme.outline
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(10.dp))
                                    .clickable(enabled = !hasAnsweredCurrent) {
                                        selectedOptionIndex = idx
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        if (hasAnsweredCurrent) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Explanation: ${currentQ.explanation}",
                                fontSize = 11.sp,
                                color = TechGold,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isFinished) {
                Button(
                    onClick = { onFinish(correctCount, questions.size) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17))
                ) {
                    Text("Done")
                }
            } else if (!isLoading && questions.isNotEmpty()) {
                val currentQ = questions.getOrNull(currentIndex)
                if (!hasAnsweredCurrent) {
                    Button(
                        onClick = {
                            if (selectedOptionIndex != null) {
                                hasAnsweredCurrent = true
                                if (selectedOptionIndex == currentQ?.correctIndex) {
                                    correctCount += 1
                                }
                            }
                        },
                        enabled = selectedOptionIndex != null,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17))
                    ) {
                        Text("Check Answer")
                    }
                } else {
                    Button(
                        onClick = {
                            if (currentIndex + 1 < questions.size) {
                                currentIndex += 1
                                selectedOptionIndex = null
                                hasAnsweredCurrent = false
                            } else {
                                isFinished = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0B0E17))
                    ) {
                        Text(if (currentIndex + 1 < questions.size) "Next Question" else "View Results")
                    }
                }
            }
        },
        dismissButton = {
            if (!isFinished) {
                TextButton(onClick = onDismiss) {
                    Text("Exit Drill")
                }
            }
        }
    )
}
