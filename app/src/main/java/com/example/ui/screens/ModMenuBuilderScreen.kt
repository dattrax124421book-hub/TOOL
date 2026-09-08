package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.GlitchViewModel
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GlitchGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import kotlin.math.roundToInt

@Composable
fun ModMenuBuilderScreen(viewModel: GlitchViewModel) {
    val state by viewModel.uiState.collectAsState()
    val cfg = state.modMenuConfig

    var activeTabIdx by remember { mutableStateOf(0) } // 0: Visual Designer & Live Preview, 1: Exported Code
    val builderTabs = listOf("Visual Designer & Live Preview", "Export Java/Smali Bridge")

    val themes = listOf(
        "Neon Cyan" to "#00F5FF",
        "Neon Purple" to "#A855F7",
        "Matrix Green" to "#10B981",
        "Amber Glow" to "#F59E0B",
        "Cyber Red" to "#EF4444"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Builder Mode Tab Row
        ScrollableTabRow(
            selectedTabIndex = activeTabIdx,
            containerColor = CyberSurface,
            contentColor = NeonCyan,
            edgePadding = 0.dp,
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            builderTabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTabIdx == index,
                    onClick = { activeTabIdx = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (activeTabIdx == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (activeTabIdx == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Interactive Floating Preview Canvas
                item {
                    Text(
                        text = "LIVE FLOATING MENU SIMULATOR (DRAGGABLE)",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CodeBackground)
                            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        // Background MCPE simulated world HUD
                        Column(modifier = Modifier.align(Alignment.TopEnd)) {
                            if (cfg.hudFps) {
                                Text("FPS: 60.0", color = GlitchGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            if (cfg.hudCoords) {
                                Text("XYZ: 142.4 / 68.0 / -312.8", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            if (cfg.hudSpeedometer) {
                                Text("Speed: 6.42 bps", color = NeonCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // Floating Mod Menu Box
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(0.92f)
                        ) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = state.isOverlayMenuOpen,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Card(
                                colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = cfg.transparency)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                                    .shadow(8.dp, RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    // Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Gamepad, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = cfg.title,
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.setOverlayMenuOpen(false) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Menu Tabs
                                    val menuCategories = listOf("COMBAT", "MOVEMENT", "RENDER", "WORLD")
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        menuCategories.forEach { cat ->
                                            val isSel = state.activeMenuTab == cat
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isSel) NeonPurple.copy(alpha = 0.3f) else CyberSurfaceVariant)
                                                    .clickable { viewModel.setActiveMenuTab(cat) }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(cat, color = if (isSel) NeonCyan else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Filtered Modules inside Menu Preview
                                    val currentCatModules = state.modules.filter { it.category.name == state.activeMenuTab }
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        currentCatModules.take(3).forEach { mod ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(CyberSurfaceVariant.copy(alpha = 0.6f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(mod.name, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                                    if (mod.hasSlider) {
                                                        Text("${mod.sliderValue} ${mod.sliderUnit}", color = GlitchGreen, fontSize = 9.sp)
                                                    }
                                                }

                                                Switch(
                                                    checked = mod.isEnabled,
                                                    onCheckedChange = { viewModel.toggleModule(mod.id) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.3f)),
                                                    modifier = Modifier.size(36.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                        // Draggable Floating Button
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        cfg.buttonPositionX.roundToInt(),
                                        cfg.buttonPositionY.roundToInt()
                                    )
                                }
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val newX = (cfg.buttonPositionX + dragAmount.x).coerceIn(0f, 600f)
                                        val newY = (cfg.buttonPositionY + dragAmount.y).coerceIn(0f, 240f)
                                        viewModel.updateButtonPosition(newX, newY)
                                    }
                                }
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                                .clickable { viewModel.setOverlayMenuOpen(!state.isOverlayMenuOpen) }
                                .shadow(6.dp, CircleShape)
                                .testTag("floating_menu_toggle_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Layers,
                                contentDescription = "Toggle Mod Menu",
                                tint = CodeBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Customization Controls
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("MENU CONFIGURATION", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = cfg.title,
                                onValueChange = { viewModel.updateModMenuConfig(cfg.copy(title = it)) },
                                label = { Text("Menu Display Title", fontSize = 11.sp, color = TextMuted) },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = CyberBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Theme Color Selector
                            Text("ACCENT COLOR", color = TextSecondary, fontSize = 11.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                themes.forEach { (name, hex) ->
                                    val isSel = cfg.accentColor == hex
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSel) NeonCyan.copy(alpha = 0.3f) else CyberSurfaceVariant)
                                            .clickable { viewModel.updateModMenuConfig(cfg.copy(accentColor = hex)) }
                                            .border(1.dp, if (isSel) NeonCyan else CyberBorder, RoundedCornerShape(16.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(name, color = TextPrimary, fontSize = 11.sp)
                                    }
                                }
                            }

                            // Transparency Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Menu Transparency", color = TextSecondary, fontSize = 11.sp)
                                    Text("${(cfg.transparency * 100).toInt()}%", color = NeonCyan, fontSize = 11.sp)
                                }
                                Slider(
                                    value = cfg.transparency,
                                    onValueChange = { viewModel.updateModMenuConfig(cfg.copy(transparency = it)) },
                                    valueRange = 0.5f..1.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                                )
                            }

                            // HUD Toggles
                            Text("IN-GAME HUD ELEMENTS", color = TextSecondary, fontSize = 11.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = cfg.hudFps,
                                        onCheckedChange = { viewModel.updateModMenuConfig(cfg.copy(hudFps = it)) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = GlitchGreen)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("FPS", color = TextPrimary, fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = cfg.hudCoords,
                                        onCheckedChange = { viewModel.updateModMenuConfig(cfg.copy(hudCoords = it)) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = GlitchGreen)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Coords", color = TextPrimary, fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = cfg.hudSpeedometer,
                                        onCheckedChange = { viewModel.updateModMenuConfig(cfg.copy(hudSpeedometer = it)) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = GlitchGreen)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Speedometer", color = TextPrimary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Exported Java & Smali Injection Code
            Card(
                colors = CardDefaults.cardColors(containerColor = CodeBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Text(
                        text = "GENERATED ANDROID OVERLAY SERVICE (classes3.dex)",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = state.generatedInjectionCode,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
