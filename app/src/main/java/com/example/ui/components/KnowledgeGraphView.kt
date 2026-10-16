package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DictionaryTerm
import com.example.data.model.JournalEntry
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechGold
import com.example.ui.theme.TechPurple
import com.example.ui.theme.TerminalBg
import kotlin.math.sqrt

enum class NodeTier {
    PARENT_APP,       // Level 0: Linux, Termux, Windows, Python, Cisco IOS, Wireshark
    TERMINAL_CONTEXT, // Level 1: Bash, PowerShell, Termux CLI, Python REPL, IOS Config
    COMMAND,          // Level 2: nmap, traceroute, iptables, wireshark, scapy, socket
    CONCEPT           // Level 3: TCP Handshake, Subnetting, Packet Inspection, Firewalls
}

enum class NodeMasteryStatus {
    DISCOVERED,  // Grey / Outline
    LAB_LOGGED,  // Electric Blue Pulse
    MASTERED     // Minecraft RTX Molten Lava Glowing Effect (Orange / Gold core)
}

data class GraphNode(
    val id: String,
    val label: String,
    val subtitle: String,
    val tier: NodeTier,
    val domain: String,
    val status: NodeMasteryStatus,
    val relativeX: Float, // Normalized 0f to 1f
    val relativeY: Float, // Normalized 0f to 1f
    val operationalDependency: String
)

data class GraphEdge(
    val sourceId: String,
    val targetId: String,
    val relationshipType: String, // "EXECUTES", "CONTAINS", "REQUIRES_CONCEPT"
    val strokeWidthDp: Float,
    val color: Color,
    val isDashed: Boolean = false
)

