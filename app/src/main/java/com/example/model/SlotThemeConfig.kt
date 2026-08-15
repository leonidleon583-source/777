package com.example.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantPurple
import com.example.ui.theme.ElegantPurpleDark

enum class SlotMachineTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val cabinetGradient: Brush,
    val frameBorderColor: Color,
    val rtpPercent: String,
    val reelStrip: List<SlotSymbol>,
    val badge: String
) {
    CLASSIC_777(
        id = "classic_777",
        title = "Три Семёрки 777",
        subtitle = "Элегантный темный Лас-Вегас",
        primaryAccent = ElegantPurple,
        secondaryAccent = Color(0xFFFFD700),
        cabinetGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF2E2C34), Color(0xFF232228), Color(0xFF19181C))
        ),
        frameBorderColor = ElegantDarkBorder,
        rtpPercent = "97.2%",
        reelStrip = SlotSymbol.CLASSIC_STRIP,
        badge = "ТОП ВЫБОР"
    ),
    NEON_VEGAS(
        id = "neon_vegas",
        title = "Неон Вегас",
        subtitle = "Кибер-слоты с фиолетовым неоном",
        primaryAccent = Color(0xFF80D8FF),
        secondaryAccent = ElegantPurple,
        cabinetGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF252834), Color(0xFF1E202B), Color(0xFF15161E))
        ),
        frameBorderColor = ElegantDarkBorder,
        rtpPercent = "96.8%",
        reelStrip = SlotSymbol.NEON_STRIP,
        badge = "НЕОН"
    ),
    ROYAL_GOLD(
        id = "royal_gold",
        title = "Королевское Золото",
        subtitle = "Максимальные множители и лаванда",
        primaryAccent = Color(0xFFFFD54F),
        secondaryAccent = ElegantPurple,
        cabinetGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF332B38), Color(0xFF261F2B), Color(0xFF1A151E))
        ),
        frameBorderColor = ElegantDarkBorder,
        rtpPercent = "97.5%",
        reelStrip = SlotSymbol.ROYAL_STRIP,
        badge = "VIP"
    )
}
