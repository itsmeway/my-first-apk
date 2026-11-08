package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF042619),
    primaryContainer = Color(0xFF0C4A34),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = GoldAccent,
    onSecondary = Color(0xFF281900),
    secondaryContainer = Color(0xFF422C05),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = CoralExpense,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF4C0D1D),
    onTertiaryContainer = Color(0xFFFECDD3),
    background = DarkBackground,
    onBackground = Color(0xFFECFDF5),
    surface = DarkSurface,
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFA7C4BC),
    outline = DarkOutline,
    error = CoralExpense
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = CoralExpense,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4E6),
    onTertiaryContainer = Color(0xFF881337),
    background = LightBackground,
    onBackground = Color(0xFF0A1F18),
    surface = LightSurface,
    onSurface = Color(0xFF0A1F18),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF3D5E54),
    outline = LightOutline,
    error = CoralExpense
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek luxury dark financial cockpit
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
