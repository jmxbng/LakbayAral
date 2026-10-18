package com.example.lakbayaral.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

// ── Brand Colors ──────────────────────────────────────────────────────────────
val Gold        = Color(0xFFC8A24A)
val GoldLight   = Color(0xFFE8C87A)
val DarkBg      = Color(0xFF0B0F1A)
val DarkSurface = Color(0xFF111827)
val DarkCard    = Color(0xFF1A2035)
val NeonBlue    = Color(0xFF4DE3FF)
val NeonBlueD   = Color(0xFF00B8D4)
val WhiteGlow   = Color(0xFFEAEAEA)
val ErrorRed    = Color(0xFFFF4C4C)
val SuccessGreen= Color(0xFF4CFFA0)
val Overlay     = Color(0x80000000)

private val LakbayColorScheme = darkColorScheme(
    primary          = Gold,
    onPrimary        = DarkBg,
    primaryContainer = Color(0xFF2A1F00),
    secondary        = NeonBlue,
    onSecondary      = DarkBg,
    background       = DarkBg,
    onBackground     = WhiteGlow,
    surface          = DarkSurface,
    onSurface        = WhiteGlow,
    surfaceVariant   = DarkCard,
    error            = ErrorRed,
)

val AppTypography = Typography(
    displayLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 32.sp, color = Gold),
    displayMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 26.sp, color = WhiteGlow),
    titleLarge    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,   fontSize = 22.sp, color = WhiteGlow),
    titleMedium   = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = WhiteGlow),
    bodyLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, color = WhiteGlow),
    bodyMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color(0xFFB0B8CC)),
    labelLarge    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Gold),
)

@Composable
fun LakbayAralTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LakbayColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
