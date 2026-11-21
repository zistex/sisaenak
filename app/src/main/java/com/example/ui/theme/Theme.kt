package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val SisaEnakLightColorScheme = lightColorScheme(
    primary = ActionOrange,
    onPrimary = Color.White,
    primaryContainer = ActionOrangeSoft,
    onPrimaryContainer = ActionOrangeDark,
    secondary = FreshGreen,
    onSecondary = Color.White,
    secondaryContainer = FreshGreenSoft,
    onSecondaryContainer = FreshGreenDark,
    tertiary = FreshGreenDark,
    onTertiary = Color.White,
    background = BgLightGray,
    onBackground = TextBlack,
    surface = BgWhite,
    onSurface = TextBlack,
    surfaceVariant = BgLightGray,
    onSurfaceVariant = TextGray,
    outline = SurfaceBorder,
    error = LiveRed,
    onError = Color.White
)

private val SisaEnakShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SisaEnakTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SisaEnakLightColorScheme,
        typography = SisaEnakTypography,
        shapes = SisaEnakShapes,
        content = content
    )
}
