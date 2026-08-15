package com.example.data

import android.content.Context
import android.content.SharedPreferences

class CasinoPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("casino_777_prefs", Context.MODE_PRIVATE)

    var balance: Long
        get() = prefs.getLong(KEY_BALANCE, 10000L)
        set(value) = prefs.edit().putLong(KEY_BALANCE, value).apply()

    var progressiveJackpot: Long
        get() = prefs.getLong(KEY_JACKPOT, 777777L)
        set(value) = prefs.edit().putLong(KEY_JACKPOT, value).apply()

    var totalSpins: Long
        get() = prefs.getLong(KEY_TOTAL_SPINS, 0L)
        set(value) = prefs.edit().putLong(KEY_TOTAL_SPINS, value).apply()

    var totalWonCoins: Long
        get() = prefs.getLong(KEY_TOTAL_WON, 0L)
        set(value) = prefs.edit().putLong(KEY_TOTAL_WON, value).apply()

    var biggestWin: Long
        get() = prefs.getLong(KEY_BIGGEST_WIN, 0L)
        set(value) = prefs.edit().putLong(KEY_BIGGEST_WIN, value).apply()

    var jackpotsHitCount: Int
        get() = prefs.getInt(KEY_JACKPOTS_HIT, 0)
        set(value) = prefs.edit().putInt(KEY_JACKPOTS_HIT, value).apply()

    var lastDailyBonusTime: Long
        get() = prefs.getLong(KEY_LAST_DAILY_BONUS, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_DAILY_BONUS, value).apply()

    var nickname: String
        get() = prefs.getString(KEY_NICKNAME, "Игрок_777") ?: "Игрок_777"
        set(value) = prefs.edit().putString(KEY_NICKNAME, value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    companion object {
        private const val KEY_NICKNAME = "player_nickname"
        private const val KEY_BALANCE = "player_balance"
        private const val KEY_JACKPOT = "progressive_jackpot"
        private const val KEY_TOTAL_SPINS = "total_spins"
        private const val KEY_TOTAL_WON = "total_won"
        private const val KEY_BIGGEST_WIN = "biggest_win"
        private const val KEY_JACKPOTS_HIT = "jackpots_hit"
        private const val KEY_LAST_DAILY_BONUS = "last_daily_bonus"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    }
}
