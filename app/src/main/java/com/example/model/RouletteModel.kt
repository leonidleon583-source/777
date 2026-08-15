package com.example.model

import androidx.compose.ui.graphics.Color

enum class RouletteColor(val displayName: String, val color: Color, val hex: Long) {
    GREEN("Зелёный (Zero)", Color(0xFF10B981), 0xFF10B981),
    RED("Красный", Color(0xFFEF4444), 0xFFEF4444),
    BLACK("Чёрный", Color(0xFF1E293B), 0xFF1E293B)
}

enum class RouletteBetType(
    val title: String,
    val payoutMultiplier: Float,
    val description: String
) {
    RED("Красное", 2.0f, "Выигрыш x2.0 при выпадении красного числа"),
    BLACK("Чёрное", 2.0f, "Выигрыш x2.0 при выпадении чёрного числа"),
    GREEN_ZERO("Зеро 0", 14.0f, "Выигрыш x14.0 при выпадении 0"),
    EVEN("Чётное", 2.0f, "Выигрыш x2.0 (2,4,6...)"),
    ODD("Нечётное", 2.0f, "Выигрыш x2.0 (1,3,5...)"),
    LOW_RANGE("1 — 18", 2.0f, "Меньшие числа от 1 до 18"),
    HIGH_RANGE("19 — 36", 2.0f, "Большие числа от 19 до 36")
}

data class RouletteSector(
    val number: Int,
    val color: RouletteColor
)

object RouletteConfig {
    // 37 sectors in standard single-zero layout
    val RED_NUMBERS = setOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    val BLACK_NUMBERS = setOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)

    val WHEEL_ORDER = listOf(
        0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10,
        5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26
    ).map { num ->
        val col = when {
            num == 0 -> RouletteColor.GREEN
            RED_NUMBERS.contains(num) -> RouletteColor.RED
            else -> RouletteColor.BLACK
        }
        RouletteSector(num, col)
    }

    fun getSector(number: Int): RouletteSector {
        val col = when {
            number == 0 -> RouletteColor.GREEN
            RED_NUMBERS.contains(number) -> RouletteColor.RED
            else -> RouletteColor.BLACK
        }
        return RouletteSector(number, col)
    }
}

data class RouletteSpinResult(
    val winningSector: RouletteSector,
    val selectedBetType: RouletteBetType,
    val selectedNumber: Int?,
    val betAmount: Long,
    val winAmount: Long,
    val isWin: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
