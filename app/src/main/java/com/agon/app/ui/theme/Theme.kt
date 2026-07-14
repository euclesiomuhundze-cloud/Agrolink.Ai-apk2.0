package com.agon.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LeafGreen80,
    onPrimary = Color(0xFF0B3618),
    secondary = Amber80,
    onSecondary = Color(0xFF3E2500),
    tertiary = Clay80,
    background = DarkBackground,
    onBackground = Color(0xFFEDEDE5),
    surface = DarkSurface,
    onSurface = Color(0xFFEDEDE5),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC8D0C6),
    error = Color(0xFFFFB4AB),
)

private val LightColorScheme = lightColorScheme(
    primary = LeafGreen40,
    onPrimary = Color.White,
    secondary = Amber40,
    onSecondary = Color.White,
    tertiary = Clay40,
    background = LightBackground,
    onBackground = Color(0xFF1B1B16),
    surface = LightSurface,
    onSurface = Color(0xFF1B1B16),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4A4638),
    error = AlertRed,
)

@Composable
fun AgonAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
