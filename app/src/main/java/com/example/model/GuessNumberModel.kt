package com.example.model

enum class GuessMode(
    val title: String,
    val multiplier: Float,
    val description: String
) {
    MORE_THAN_50("Больше 50 (>50)", 2.0f, "Числа от 51 до 100"),
    LESS_THAN_50("Меньше 50 (<50)", 2.0f, "Числа от 1 до 49"),
    EVEN("Чётное", 2.0f, "Любое чётное число (2, 4, 6...)"),
    ODD("Нечётное", 2.0f, "Любое нечётное число (1, 3, 5...)"),
    TIER_1("1 — 33", 3.0f, "Первая треть (1..33)"),
    TIER_2("34 — 66", 3.0f, "Вторая треть (34..66)"),
    TIER_3("67 — 100", 3.0f, "Третья треть (67..100)"),
    EXACT("Точное число (1..100)", 50.0f, "Угадай в точности конкретное число")
}

data class GuessRoundResult(
    val targetNumber: Int,
    val chosenMode: GuessMode,
    val exactChosenNumber: Int?,
    val betAmount: Long,
    val winAmount: Long,
    val isWin: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
