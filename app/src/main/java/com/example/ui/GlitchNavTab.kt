package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.graphics.vector.ImageVector

enum class GlitchNavTab(val label: String, val icon: ImageVector) {
    WORKSPACE("Workspace", Icons.Default.Folder),
    DEX_SMALI("Dex/Smali", Icons.Default.Code),
    HEX_PATCHER("Hex Patcher", Icons.Default.Memory),
    NATIVE_HOOKS("ARM64 Hooks", Icons.Default.Build),
    MOD_MENU("Menu Builder", Icons.Default.Layers),
    MODULES("Modules", Icons.Default.Extension),
    PYTHON("Python", Icons.Default.Terminal),
    MANIFEST("Manifest", Icons.Default.DataObject),
    PACKAGING("Sign & Install", Icons.Default.Security)
}
