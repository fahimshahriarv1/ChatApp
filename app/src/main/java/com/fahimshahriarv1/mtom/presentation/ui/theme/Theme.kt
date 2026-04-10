package com.fahimshahriarv1.mtom.presentation.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimaryDark,
    secondary = AppPrimaryVariantDark,
    tertiary = AppPrimaryVariantDark,
    background = AppBackgroundDark,
    surface = AppSurfaceDark,
    onPrimary = AppOnPrimaryDark,
    onSecondary = AppOnPrimaryDark,
    onTertiary = AppOnPrimaryDark,
    onBackground = AppOnBackgroundDark,
    onSurface = AppOnSurfaceDark,
    error = AppErrorDark
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    secondary = AppPrimaryVariant,
    tertiary = AppPrimaryVariant,
    background = AppBackground,
    surface = AppSurface,
    onPrimary = AppOnPrimary,
    onSecondary = AppOnPrimary,
    onTertiary = AppOnPrimary,
    onBackground = AppOnBackground,
    onSurface = AppOnSurface,
    error = AppError
)

@Composable
fun MtoMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