object DefaultKnowledgeData {
    fun buildNodes(
        journalEntries: List<JournalEntry>,
        dictionaryTerms: List<DictionaryTerm>
    ): List<GraphNode> {
        val hasJournal = { tag: String -> journalEntries.any { it.title.contains(tag, true) || it.tags.any { t -> t.contains(tag, true) } } }
        val isMastered = { termText: String -> dictionaryTerms.any { it.term.equals(termText, true) && it.isMastered } }

        return listOf(
            // Tier 0: Parent App / OS / Program
            GraphNode(
                id = "p_linux",
                label = "Linux Kernel",
                subtitle = "Debian / Ubuntu / Kali OS",
                tier = NodeTier.PARENT_APP,
                domain = "Linux",
                status = if (isMastered("chmod") || hasJournal("Linux")) NodeMasteryStatus.MASTERED else NodeMasteryStatus.LAB_LOGGED,
                relativeX = 0.20f,
                relativeY = 0.12f,
                operationalDependency = "Underlying Unix POSIX standard and system calls"
            ),
            GraphNode(
                id = "p_termux",
                label = "Termux Android",
                subtitle = "Mobile Terminal Environment",
                tier = NodeTier.PARENT_APP,
                domain = "Termux",
                status = if (hasJournal("Termux")) NodeMasteryStatus.LAB_LOGGED else NodeMasteryStatus.DISCOVERED,
                relativeX = 0.50f,
                relativeY = 0.12f,
                operationalDependency = "Android Linux sandbox & apt package manager"
            ),
            GraphNode(
                id = "p_python",
                label = "Python 3.12",
                subtitle = "Automation & Socket Engine",
                tier = NodeTier.PARENT_APP,
                domain = "Python",
                status = if (isMastered("socket") || hasJournal("Python")) NodeMasteryStatus.MASTERED else NodeMasteryStatus.LAB_LOGGED,
                relativeX = 0.80f,
                relativeY = 0.12f,
                operationalDependency = "CPython runtime and standard networking libraries"
            ),

            // Tier 1: Terminal Context
            GraphNode(
                id = "t_bash",
                label = "Bash Shell (CLI)",
                subtitle = "/bin/bash shell execution",
                tier = NodeTier.TERMINAL_CONTEXT,
                domain = "Linux",
                status = if (hasJournal("Bash")) NodeMasteryStatus.MASTERED else NodeMasteryStatus.LAB_LOGGED,
                relativeX = 0.20f,
                relativeY = 0.38f,
                operationalDependency = "STDIN / STDOUT file descriptor piping & PATH binaries"
            ),
            GraphNode(
                id = "t_termux_cli",
                label = "Termux Zsh/Bash",
                subtitle = "Mobile CLI toolchain",
                tier = NodeTier.TERMINAL_CONTEXT,
                domain = "Termux",
                status = NodeMasteryStatus.LAB_LOGGED,
                relativeX = 0.50f,
                relativeY = 0.38f,
                operationalDependency = "Rootless Android shell environment with proot support"
            ),
            GraphNode(
                id = "t_repl",
                label = "Scapy / Netmiko",
                subtitle = "Network scripting console",
                tier = NodeTier.TERMINAL_CONTEXT,
                domain = "Python",
                status = NodeMasteryStatus.DISCOVERED,
                relativeX = 0.80f,
                relativeY = 0.38f,
                operationalDependency = "Python module import & asynchronous socket event loop"
            ),

            // Tier 2: Commands & Tools
            GraphNode(
                id = "c_nmap",
                label = "nmap -sS -p-",
                subtitle = "SYN Stealth Port Scanner",
                tier = NodeTier.COMMAND,
                domain = "Security",
                status = if (isMastered("nmap") || hasJournal("Nmap")) NodeMasteryStatus.MASTERED else NodeMasteryStatus.LAB_LOGGED,
                relativeX = 0.15f,
                relativeY = 0.65f,
                operationalDependency = "Raw socket packet transmission (Requires root/sudo)"
            ),
            GraphNode(
                id = "c_wireshark",
                label = "tshark / Wireshark",
                subtitle = "Live Packet Capture",
                tier = NodeTier.COMMAND,
                domain = "Networking",
                status = if (isMastered("wireshark") || hasJournal("Wireshark")) NodeMasteryStatus.MASTERED else NodeMasteryStatus.LAB_LOGGED,
                relativeX = 0.38f,
                relativeY = 0.65f,
                operationalDependency = "libpcap packet sniffing hook on network interface"
            ),
            GraphNode(
                id = "c_traceroute",
                label = "traceroute -I",
                subtitle = "ICMP TTL Route Discovery",
                tier = NodeTier.COMMAND,
                domain = "Networking",
                status = if (isMastered("traceroute")) NodeMasteryStatus.MASTERED else NodeMasteryStatus.DISCOVERED,
                relativeX = 0.62f,
                relativeY = 0.65f,
                operationalDependency = "Incremental TTL expiration & ICMP Type 11 Time Exceeded"
            ),
            GraphNode(
                id = "c_socket",
                label = "socket.connect()",
                subtitle = "Direct TCP Client Endpoint",
                tier = NodeTier.COMMAND,
                domain = "Python",
                status = if (isMastered("socket")) NodeMasteryStatus.MASTERED else NodeMasteryStatus.DISCOVERED,
                relativeX = 0.85f,
                relativeY = 0.65f,
                operationalDependency = "TCP 3-way handshake initiated from userspace socket"
            ),

            // Tier 3: Fundamental Concepts
            GraphNode(
                id = "f_handshake",
                label = "TCP 3-Way Handshake",
                subtitle = "SYN -> SYN-ACK -> ACK",
                tier = NodeTier.CONCEPT,
                domain = "Networking",
                status = NodeMasteryStatus.MASTERED,
                relativeX = 0.30f,
                relativeY = 0.90f,
                operationalDependency = "Stateful connection establishment with Sequence Numbers"
            ),
            GraphNode(
                id = "f_icmp",
                label = "TTL & ICMP Routing",
                subtitle = "Hop-by-hop packet lifetime",
                tier = NodeTier.CONCEPT,
                domain = "Networking",
                status = NodeMasteryStatus.LAB_LOGGED,
                relativeX = 0.55f,
                relativeY = 0.90f,
                operationalDependency = "RFC 792 ICMP protocol messaging for IP transit health"
            ),
            GraphNode(
                id = "f_firewall",
                label = "Stateful Inspection",
                subtitle = "L4 Port Filtering & NAT",
                tier = NodeTier.CONCEPT,
                domain = "Security",
                status = NodeMasteryStatus.MASTERED,
                relativeX = 0.80f,
                relativeY = 0.90f,
                operationalDependency = "iptables connection tracking table & drop rules"
            )
        )
    }

