package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.Arm64Assembler
import com.example.ui.GlitchViewModel
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GlitchGreen
import com.example.ui.theme.HexHighlight
import com.example.ui.theme.HexModified
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun HookFrameworkScreen(viewModel: GlitchViewModel) {
    val state by viewModel.uiState.collectAsState()
    var targetFuncName by remember { mutableStateOf(state.hookTargetFunction) }
    var hookAddrInput by remember { mutableStateOf(state.hookAddressHex) }
    var caveAddrInput by remember { mutableStateOf(state.hookCaveAddressHex) }

    val hookTemplates = listOf(
        "Player::getReachDistance" to ("0x0381F4C0" to "0x0381F4E8"),
        "LocalPlayer::tick" to ("0x0349A100" to "0x0349A128"),
        "GameMode::attack" to ("0x0399CD80" to "0x0399CDA8"),
        "RakNetInstance::send" to ("0x028A1310" to "0x028A1338"),
        "Dimension::getBrightness" to ("0x02B41AC4" to "0x02B41AEC")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Live Test Simulation Runner Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (state.isLiveTestActive) GlitchGreen else CyberBorder,
                        RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (state.isLiveTestActive) GlitchGreen.copy(alpha = 0.2f) else CyberSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (state.isLiveTestActive) GlitchGreen else TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Frida Live-Test Simulation",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (state.isLiveTestActive) "Engine hooked! Interceptions: ${state.liveHookInterceptionCount}"
                                else "Simulate hook without rebuilding APK",
                                color = if (state.isLiveTestActive) GlitchGreen else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = state.isLiveTestActive,
                        onCheckedChange = { viewModel.toggleLiveHookTest() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GlitchGreen,
                            checkedTrackColor = GlitchGreen.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("live_test_switch")
                    )
                }
            }
        }

        // Quick Hook Templates
        item {
            Column {
                Text(
                    text = "HOOK TEMPLATES (ARM64-v8a)",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hookTemplates.forEach { (func, addrs) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSurfaceVariant)
                                .clickable {
                                    targetFuncName = func
                                    hookAddrInput = addrs.first
                                    caveAddrInput = addrs.second
                                    val h = java.lang.Long.decode(addrs.first)
                                    val c = java.lang.Long.decode(addrs.second)
                                    viewModel.generateHookInstructions(h, c, func)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = func,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Hook Parameters Input
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "HOOK PARAMETERS & DISPLACEMENT",
                        color = NeonPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = targetFuncName,
                        onValueChange = { targetFuncName = it },
                        label = { Text("Target Native Symbol", fontSize = 11.sp, color = TextMuted) },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = CyberBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hookAddrInput,
                            onValueChange = { hookAddrInput = it },
                            label = { Text("Hook Addr", fontSize = 10.sp, color = TextMuted) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = NeonCyan),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = caveAddrInput,
                            onValueChange = { caveAddrInput = it },
                            label = { Text("Code Cave Addr", fontSize = 10.sp, color = TextMuted) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = GlitchGreen),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GlitchGreen,
                                unfocusedBorderColor = CyberBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            try {
                                val h = java.lang.Long.decode(hookAddrInput)
                                val c = java.lang.Long.decode(caveAddrInput)
                                viewModel.generateHookInstructions(h, c, targetFuncName)
                                viewModel.showMessage("Generated trampoline instructions for $targetFuncName")
                            } catch (e: Exception) {
                                viewModel.showMessage("Invalid hex address.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calculate Displacement & Disassemble Trampoline")
                    }
                }
            }
        }

        // Trampoline Assembly Viewer
        item {
            Text(
                text = "DISASSEMBLED INLINE TRAMPOLINE",
                color = WarningAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(state.generatedTrampoline) { ins ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CodeBackground),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format("0x%08X:  ", ins.address),
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = ins.instruction,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = ins.comment,
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = ins.opcodeHex,
                            color = HexHighlight,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
