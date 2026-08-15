package com.example.model

import androidx.compose.ui.graphics.Color

enum class SlotGridMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val cols: Int,
    val rows: Int,
    val defaultLines: Int,
    val badge: String
) {
    GRID_3X1(
        id = "3x1",
        title = "Классический 3x1",
        subtitle = "1 линия • 3 барабана • Быстрая игра",
        cols = 3,
        rows = 1,
        defaultLines = 1,
        badge = "3x1"
    ),
    GRID_3X3(
        id = "3x3",
        title = "Стандартный 3x3",
        subtitle = "5 линий • 3 барабана • Баланс",
        cols = 3,
        rows = 3,
        defaultLines = 5,
        badge = "3x3"
    ),
    GRID_9X5(
        id = "9x5",
        title = "Мега Сетка 9x5",
        subtitle = "45 ячеек • Множественные комбо • Экстрим",
        cols = 9,
        rows = 5,
        defaultLines = 15,
        badge = "9x5 МЕГА"
    );

    companion object {
        val ALL_MODES = entries.toList()
    }
}

enum class SlotModifier(
    val label: String,
    val multiplier: Float,
    val isDebuff: Boolean,
    val badgeColor: Color,
    val description: String
) {
    NONE("1x", 1.0f, false, Color.Transparent, "Стандарт"),
    BUFF_1_5("x1.5", 1.5f, false, Color(0xFF60A5FA), "Усиление +50%"),
    BUFF_2("x2", 2.0f, false, Color(0xFF34D399), "Двойной куш"),
    BUFF_3("x3", 3.0f, false, Color(0xFF10B981), "Тройной куш"),
    BUFF_4("x4", 4.0f, false, Color(0xFFFBBF24), "x4 Множитель"),
    BUFF_5("x5", 5.0f, false, Color(0xFFF59E0B), "x5 Множитель"),
    BUFF_6("x6", 6.0f, false, Color(0xFFEC4899), "x6 Множитель"),
    BUFF_7("x7", 7.0f, false, Color(0xFF8B5CF6), "x7 МАКСИМУМ"),
    DEBUFF_0_5("x0.5", 0.5f, true, Color(0xFFEF4444), "Дебафф: половинный выигрыш");

    companion object {
        fun rollRandomModifier(): SlotModifier {
            val roll = kotlin.random.Random.nextInt(100)
            return when {
                roll < 50 -> NONE // 50% обычный
                roll < 65 -> BUFF_1_5 // 15%
                roll < 77 -> BUFF_2 // 12%
                roll < 85 -> BUFF_3 // 8%
                roll < 90 -> BUFF_4 // 5%
                roll < 94 -> BUFF_5 // 4%
                roll < 96 -> BUFF_6 // 2%
                roll < 97 -> BUFF_7 // 1%
                else -> DEBUFF_0_5 // 3% редкий дебафф
            }
        }
    }
}