    fun buildEdges(): List<GraphEdge> {
        return listOf(
            // Parent -> Terminal Context
            GraphEdge("p_linux", "t_bash", "CONTAINS", 3f, CyberCyan),
            GraphEdge("p_termux", "t_termux_cli", "CONTAINS", 2.5f, TechEmerald),
            GraphEdge("p_python", "t_repl", "CONTAINS", 2.5f, TechPurple),

            // Terminal Context -> Commands
            GraphEdge("t_bash", "c_nmap", "EXECUTES", 3f, CyberCyan),
            GraphEdge("t_bash", "c_wireshark", "EXECUTES", 3f, CyberCyan),
            GraphEdge("t_termux_cli", "c_nmap", "EXECUTES", 2f, TechEmerald, isDashed = true),
            GraphEdge("t_termux_cli", "c_traceroute", "EXECUTES", 2f, TechEmerald),
            GraphEdge("t_repl", "c_socket", "EXECUTES", 3f, TechPurple),
            GraphEdge("t_bash", "c_traceroute", "EXECUTES", 2.5f, CyberCyan),

            // Commands -> Fundamental Concepts
            GraphEdge("c_nmap", "f_handshake", "REQUIRES_CONCEPT", 3.5f, TechGold),
            GraphEdge("c_wireshark", "f_handshake", "REQUIRES_CONCEPT", 3.5f, TechGold),
            GraphEdge("c_traceroute", "f_icmp", "REQUIRES_CONCEPT", 3f, CyberCyan),
            GraphEdge("c_socket", "f_handshake", "REQUIRES_CONCEPT", 2.5f, TechPurple),
            GraphEdge("c_nmap", "f_firewall", "REQUIRES_CONCEPT", 3.5f, TechGold)
        )
    }
}

@Composable
fun KnowledgeGraphModalDialog(
    journalEntries: List<JournalEntry>,
    dictionaryTerms: List<DictionaryTerm>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = DeepNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, TechGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B1E08))
                                .border(1.dp, TechGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = TechGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Automated Knowledge Graph",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Operational Dependency Tree • RTX Lava Engine",
                                fontSize = 11.sp,
                                color = TechGold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_knowledge_graph_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Interactive Map Legend
                GraphMapLegendBar()

                // Interactive Graph Canvas Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    InteractiveKnowledgeCanvas(
                        journalEntries = journalEntries,
                        dictionaryTerms = dictionaryTerms
                    )
                }
            }
        }
    }
}

@Composable
fun GraphMapLegendBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardNavyElevated)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(
            indicatorColor = Color(0xFF64748B),
            label = "Discovered",
            styleDesc = "Grey Outline"
        )
        LegendItem(
            indicatorColor = CyberCyan,
            label = "Lab Logged",
            styleDesc = "Electric Blue Pulse"
        )
        LegendItem(
            indicatorColor = Color(0xFFFF5722),
            label = "Mastered",
            styleDesc = "Minecraft RTX Lava Glow",
            isLava = true
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .background(TechGold)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "[App] ➔ [CLI] ➔ [Cmd] ➔ [Concept]",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TechGold
            )
        }
    }
}

