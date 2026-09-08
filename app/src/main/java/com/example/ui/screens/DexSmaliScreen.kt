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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun DexSmaliScreen(viewModel: GlitchViewModel) {
    val state by viewModel.uiState.collectAsState()
    var searchDexQuery by remember { mutableStateOf(state.smaliSearchQuery) }
    var selectedDexTab by remember { mutableStateOf(0) }
    val dexTabs = listOf("classes.dex", "classes2.dex", "classes3.dex (Glitch)")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Multi-DEX Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedDexTab,
            containerColor = CyberSurface,
            contentColor = NeonCyan,
            edgePadding = 0.dp,
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            dexTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedDexTab == index,
                    onClick = { selectedDexTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedDexTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Multi-Dex Search Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchDexQuery,
                onValueChange = { searchDexQuery = it },
                placeholder = { Text("Search classes, methods across DEX...", fontSize = 11.sp, color = TextMuted) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TextPrimary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface
                ),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { viewModel.searchSmaliAcrossDex(searchDexQuery) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Search", color = TextPrimary, fontSize = 12.sp)
            }
        }

        // Search Results Chips
        if (state.smaliSearchResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Found in:", color = WarningAmber, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterVertically))
                state.smaliSearchResults.forEach { (file, count) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(WarningAmber.copy(alpha = 0.2f))
                            .clickable { viewModel.selectSmaliFile(file) }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "${file.className.substringAfterLast("/")} ($count hits)",
                            color = WarningAmber,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Smali File Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.smaliFiles.forEach { file ->
                val isSelected = file.path == state.activeSmaliFile.path
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) NeonPurple.copy(alpha = 0.3f) else CyberSurface)
                        .clickable { viewModel.selectSmaliFile(file) }
                        .border(1.dp, if (isSelected) NeonPurple else CyberBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = if (isSelected) NeonCyan else TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(file.className.substringAfterLast("/"), color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(file.dexLocation, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Smali Editor Area
        Card(
            colors = CardDefaults.cardColors(containerColor = CodeBackground),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                // Editor status bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.activeSmaliFile.className,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { viewModel.checkSmaliSyntax() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(28.dp).testTag("syntax_check_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = GlitchGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check Syntax", color = TextPrimary, fontSize = 10.sp)
                        }

                        Button(
                            onClick = { viewModel.autoInjectModMenu() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(28.dp).testTag("auto_inject_button")
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inject Menu", color = TextPrimary, fontSize = 10.sp)
                        }
                    }
                }

                // Error Banner if syntax check failed
                if (state.smaliErrors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(DangerRed.copy(alpha = 0.2f))
                            .padding(6.dp)
                    ) {
                        state.smaliErrors.forEach { err ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(err, color = DangerRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Editable Code Text Area
                OutlinedTextField(
                    value = state.smaliEditorText,
                    onValueChange = { viewModel.updateSmaliText(it) },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedContainerColor = CodeBackground,
                        unfocusedContainerColor = CodeBackground
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("smali_text_editor")
                )
            }
        }
    }
}
