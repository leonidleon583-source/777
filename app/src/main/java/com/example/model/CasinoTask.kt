package com.example.model

enum class TaskType {
    SEQUENTIAL,  // Открываются строго по цепочке (след. только после завершения и получения награды предыдущего)
    PERSISTENT   // Постоянные/ежедневные, доступны всё время
}

enum class TaskCategory {
    SLOTS,
    ROULETTE,
    GUESS_NUMBER,
    WHEEL,
    P2P,
    GENERAL
}

data class CasinoTask(
    val id: String,
    val title: String,
    val description: String,
    val type: TaskType,
    val sequenceOrder: Int = 0, // Для SEQUENTIAL цепочки
    val category: TaskCategory,
    val targetCount: Int,
    val currentCount: Int = 0,
    val rewardCoins: Long,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val isUnlocked: Boolean = true // Для цепочки: открыто ли сейчас
)
