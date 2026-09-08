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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.HexPatch
import com.example.ui.GlitchViewModel
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberVoid
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
fun HexPatcherScreen(viewModel: GlitchViewModel) {
    val state by viewModel.uiState.collectAsState()
    var offsetInput by remember { mutableStateOf(String.format("0x%08X", state.hexBaseOffset)) }
    var searchInput by remember { mutableStateOf(state.hexSearchQuery) }
    var patchBytesInput by remember { mutableStateOf("00 00 20 52 C0 03 5F D6") }

    val bookmarks = listOf(
        "Reach" to "0x0381F4C0",
        "Fullbright" to "0x02B41AC4",
        "NoFog" to "0x01E49810",
        "Anti-KB" to "0x0349A100",
        "Fly/Jump" to "0x0321A840"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Toolbar: Navigation, Jump, Undo/Redo, Diff Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = offsetInput,
                onValueChange = { offsetInput = it },
                label = { Text("Jump to Hex Offset", fontSize = 11.sp, color = TextMuted) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = NeonCyan
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("offset_input")
            )

            Button(
                onClick = { viewModel.jumpToOffset(offsetInput) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("jump_button")
            ) {
                Text("Go", color = CodeBackground, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = { viewModel.undoHexPatch() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurface)
            ) {
                Icon(Icons.Default.Undo, contentDescription = "Undo", tint = NeonPurple)
            }

            IconButton(
                onClick = { viewModel.redoHexPatch() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurface)
            ) {
                Icon(Icons.Default.Redo, contentDescription = "Redo", tint = NeonPurple)
            }

            IconButton(
                onClick = { viewModel.toggleDiffView() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (state.isDiffViewActive) NeonCyan.copy(alpha = 0.2f) else CyberSurface)
            ) {
                Icon(
                    Icons.Default.Difference,
                    contentDescription = "Diff View",
                    tint = if (state.isDiffViewActive) NeonCyan else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bookmarks Scroll Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Bookmark, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
            bookmarks.forEach { (label, hex) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberSurfaceVariant)
                        .clickable {
                            offsetInput = hex
                            viewModel.jumpToOffset(hex)
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$label ($hex)",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                placeholder = { Text("Search Hex (e.g. BD 04 00 12 or MOV W0, #0)", fontSize = 12.sp, color = TextMuted) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TextPrimary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = CyberBorder,
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface
                ),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { viewModel.searchHexPattern(searchInput) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonPurple, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Search", color = TextPrimary, fontSize = 12.sp)
            }
        }

        // Search Results Chips
        if (state.hexSearchResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Matches:", color = GlitchGreen, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterVertically))
                state.hexSearchResults.forEach { res ->
                    val offsetStr = String.format("0x%08X", res.offset)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GlitchGreen.copy(alpha = 0.2f))
                            .clickable {
                                offsetInput = offsetStr
                                viewModel.jumpToOffset(offsetStr)
                            }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(offsetStr, color = GlitchGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main View: Diff View vs Hex Table
        if (state.isDiffViewActive) {
            // Diff View Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ACTIVE APPLIED PATCHES (${state.hexPatches.size})",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.hexPatches.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No native patches applied yet.\nApply patches from known offsets or custom hex below.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.hexPatches) { patch ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberSurfaceVariant)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(patch.functionName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "0x${java.lang.Long.toHexString(patch.offset).uppercase()}",
                                            color = GlitchGreen,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text("Orig: ${patch.originalBytes}", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        Text("Patch: ${patch.patchedBytes}", color = HexModified, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }

                                    Button(
                                        onClick = {
                                            offsetInput = String.format("0x%08X", patch.offset)
                                            viewModel.jumpToOffset(offsetInput)
                                            viewModel.toggleDiffView()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberVoid),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("Inspect", color = NeonCyan, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Hex Table Grid
            Card(
                colors = CardDefaults.cardColors(containerColor = CodeBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Hex Table Column Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "OFFSET    ",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F  ",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ASCII",
                            color = NeonPurple,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(state.hexRows) { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 1.dp)
                            ) {
                                // Offset column
                                Text(
                                    text = "${row.offsetHex}  ",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )

                                // 16 bytes columns
                                for ((idx, b) in row.bytes.withIndex()) {
                                    val isModified = row.modifiedIndices.contains(idx)
                                    val hexStr = String.format("%02X", b)
                                    Text(
                                        text = hexStr,
                                        color = if (isModified) HexModified else HexHighlight,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isModified) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = if (idx == 7) "  " else " ",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(text = " ", fontSize = 11.sp)

                                // ASCII column
                                Text(
                                    text = row.ascii,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Patch Insertion Strip
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = patchBytesInput,
                    onValueChange = { patchBytesInput = it },
                    label = { Text("Patch Bytes (ARM64 Opcode)", fontSize = 10.sp, color = TextMuted) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = HexModified
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HexModified,
                        unfocusedBorderColor = CyberBorder,
                        focusedContainerColor = CyberVoid,
                        unfocusedContainerColor = CyberVoid
                    ),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        viewModel.applyHexPatch(
                            HexPatch(
                                id = "manual_${System.currentTimeMillis()}",
                                offset = state.hexBaseOffset,
                                originalBytes = "BD 04 00 12",
                                patchedBytes = patchBytesInput,
                                description = "Manual Opcode Patch",
                                functionName = "libminecraftpe::custom"
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HexModified),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Apply Patch", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
