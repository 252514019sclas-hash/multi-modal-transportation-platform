package com.simats.multimodaltransportationrecommendation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TourismLightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = SkyBlueLight,
    onPrimaryContainer = SkyBlueDark,
    secondary = TealSecondary,
    onSecondary = SurfaceWhite,
    secondaryContainer = TealLight,
    onSecondaryContainer = TealDark,
    tertiary = WarmOrangeAccent,
    onTertiary = SurfaceWhite,
    tertiaryContainer = WarmOrangeLight,
    onTertiaryContainer = WarmOrangeDark,
    background = BackgroundSoftCream,
    onBackground = TextNavyDark,
    surface = SurfaceWhite,
    onSurface = TextNavyDark,
    surfaceVariant = PastelCardBlue,
    onSurfaceVariant = TextNavyBody,
    outline = BorderLight
)

@Composable
fun MultiModalTransportationRecommendationTheme(
    darkTheme: Boolean = false, // Enforce bright tourism theme consistently
    content: @Composable () -> Unit
) {
    val colorScheme = TourismLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SurfaceWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
