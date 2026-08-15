package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Minimal Sleek Graphite Dark Theme
val ElegantDarkBackground = Color(0xFF101014)
val ElegantDarkSurface = Color(0xFF18181F)
val ElegantDarkSurfaceElevated = Color(0xFF22222B)
val ElegantDarkBorder = Color(0xFF2E2E3A)
val ElegantDarkBorderLight = Color(0xFF424252)

// Minimal Vibrant Accents
val AccentEmerald = Color(0xFF10B981)
val AccentEmeraldDark = Color(0xFF064E3B)
val AccentPurple = Color(0xFF8B5CF6)
val AccentPurpleLight = Color(0xFFDDD6FE)
val AccentGold = Color(0xFFF59E0B)
val AccentGoldLight = Color(0xFFFDE68A)
val AccentRed = Color(0xFFEF4444)
val AccentBlue = Color(0xFF3B82F6)

// Aliases for compatibility
val ElegantPurple = AccentPurple
val ElegantPurpleDark = Color(0xFF2E1065)
val ElegantPurpleContainer = Color(0xFF4C1D95)
val ElegantPurpleLight = AccentPurpleLight
val ElegantTextPrimary = Color(0xFFF3F4F6)
val ElegantTextSecondary = Color(0xFF9CA3AF)
val ElegantTextMuted = Color(0xFF6B7280)

val CasinoBackground = ElegantDarkBackground
val CasinoSurface = ElegantDarkSurface
val CasinoSurfaceElevated = ElegantDarkSurfaceElevated
val CasinoSurfaceBorder = ElegantDarkBorder

val GoldLight = AccentGoldLight
val GoldBase = AccentGold
val GoldDark = Color(0xFFB45309)
val GoldGlow = Color(0x33F59E0B)

val LuckyRed = AccentRed
val LuckyRedGlow = Color(0x33EF4444)
val NeonCyan = Color(0xFF38BDF8)
val NeonMagenta = Color(0xFFF43F5E)
val NeonGreen = AccentEmerald
val RoyalPurple = AccentPurple
val DiamondBlue = Color(0xFF60A5FA)
val AmberGlow = Color(0x33F59E0B)

// Minimalist Gradients
val ElegantCabinetGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1E1E26), Color(0xFF16161D), Color(0xFF111116))
)

val ElegantCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1C1C24), Color(0xFF14141A))
)

val ElegantPurpleGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA), Color(0xFF8B5CF6))
)

val SpinButtonGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFA78BFA), Color(0xFF8B5CF6), Color(0xFF7C3AED))
)

val ReelDrumGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF101014),
        Color(0x00101014),
        Color(0x00101014),
        Color(0xFF101014)
    )
)

val GoldGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFDE68A), Color(0xFFF59E0B), Color(0xFFD97706))
)

val GoldHorizontalGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2E2E3A), Color(0xFF8B5CF6), Color(0xFF2E2E3A))
)

val RedJackpotGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFEF4444), Color(0xFF7F1D1D))
)

val DarkCabinetGradient = ElegantCabinetGradient
