package com.posturemind.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = BgCard,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Accent,
    onSecondary = TextPrimary,
    secondaryContainer = AccentLight,
    onSecondaryContainer = TextPrimary,
    tertiary = Success,
    onTertiary = BgCard,
    background = Bg,
    onBackground = TextPrimary,
    surface = BgCard,
    onSurface = TextPrimary,
    surfaceVariant = Bg,
    onSurfaceVariant = TextSecondary,
    error = Danger,
    onError = BgCard,
    outline = Border
)

private val DarkColors = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = PrimaryDark,
    primaryContainer = Primary,
    onPrimaryContainer = BgCard,
    secondary = Accent,
    onSecondary = TextPrimary,
    background = PrimaryDark,
    onBackground = Bg,
    surface = Color(0xFF1E293B),
    onSurface = Bg
)

@Composable
fun PostureMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = PostureMindTypography,
        content = content
    )
}
