package com.example.model

import androidx.compose.ui.graphics.Color

enum class SlotSymbol(
    val id: String,
    val displayName: String,
    val iconEmoji: String,
    val payout3: Int,      // Payout multiplier for 3 matching symbols
    val payout2: Int = 0,  // Payout multiplier for 2 matching symbols (e.g. Cherries)
    val accentColor: Color,
    val isSeven: Boolean = false,
    val isWild: Boolean = false,
    val isScatter: Boolean = false,
    val weight: Int = 10   // Probability weight for RNG reel strip distribution
) {
    // Top Tier (JACKPOT)
    SEVEN_RED(
        id = "seven_red",
        displayName = "Красная 7",
        iconEmoji = "7️⃣",
        payout3 = 777,
        accentColor = Color(0xFFFF1744),
        isSeven = true,
        weight = 3
    ),
    SEVEN_GOLD(
        id = "seven_gold",
        displayName = "Золотая 7",
        iconEmoji = "🔥",
        payout3 = 500,
        accentColor = Color(0xFFFFD700),
        isSeven = true,
        weight = 4
    ),

    // High Tier
    CROWN(
        id = "crown",
        displayName = "Корона",
        iconEmoji = "👑",
        payout3 = 250,
        accentColor = Color(0xFFFFB300),
        weight = 6
    ),
    DIAMOND(
        id = "diamond",
        displayName = "Бриллиант",
        iconEmoji = "💎",
        payout3 = 150,
        accentColor = Color(0xFF00E5FF),
        weight = 8
    ),
    BAR(
        id = "bar",
        displayName = "BAR",
        iconEmoji = "💵",
        payout3 = 100,
        accentColor = Color(0xFFFF9100),
        weight = 10
    ),
    BELL(
        id = "bell",
        displayName = "Колокол",
        iconEmoji = "🔔",
        payout3 = 60,
        accentColor = Color(0xFFFFEA00),
        weight = 12
    ),

    // Fruit / Mid Tier
    GRAPES(
        id = "grapes",
        displayName = "Виноград",
        iconEmoji = "🍇",
        payout3 = 30,
        accentColor = Color(0xFFD500F9),
        weight = 16
    ),
    LEMON(
        id = "lemon",
        displayName = "Лимон",
        iconEmoji = "🍋",
        payout3 = 20,
        accentColor = Color(0xFFEEFF41),
        weight = 20
    ),
    CHERRY(
        id = "cherry",
        displayName = "Вишня",
        iconEmoji = "🍒",
        payout3 = 15,
        payout2 = 3,
        accentColor = Color(0xFFFF1744),
        weight = 24
    ),

    // Special Symbols
    WILD(
        id = "wild",
        displayName = "WILD",
        iconEmoji = "🃏",
        payout3 = 300,
        accentColor = Color(0xFF00E676),
        isWild = true,
        weight = 5
    ),
    SCATTER_STAR(
        id = "scatter",
        displayName = "СКАТТЕР",
        iconEmoji = "⭐",
        payout3 = 50,
        accentColor = Color(0xFFFF007F),
        isScatter = true,
        weight = 5
    );

    companion object {
        val ALL_SYMBOLS = entries.toList()
        
        /** Classic 777 standard reel strip sequence */
        val CLASSIC_STRIP = listOf(
            SEVEN_RED, CHERRY, LEMON, BAR, BELL, 
            GRAPES, SEVEN_GOLD, CHERRY, DIAMOND, 
            LEMON, CROWN, BELL, WILD, CHERRY, 
            GRAPES, BAR, SCATTER_STAR, LEMON, SEVEN_RED,
            BELL, CHERRY, DIAMOND, SEVEN_GOLD, BAR
        )

        /** Neon Vegas strip sequence */
        val NEON_STRIP = listOf(
            SEVEN_RED, DIAMOND, CHERRY, WILD, BELL,
            LEMON, SEVEN_GOLD, GRAPES, BAR, CHERRY,
            CROWN, SCATTER_STAR, LEMON, SEVEN_RED,
            BELL, DIAMOND, CHERRY, SEVEN_GOLD, BAR
        )

        /** Royal Gold strip sequence */
        val ROYAL_STRIP = listOf(
            SEVEN_GOLD, CROWN, DIAMOND, SEVEN_RED, BAR,
            BELL, WILD, CHERRY, LEMON, SCATTER_STAR,
            CROWN, SEVEN_GOLD, DIAMOND, BAR, BELL,
            GRAPES, SEVEN_RED, CROWN
        )
    }
}
