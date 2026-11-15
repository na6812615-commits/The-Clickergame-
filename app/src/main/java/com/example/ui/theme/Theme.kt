package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val TapCoreDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = ObsidianBgDeep,
    primaryContainer = CyberCyanDim,
    onPrimaryContainer = CyberCyan,
    secondary = NeonEmerald,
    onSecondary = ObsidianBgDeep,
    secondaryContainer = NeonEmeraldDim,
    onSecondaryContainer = NeonEmerald,
    tertiary = AmberGold,
    onTertiary = ObsidianBgDeep,
    tertiaryContainer = AmberGoldDim,
    onTertiaryContainer = AmberGold,
    error = CoralCrimson,
    onError = Color.White,
    errorContainer = CoralCrimsonDim,
    onErrorContainer = CoralCrimson,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = CarbonSurface,
    onSurface = TextPrimary,
    surfaceVariant = ElevatedCard,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = Color(0xFF1F2A40)
)

private val TapCoreShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun TapCoreTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TapCoreDarkColorScheme,
        typography = AppTypography,
        shapes = TapCoreShapes,
        content = content
    )
}
