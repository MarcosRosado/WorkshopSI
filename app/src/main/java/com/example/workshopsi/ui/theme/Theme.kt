package com.example.workshopsi.ui.theme

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

private val DarkPokemonColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    background = Color(0xFF121212), // Example dark background
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E), // Example dark surface
    onSurface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

private val LightPokemonColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
    background = PokemonYellow, // Testing with PokemonYellow
    onBackground = PokemonRedDark, // Testing with PokemonRedDark
    surface = PokemonBlue, // Testing with PokemonBlue
    onSurface = PokemonYellowDark, // Testing with PokemonYellowDark
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

@Composable
fun WorkshopSITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkPokemonColorScheme
        else -> LightPokemonColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming you have Typography.kt
        content = content
    )
}