@Composable
private fun LegendItem(
    indicatorColor: Color,
    label: String,
    styleDesc: String,
    isLava: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (isLava) Color(0xFFFF9800) else indicatorColor)
                .border(1.5.dp, if (isLava) Color(0xFFFFD700) else indicatorColor.copy(alpha = 0.5f), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label ($styleDesc)",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InteractiveKnowledgeCanvas(
    journalEntries: List<JournalEntry>,
    dictionaryTerms: List<DictionaryTerm>
) {
    val nodes = remember(journalEntries, dictionaryTerms) {
        DefaultKnowledgeData.buildNodes(journalEntries, dictionaryTerms)
    }
    val edges = remember { DefaultKnowledgeData.buildEdges() }

    var selectedNode by remember { mutableStateOf<GraphNode?>(null) }

    // Pulsing animations for Electric Blue & RTX Lava
    val infiniteTransition = rememberInfiniteTransition(label = "GraphGlowTransition")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAnim"
    )

    val lavaCoreAnim by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LavaCoreAnim"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(nodes) {
                    detectTapGestures { tapOffset ->
                        val width = size.width
                        val height = size.height
                        // Check if tap hit any node
                        val hit = nodes.find { node ->
                            val nx = node.relativeX * width
                            val ny = node.relativeY * height
                            val dist = sqrt((tapOffset.x - nx) * (tapOffset.x - nx) + (tapOffset.y - ny) * (tapOffset.y - ny))
                            dist <= 35f // Hit radius in pixels
                        }
                        selectedNode = hit
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // 1. Draw Edges / Dependency Links
            for (edge in edges) {
                val source = nodes.find { it.id == edge.sourceId } ?: continue
                val target = nodes.find { it.id == edge.targetId } ?: continue

                val sx = source.relativeX * canvasWidth
                val sy = source.relativeY * canvasHeight
                val tx = target.relativeX * canvasWidth
                val ty = target.relativeY * canvasHeight

                // Draw dependency curve
                val path = Path().apply {
                    moveTo(sx, sy)
                    val midY = (sy + ty) / 2f
                    cubicTo(sx, midY, tx, midY, tx, ty)
                }

                drawPath(
                    path = path,
                    color = edge.color.copy(alpha = 0.65f),
                    style = Stroke(
                        width = edge.strokeWidthDp.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = if (edge.isDashed) PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f) else null
                    )
                )
            }

            // 2. Draw Nodes with Minecraft RTX Lava Glowing Effect
            for (node in nodes) {
                val nx = node.relativeX * canvasWidth
                val ny = node.relativeY * canvasHeight
                val isSelected = selectedNode?.id == node.id
                val baseRadius = if (node.tier == NodeTier.PARENT_APP) 24.dp.toPx() else 18.dp.toPx()

                when (node.status) {
                    NodeMasteryStatus.MASTERED -> {
                        // Minecraft RTX Molten Lava Glowing Effect:
                        // Multi-layered intense radial radiant bloom: Deep Red -> Molten Orange -> Radiant Gold -> Supernova Core
                        val outerLavaRadius = baseRadius * (1.8f * pulseAnim)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.9f),
                                    Color(0xFFFF9800).copy(alpha = 0.65f),
                                    Color(0xFFFF3D00).copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                center = Offset(nx, ny),
                                radius = outerLavaRadius
                            ),
                            radius = outerLavaRadius,
                            center = Offset(nx, ny)
                        )

                        // Core molten lava sphere
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFF7C2), // Supernova bright core
                                    Color(0xFFFFC107), // Gold
                                    Color(0xFFFF5722)  // Molten magma orange
                                ),
                                center = Offset(nx, ny),
                                radius = baseRadius * lavaCoreAnim
                            ),
                            radius = baseRadius * lavaCoreAnim,
                            center = Offset(nx, ny)
                        )

                        // Golden rim
                        drawCircle(
                            color = Color(0xFFFFE082),
                            radius = baseRadius * lavaCoreAnim,
                            center = Offset(nx, ny),
                            style = Stroke(width = 2.5.dp.toPx())
                        )
                    }

                    NodeMasteryStatus.LAB_LOGGED -> {
                        // Electric Blue Pulse
                        val outerRadius = baseRadius * (1.5f * pulseAnim)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    CyberCyan.copy(alpha = 0.5f),
                                    CyberCyan.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                center = Offset(nx, ny),
                                radius = outerRadius
                            ),
                            radius = outerRadius,
                            center = Offset(nx, ny)
                        )
                        drawCircle(
                            color = CyberCyan,
                            radius = baseRadius,
                            center = Offset(nx, ny)
                        )
                        drawCircle(
                            color = Color(0xFFE0F7FA),
                            radius = baseRadius * 0.5f,
                            center = Offset(nx, ny)
                        )
                    }

                    NodeMasteryStatus.DISCOVERED -> {
                        // Grey / Outline
                        drawCircle(
                            color = CardNavyElevated,
                            radius = baseRadius,
                            center = Offset(nx, ny)
                        )
                        drawCircle(
                            color = Color(0xFF94A3B8),
                            radius = baseRadius,
                            center = Offset(nx, ny),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }

                // If selected, draw highlight halo
                if (isSelected) {
                    drawCircle(
                        color = Color.White,
                        radius = baseRadius + 7.dp.toPx(),
                        center = Offset(nx, ny),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        // Selected Node Inspection Card overlay at bottom
        if (selectedNode != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selectedNode!!.status == NodeMasteryStatus.MASTERED) TechGold else CyberCyan)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedNode!!.tier.name.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechGold,
                                modifier = Modifier
                                    .background(Color(0xFF2E1905), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedNode!!.domain,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyberCyan,
                                modifier = Modifier
                                    .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = { selectedNode = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedNode!!.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = selectedNode!!.subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Operational Dependency:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechGold
                    )
                    Text(
                        text = selectedNode!!.operationalDependency,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
