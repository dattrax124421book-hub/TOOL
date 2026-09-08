package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

@Composable
fun ManifestEditorScreen(viewModel: GlitchViewModel) {
    val state by viewModel.uiState.collectAsState()

    val commonPermissions = listOf(
        "android.permission.SYSTEM_ALERT_WINDOW" to "Overlay Window (Required for Mod Menu)",
        "android.permission.INTERNET" to "Network / Server Connections",
        "android.permission.FOREGROUND_SERVICE" to "Keep Overlay Alive in Background",
        "android.permission.WRITE_EXTERNAL_STORAGE" to "World Export / Import",
        "android.permission.READ_EXTERNAL_STORAGE" to "Texture Packs & Scripts"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Quick Injection Bar
        Text(
            text = "QUICK PERMISSION INJECTION (ONE-TAP)",
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
            commonPermissions.forEach { (perm, desc) ->
                val isPresent = state.manifestContent.contains(perm)
                Button(
                    onClick = { viewModel.injectPermission(perm) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPresent) GlitchGreen.copy(alpha = 0.2f) else CyberSurfaceVariant
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.border(
                        1.dp,
                        if (isPresent) GlitchGreen else CyberBorder,
                        RoundedCornerShape(6.dp)
                    )
                ) {
                    Icon(
                        imageVector = if (isPresent) Icons.Default.Security else Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = if (isPresent) GlitchGreen else NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = perm.substringAfterLast("."),
                        color = if (isPresent) GlitchGreen else TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Service & Receiver Injection Strip
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = NeonPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("GlitchOverlayService", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Required for floating menu over MCPE render view", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Button(
                    onClick = { viewModel.injectOverlayService() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Inject Service", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Manifest XML Editor
        Card(
            colors = CardDefaults.cardColors(containerColor = CodeBackground),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "AndroidManifest.xml (Decoded AXML)",
                        color = WarningAmber,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Auto-recompiled on save",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = state.manifestContent,
                    onValueChange = { viewModel.updateManifestText(it) },
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
                        .testTag("manifest_editor_text")
                )
            }
        }
    }
}
