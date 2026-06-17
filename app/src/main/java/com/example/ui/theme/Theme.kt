package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkTraditionalGold,
    secondary = DarkMutedBronze,
    tertiary = RiceWineWhite,
    background = CalligraphyInkBg,
    surface = CalligraphyInkSurface,
    onPrimary = CalligraphyInkBg,
    onSecondary = CalligraphyInkBg,
    onTertiary = CalligraphyInkBg,
    onBackground = TraditionalIvoryBg,
    onSurface = TraditionalIvoryBg
)

private val LightColorScheme = lightColorScheme(
    primary = TraditionalSlateBlack,
    secondary = MutedWarmSand,
    tertiary = TraditionalGoldAcc,
    background = TraditionalIvoryBg,
    surface = androidx.compose.ui.graphics.Color.White,
    primaryContainer = RicePaperCream,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = TraditionalSlateBlack,
    onSurface = TraditionalSlateBlack
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamicColor to enforce Kooksoondang Branded Colors strictly
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
