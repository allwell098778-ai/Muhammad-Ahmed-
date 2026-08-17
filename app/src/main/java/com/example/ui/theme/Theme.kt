package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Color(0xFF050B18),
    primaryContainer = Color(0x33F59E0B),
    onPrimaryContainer = Color(0xFFFDE68A),
    secondary = AmberLight,
    onSecondary = Color(0xFF050B18),
    secondaryContainer = GlassSurfaceVariantDark,
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = IbadahEmeraldLight,
    onTertiary = Color(0xFF050B18),
    tertiaryContainer = Color(0x3310B981),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = FrostedBackgroundDark,
    onBackground = DarkTextPrimary,
    surface = GlassSurfaceDark,
    onSurface = DarkTextPrimary,
    surfaceVariant = GlassSurfaceVariantDark,
    onSurfaceVariant = DarkTextSecondary,
    outline = GlassBorderDark,
    outlineVariant = Color(0x14FFFFFF),
    error = SignalBearish,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AmberDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = Color(0xFF0F172A),
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = IbadahEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = IbadahEmeraldDark,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = SignalBearish,
    onError = Color.White
)

@Composable
fun WorldEagleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
