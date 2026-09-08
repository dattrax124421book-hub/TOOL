package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GlitchDarkColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = CyberVoid,
  primaryContainer = NeonCyanDark,
  onPrimaryContainer = TextPrimary,
  secondary = NeonPurple,
  onSecondary = CyberVoid,
  secondaryContainer = NeonPurpleDark,
  onSecondaryContainer = TextPrimary,
  tertiary = GlitchGreen,
  onTertiary = CyberVoid,
  background = CyberVoid,
  onBackground = TextPrimary,
  surface = CyberSurface,
  onSurface = TextPrimary,
  surfaceVariant = CyberSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  error = DangerRed,
  onError = CyberVoid,
  outline = CyberBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = GlitchDarkColorScheme,
    typography = Typography,
    content = content
  )
}
