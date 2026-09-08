package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.McpeDatabase
import com.example.model.HexPatch
import com.example.ui.GlitchNavTab
import com.example.ui.GlitchViewModel
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GlitchGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleDark
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun WorkspaceScreen(viewModel: GlitchViewModel) {
    val state by viewModel.uiState.collectAsState()
    val versions = listOf("1.26.31.1", "1.25.10", "1.21.50")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Project Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .testTag("active_project_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = "MCPE",
                                tint = NeonCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = state.activeProject.name,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.activeProject.apkFileName,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonPurple.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = state.activeProject.architecture,
                                color = NeonPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Project Specs Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecChip(label = "Engine SO", value = "${state.activeProject.soSizeMb} MB")
                        SpecChip(label = "DEX Files", value = "${state.activeProject.dexCount} classes.dex")
                        SpecChip(label = "Package", value = "com.mojang.minecraftpe")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Backup & Restore Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.createBackup() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("backup_button")
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = "Backup", tint = GlitchGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Auto Backup", color = TextPrimary, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.restoreBackup() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningAmber),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restore_button")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = "Restore", tint = WarningAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore Orig", color = WarningAmber, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // MCPE Version Profiles Selector
        item {
            Column {
                Text(
                    text = "TARGET MCPE VERSION PROFILE",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                ScrollableTabRow(
                    selectedTabIndex = versions.indexOf(state.selectedMcpeVersion),
                    containerColor = CyberSurface,
                    contentColor = NeonCyan,
                    edgePadding = 0.dp,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    versions.forEachIndexed { index, ver ->
                        Tab(
                            selected = state.selectedMcpeVersion == ver,
                            onClick = { viewModel.loadVersionProfile(ver) },
                            text = {
                                Text(
                                    text = "v$ver (arm64)",
                                    fontSize = 13.sp,
                                    fontWeight = if (state.selectedMcpeVersion == ver) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        // Offset Database Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ENGINE OFFSET DATABASE (${state.knownOffsets.size} SIGNED HOOKS)",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "libminecraftpe.so",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        items(state.knownOffsets, key = { it.id }) { offsetInfo ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = offsetInfo.functionName,
                                color = NeonCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = offsetInfo.demangledSymbol,
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = offsetInfo.offsetHex,
                                color = GlitchGreen,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = offsetInfo.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Patch: ${offsetInfo.originalHex} -> ${offsetInfo.patchedHex}",
                            color = WarningAmber,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Button(
                            onClick = {
                                viewModel.applyHexPatch(
                                    com.example.model.HexPatch(
                                        id = offsetInfo.id,
                                        offset = java.lang.Long.decode(offsetInfo.offsetHex),
                                        originalBytes = offsetInfo.originalHex,
                                        patchedBytes = offsetInfo.patchedHex,
                                        description = offsetInfo.functionName,
                                        functionName = offsetInfo.functionName
                                    )
                                )
                                viewModel.jumpToOffset(offsetInfo.offsetHex)
                                viewModel.selectTab(GlitchNavTab.HEX_PATCHER)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleDark),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Open in Hex", color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Available Projects List
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "SWITCH APK WORKSPACE",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(state.projects, key = { it.id }) { proj ->
            val isSelected = proj.id == state.activeProject.id
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CyberSurfaceVariant else CyberSurface
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectProject(proj) }
                    .border(
                        1.dp,
                        if (isSelected) NeonCyan else CyberBorder,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = if (isSelected) NeonCyan else TextMuted
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = proj.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = "MCPE ${proj.mcpeVersion} • ${proj.architecture}", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = GlitchGreen, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SpecChip(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CyberSurfaceVariant)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
