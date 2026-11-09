package com.yunfei.quant.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val BrandNavy = Color(0xFF0C2D5A)
val BrandCyan = Color(0xFF009FB5)
val BrandGreen = Color(0xFF5ED679)
val BrandRed = Color(0xFFE53935)
val BrandOrange = Color(0xFFFF9800)

private val LightColors = lightColorScheme(
    primary = BrandNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E3F7),
    onPrimaryContainer = BrandNavy,
    secondary = BrandCyan,
    onSecondary = Color.White,
    tertiary = BrandGreen,
    error = BrandRed,
    background = Color(0xFFF7F9FC),
    surface = Color.White,
    surfaceVariant = Color(0xFFEDF1F7),
    onSurface = Color(0xFF111827),
    onSurfaceVariant = Color(0xFF5B6675),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7BA6E8),
    onPrimary = Color(0xFF04182F),
    primaryContainer = Color(0xFF14375F),
    onPrimaryContainer = Color(0xFFD6E3F7),
    secondary = Color(0xFF4DD0E1),
    onSecondary = Color(0xFF00201F),
    tertiary = BrandGreen,
    error = Color(0xFFFF6B6B),
    background = Color(0xFF0A0E14),
    surface = Color(0xFF141A23),
    surfaceVariant = Color(0xFF1D2530),
    onSurface = Color(0xFFE8EDF5),
    onSurfaceVariant = Color(0xFF97A3B4),
)

data class SemanticColors(
    val success: Color,
    val warning: Color,
    val danger: Color,
    val successContainer: Color,
    val warningContainer: Color,
    val dangerContainer: Color,
)

val LocalSemanticColors = staticCompositionLocalOf {
    SemanticColors(
        BrandGreen, BrandOrange, BrandRed,
        BrandGreen.copy(alpha = .12f), BrandOrange.copy(alpha = .12f), BrandRed.copy(alpha = .12f),
    )
}

@Composable
fun YunFeiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val semantic = if (darkTheme) SemanticColors(
        success = BrandGreen,
        warning = Color(0xFFFFB74D),
        danger = Color(0xFFFF6B6B),
        successContainer = BrandGreen.copy(alpha = .16f),
        warningContainer = Color(0xFFFFB74D).copy(alpha = .16f),
        dangerContainer = Color(0xFFFF6B6B).copy(alpha = .16f),
    ) else SemanticColors(
        success = Color(0xFF2E7D32),
        warning = Color(0xFFE65100),
        danger = BrandRed,
        successContainer = BrandGreen.copy(alpha = .18f),
        warningContainer = BrandOrange.copy(alpha = .14f),
        dangerContainer = BrandRed.copy(alpha = .10f),
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalSemanticColors provides semantic) {
        MaterialTheme(colorScheme = colors, content = content)
    }
}

val semanticColors: SemanticColors
    @Composable get() = LocalSemanticColors.current
