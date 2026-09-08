package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.ui.GlitchNavTab
import com.example.ui.GlitchViewModel
import com.example.ui.screens.DexSmaliScreen
import com.example.ui.screens.FeatureModulesScreen
import com.example.ui.screens.HexPatcherScreen
import com.example.ui.screens.HookFrameworkScreen
import com.example.ui.screens.ManifestEditorScreen
import com.example.ui.screens.ModMenuBuilderScreen
import com.example.ui.screens.PackagingScreen
import com.example.ui.screens.ScriptingScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.GlitchGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val glitchViewModel: GlitchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GlitchEngineApp(glitchViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlitchEngineApp(viewModel: GlitchViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberVoid,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(end = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonCyan.copy(alpha = 0.2f))
                                    .border(1.dp, NeonCyan, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Memory,
                                    contentDescription = "Logo",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "GLITCH ENGINE",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp
                                )
                                Text(
                                    text = "MCPE ${state.selectedMcpeVersion} • arm64-v8a",
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Active patches badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlitchGreen.copy(alpha = 0.2f))
                                .border(1.dp, GlitchGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${state.hexPatches.size} Patches",
                                color = GlitchGreen,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberSurface
                )
            )
        },
        bottomBar = {
            Column {
                // Secondary scrollable tab row for full quick access across all 9 modules
                ScrollableTabRow(
                    selectedTabIndex = GlitchNavTab.entries.indexOf(state.currentTab),
                    containerColor = CyberSurface,
                    contentColor = NeonCyan,
                    edgePadding = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    GlitchNavTab.entries.forEach { tab ->
                        val isSelected = state.currentTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}"),
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) NeonCyan else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // User Feedback Banner (Auto dismissible)
            AnimatedVisibility(
                visible = state.userMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.userMessage?.let { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberSurfaceVariant)
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearUserMessage() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Screen Content Router
            Box(modifier = Modifier.fillMaxSize()) {
                when (state.currentTab) {
                    GlitchNavTab.WORKSPACE -> WorkspaceScreen(viewModel)
                    GlitchNavTab.HEX_PATCHER -> HexPatcherScreen(viewModel)
                    GlitchNavTab.DEX_SMALI -> DexSmaliScreen(viewModel)
                    GlitchNavTab.MANIFEST -> ManifestEditorScreen(viewModel)
                    GlitchNavTab.NATIVE_HOOKS -> HookFrameworkScreen(viewModel)
                    GlitchNavTab.MOD_MENU -> ModMenuBuilderScreen(viewModel)
                    GlitchNavTab.MODULES -> FeatureModulesScreen(viewModel)
                    GlitchNavTab.PYTHON -> ScriptingScreen(viewModel)
                    GlitchNavTab.PACKAGING -> PackagingScreen(viewModel)
                }
            }
        }
    }
}
