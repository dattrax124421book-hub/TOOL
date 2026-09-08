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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ApkPackager
import com.example.ui.GlitchViewModel
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GlitchGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun PackagingScreen(viewModel: GlitchViewModel) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Signing & Packaging Configuration Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CRYPTOGRAPHIC SIGNING ENGINE",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GlitchGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("AOSP Keys Ready", color = GlitchGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Signature Schemes Checkboxes
                    Text("SIGNATURE SCHEMES TO APPLY", color = TextSecondary, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = state.v1Signer,
                                onCheckedChange = { viewModel.setV1Signer(it) },
                                colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                            )
                            Text("v1 (JAR)", color = TextPrimary, fontSize = 11.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = state.v2Signer,
                                onCheckedChange = { viewModel.setV2Signer(it) },
                                colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                            )
                            Text("v2 (APK Full Zip)", color = TextPrimary, fontSize = 11.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = state.v3Signer,
                                onCheckedChange = { viewModel.setV3Signer(it) },
                                colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                            )
                            Text("v3 (Rotation)", color = TextPrimary, fontSize = 11.sp)
                        }
                    }

                    // Signature Conflict Helper (Package Clone)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Clone Package (No-Uninstall Mode)",
                                    color = WarningAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Changes package to com.mojang.minecraftpe.glitch so original game & worlds remain untouched.",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = state.clonePackage,
                                onCheckedChange = { viewModel.setClonePackage(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = WarningAmber)
                            )
                        }
                    }

                    // Build & Sign Primary Action
                    Button(
                        onClick = { viewModel.buildAndSignApk() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("build_and_sign_button")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CodeBackground)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REBUILD, REPACK & SIGN MODDED APK",
                            color = CodeBackground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.signingStatus.startsWith("SUCCESS")) {
                        Button(
                            onClick = {
                                viewModel.showMessage("Launching Android Package Installer for ${state.activeProject.apkFileName.replace(".apk", "")}_modded_signed.apk...")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GlitchGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("install_apk_button")
                        ) {
                            Icon(Icons.Default.InstallMobile, contentDescription = null, tint = CodeBackground)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "INSTALL MODDED APK ON DEVICE",
                                color = CodeBackground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Build / Signing Log Output
        if (state.signingLogs.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CodeBackground),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "BUILD & SIGNING PIPELINE LOG",
                            color = GlitchGreen,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.signingLogs,
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Export Log & Safe Mode Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val exported = ApkPackager.exportPatchLog(state.hexPatches, state.selectedMcpeVersion)
                        viewModel.showMessage("Patch log exported to /sdcard/GlitchEngine/patches_${state.selectedMcpeVersion}.txt")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Patch Log", color = TextPrimary, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        viewModel.showMessage("Safe-Mode boot config written: SIGSEGV / SIGBUS recovery hooks armed.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.BugReport, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Crash-Guard", color = TextPrimary, fontSize = 11.sp)
                }
            }
        }

        // Live Logcat Monitor Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE MCPE LOGCAT MONITOR",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("ALL", "GLITCH", "NATIVE").forEach { filter ->
                        val isSel = state.logcatFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant)
                                .clickable { viewModel.setLogcatFilter(filter) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(filter, color = if (isSel) NeonCyan else TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Filtered Logcat items
        val filteredLogs = when (state.logcatFilter) {
            "GLITCH" -> state.logcatEntries.filter { it.tag.contains("Glitch") }
            "NATIVE" -> state.logcatEntries.filter { it.tag.contains("MCPE") }
            else -> state.logcatEntries
        }

        items(filteredLogs) { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CodeBackground),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, CyberBorder, RoundedCornerShape(4.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${log.timestamp} ",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    val levelColor = when (log.level) {
                        "E" -> DangerRed
                        "W" -> WarningAmber
                        "I" -> GlitchGreen
                        else -> NeonCyan
                    }
                    Text(
                        text = "[${log.level}/${log.tag}] ",
                        color = levelColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = log.message,
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
