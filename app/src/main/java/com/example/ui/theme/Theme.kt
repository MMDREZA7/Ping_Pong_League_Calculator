package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = Zinc950,
    primaryContainer = Zinc800,
    onPrimaryContainer = PureWhite,
    secondary = Emerald400,
    onSecondary = Zinc950,
    secondaryContainer = Emerald950,
    onSecondaryContainer = Emerald400,
    tertiary = AmberGold,
    onTertiary = Zinc950,
    background = Zinc950,
    onBackground = Zinc100,
    surface = Zinc900,
    onSurface = Zinc100,
    surfaceVariant = Zinc850,
    onSurfaceVariant = Zinc400,
    outline = Zinc800,
    outlineVariant = Zinc700,
    error = RoseLoss,
    onError = PureWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Zinc950,
    onPrimary = PureWhite,
    primaryContainer = Zinc200,
    onPrimaryContainer = Zinc950,
    secondary = Emerald600,
    onSecondary = PureWhite,
    secondaryContainer = Emerald400.copy(alpha = 0.2f),
    onSecondaryContainer = Emerald900,
    tertiary = AmberGold,
    onTertiary = PureWhite,
    background = PureWhite,
    onBackground = Zinc950,
    surface = Zinc100,
    onSurface = Zinc950,
    surfaceVariant = Zinc200,
    onSurfaceVariant = Zinc600,
    outline = Zinc300,
    outlineVariant = Zinc200,
    error = RoseLoss,
    onError = PureWhite
)

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun PingPongTheme(
    darkTheme: Boolean = true, // Default to tournament night dark aesthetic
    dynamicColor: Boolean = false, // Keep intentional athletic dark identity by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}

// Alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PingPongTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
