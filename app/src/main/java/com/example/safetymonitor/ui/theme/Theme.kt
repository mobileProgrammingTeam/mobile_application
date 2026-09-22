package com.example.safetymonitor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary         = PrimaryLight,
    onPrimary       = Color.Black,
    background      = Surface,
    surface         = Surface,
    surfaceVariant  = SurfaceVar,
    onBackground    = OnSurface,
    onSurface       = OnSurface,
    onSurfaceVariant = OnSurfaceVar,
    error           = DangerRed
)

private val LightColorScheme = lightColorScheme(
    primary         = Primary,
    onPrimary       = Color.White,
    background      = Color(0xFFF5F5F5),
    surface         = Color.White,
    surfaceVariant  = Color(0xFFECEFF1),
    onBackground    = Color(0xFF212121),
    onSurface       = Color(0xFF212121),
    onSurfaceVariant = Color(0xFF546E7A),
    error           = DangerRed
)

@Composable
fun SafetyMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography  = AppTypography,
        content     = content
    )
}
