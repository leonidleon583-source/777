package com.example.model

data class P2PPlayer(
    val id: String,
    val nickname: String,
    val balance: Long,
    val winsCount: Int,
    val biggestWin: Long,
    val isHost: Boolean = false,
    val isReady: Boolean = true,
    val roleTitle: String = if (isHost) "👑 Хост (Админ)" else "👤 Друг (Игрок)",
    val hasAdminBoost: Boolean = false,
    val boostDescription: String = ""
)

data class P2PChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val isSystem: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class P2PRoomState(
    val roomCode: String = "",
    val isConnected: Boolean = false,
    val isHost: Boolean = false,
    val myNickname: String = "Игрок_777",
    val players: List<P2PPlayer> = emptyList(),
    val messages: List<P2PChatMessage> = emptyList(),
    val leaderboard: List<P2PPlayer> = emptyList(),
    val activeTrackTitle: String? = null,
    val isDiscoStrobeActive: Boolean = false
)
