package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AdminAudioPlayer
import com.example.audio.AdminAudioState
import com.example.audio.CasinoAudioEngine
import com.example.audio.CasinoHaptics
import com.example.data.CasinoPreferences
import com.example.model.CasinoTask
import com.example.model.GuessMode
import com.example.model.GuessRoundResult
import com.example.model.LineWin
import com.example.model.P2PChatMessage
import com.example.model.P2PPlayer
import com.example.model.P2PRoomState
import com.example.model.Payline
import com.example.model.RouletteBetType
import com.example.model.RouletteConfig
import com.example.model.RouletteSector
import com.example.model.SlotEvaluator
import com.example.model.SlotGridMode
import com.example.model.SlotMachineTheme
import com.example.model.SlotModifier
import com.example.model.SlotSymbol
import com.example.model.TaskCategory
import com.example.model.TaskType
import com.example.model.WinTier
import com.example.ui.components.ReelAnimState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

data class CasinoUiState(
    val balance: Long = 10000L,
    val progressiveJackpot: Long = 777777L,
    val nickname: String = "Игрок_777",
    val selectedTheme: SlotMachineTheme = SlotMachineTheme.CLASSIC_777,
    val selectedGridMode: SlotGridMode = SlotGridMode.GRID_3X3,
    val activeModifier: SlotModifier = SlotModifier.NONE,
    val betPerLine: Long = 20L,
    val activeLinesCount: Int = 5,
    val isSpinning: Boolean = false,
    val isAutoSpinning: Boolean = false,
    val autoSpinsLeft: Int = 0,
    val freeSpinsLeft: Int = 0,
    val freeSpinMultiplier: Int = 1,
    val lastWinAmount: Long = 0L,
    val activeLineWins: List<LineWin> = emptyList(),
    val totalSpins: Long = 0L,
    val totalWon: Long = 0L,
    val biggestWin: Long = 0L,
    val jackpotsHit: Int = 0,
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val celebrationTier: WinTier = WinTier.NONE,
    val celebrationCoins: Long = 0L,
    val showPaytable: Boolean = false,
    val showStats: Boolean = false,
    val showBonusWheel: Boolean = false,
    val showRefillDialog: Boolean = false,
    val showNicknameDialog: Boolean = false,
    val showTasksDialog: Boolean = false,
    val showAdminDialog: Boolean = false,
    val currentScreen: CasinoScreen = CasinoScreen.LOBBY,

    // Tasks State
    val tasks: List<CasinoTask> = emptyList(),

    // Admin & Abuse State
    val isAdminUnlocked: Boolean = false,
    val isGodModeActive: Boolean = false,
    val forcedJackpotNextSpin: Boolean = false,
    val forcedMultiplierNextSpin: Int = 1,
    val adminAudioState: AdminAudioState = AdminAudioState(),
    val isDiscoStrobeActive: Boolean = false,

    // Roulette state
    val rouletteBetType: RouletteBetType = RouletteBetType.RED,
    val rouletteSelectedNumber: Int? = null,
    val rouletteBetAmount: Long = 50L,
    val isRouletteSpinning: Boolean = false,
    val lastWinningSector: RouletteSector? = null,
    val rouletteTargetDegrees: Float = 0f,
    val rouletteHistory: List<RouletteSector> = listOf(
        RouletteConfig.getSector(7),
        RouletteConfig.getSector(0),
        RouletteConfig.getSector(18),
        RouletteConfig.getSector(22)
    ),
    val lastRouletteWin: Long = 0L,
    val lastRouletteMessage: String = "",

    // Guess Number state
    val guessMode: GuessMode = GuessMode.MORE_THAN_50,
    val guessExactNumber: Int = 50,
    val guessBetAmount: Long = 50L,
    val isGuessRevealing: Boolean = false,
    val revealedNumber: Int? = null,
    val lastGuessWin: Long = 0L,
    val lastGuessMessage: String = "",
    val guessHistory: List<GuessRoundResult> = emptyList(),

    // P2P Room state
    val p2pState: P2PRoomState = P2PRoomState()
)

enum class CasinoScreen {
    LOBBY,
    SLOT_GAME,
    ROULETTE,
    GUESS_NUMBER,
    P2P_ROOM
}

class CasinoViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = CasinoPreferences(application)
    val audioEngine = CasinoAudioEngine()
    val adminAudioPlayer = AdminAudioPlayer(application)
    val haptics = CasinoHaptics(application)

    private val _uiState = MutableStateFlow(CasinoUiState())
    val uiState: StateFlow<CasinoUiState> = _uiState.asStateFlow()

    // 9 Reel Animation States to support 3x1, 3x3, and 9x5
    val reelStates: List<ReelAnimState> = List(9) { index ->
        ReelAnimState(index, SlotMachineTheme.CLASSIC_777.reelStrip)
    }

    private var autoSpinJob: Job? = null
    private var spinJob: Job? = null
    private var rouletteJob: Job? = null
    private var guessJob: Job? = null

    init {
        val savedNick = prefs.nickname
        val initialTasks = createInitialTasks()

        _uiState.update {
            it.copy(
                balance = prefs.balance,
                progressiveJackpot = prefs.progressiveJackpot,
                nickname = savedNick,
                totalSpins = prefs.totalSpins,
                totalWon = prefs.totalWonCoins,
                biggestWin = prefs.biggestWin,
                jackpotsHit = prefs.jackpotsHitCount,
                isSoundEnabled = prefs.isSoundEnabled,
                isVibrationEnabled = prefs.isVibrationEnabled,
                tasks = initialTasks,
                p2pState = it.p2pState.copy(
                    myNickname = savedNick,
                    leaderboard = listOf(
                        P2PPlayer("p1", savedNick, prefs.balance, (prefs.totalSpins / 3).toInt(), prefs.biggestWin, isHost = true),
                        P2PPlayer("p2", "Alex_Vegas", 28500L, 42, 12000L),
                        P2PPlayer("p3", "CasinoKing", 15400L, 19, 7500L),
                        P2PPlayer("p4", "LuckyStrike", 9200L, 11, 4500L)
                    )
                )
            )
        }
        audioEngine.isMuted = !prefs.isSoundEnabled
        haptics.isEnabled = prefs.isVibrationEnabled

        viewModelScope.launch {
            adminAudioPlayer.audioState.collect { audioState ->
                _uiState.update {
                    it.copy(
                        adminAudioState = audioState,
                        isDiscoStrobeActive = audioState.isDiscoStrobeActive
                    )
                }
            }
        }

        initializeReels(SlotMachineTheme.CLASSIC_777)
    }

    private fun createInitialTasks(): List<CasinoTask> {
        return listOf(
            // SEQUENTIAL CHAIN QUESTS (1 -> 2 -> 3 -> 4 -> 5 -> 6)
            CasinoTask(
                id = "seq_1",
                title = "⚡ Быстрый старт в 3x1",
                description = "Сделайте 5 спинов на классической сетке 3x1",
                type = TaskType.SEQUENTIAL,
                sequenceOrder = 1,
                category = TaskCategory.SLOTS,
                targetCount = 5,
                rewardCoins = 2500L,
                isUnlocked = true
            ),
            CasinoTask(
                id = "seq_2",
                title = "🔴 Ставка на Рулетку",
                description = "Сыграйте 3 раунда в европейскую рулетку",
                type = TaskType.SEQUENTIAL,
                sequenceOrder = 2,
                category = TaskCategory.ROULETTE,
                targetCount = 3,
                rewardCoins = 4000L,
                isUnlocked = false
            ),
            CasinoTask(
                id = "seq_3",
                title = "🎲 Шестое чувство",
                description = "Сыграйте 3 раунда в «Угадай число»",
                type = TaskType.SEQUENTIAL,
                sequenceOrder = 3,
                category = TaskCategory.GUESS_NUMBER,
                targetCount = 3,
                rewardCoins = 6000L,
                isUnlocked = false
            ),
            CasinoTask(
                id = "seq_4",
                title = "🎰 Мастер Сетки 3x3",
                description = "Сделайте 10 спинов на популярной сетке 3x3",
                type = TaskType.SEQUENTIAL,
                sequenceOrder = 4,
                category = TaskCategory.SLOTS,
                targetCount = 10,
                rewardCoins = 10000L,
                isUnlocked = false
            ),
            CasinoTask(
                id = "seq_5",
                title = "💥 Мега-Сетка 9x5",
                description = "Сделайте 5 спинов на масштабной сетке 9x5",
                type = TaskType.SEQUENTIAL,
                sequenceOrder = 5,
                category = TaskCategory.SLOTS,
                targetCount = 5,
                rewardCoins = 15000L,
                isUnlocked = false
            ),
            CasinoTask(
                id = "seq_6",
                title = "👑 Чемпион Казино 777",
                description = "Сделайте еще 25 любых спинов и заберите Супер-Приз",
                type = TaskType.SEQUENTIAL,
                sequenceOrder = 6,
                category = TaskCategory.SLOTS,
                targetCount = 25,
                rewardCoins = 50000L,
                isUnlocked = false
            ),

            // PERSISTENT (ALL-TIME / DAILY) TASKS
            CasinoTask(
                id = "pers_1",
                title = "🔥 Азартный марафон",
                description = "Сделайте 20 любых спинов в игровых слотах",
                type = TaskType.PERSISTENT,
                category = TaskCategory.SLOTS,
                targetCount = 20,
                rewardCoins = 3500L,
                isUnlocked = true
            ),
            CasinoTask(
                id = "pers_2",
                title = "🎡 Вращение Колеса Удачи",
                description = "Прокрутите бонусное Колесо Фортуны",
                type = TaskType.PERSISTENT,
                category = TaskCategory.WHEEL,
                targetCount = 1,
                rewardCoins = 2000L,
                isUnlocked = true
            ),
            CasinoTask(
                id = "pers_3",
                title = "🔴 Красное или Чёрное",
                description = "Сделайте 5 ставок в рулетке",
                type = TaskType.PERSISTENT,
                category = TaskCategory.ROULETTE,
                targetCount = 5,
                rewardCoins = 4500L,
                isUnlocked = true
            ),
            CasinoTask(
                id = "pers_4",
                title = "👥 Общение в P2P",
                description = "Отправьте сообщение в P2P чате комнаты",
                type = TaskType.PERSISTENT,
                category = TaskCategory.P2P,
                targetCount = 2,
                rewardCoins = 3000L,
                isUnlocked = true
            )
        )
    }

    private fun initializeReels(theme: SlotMachineTheme) {
        val strip = theme.reelStrip
        reelStates.forEach { reel ->
            val startIdx = Random.nextInt(strip.size)
            reel.targetSymbols = listOf(
                strip[(startIdx) % strip.size],
                strip[(startIdx + 1) % strip.size],
                strip[(startIdx + 2) % strip.size],
                strip[(startIdx + 3) % strip.size],
                strip[(startIdx + 4) % strip.size]
            )
        }
    }

    // ==========================================
    // NAVIGATION
    // ==========================================
    fun navigateToLobby() {
        stopAutoSpin()
        _uiState.update { it.copy(currentScreen = CasinoScreen.LOBBY) }
    }

    fun navigateToSlotGame(mode: SlotGridMode = SlotGridMode.GRID_3X3) {
        _uiState.update {
            it.copy(
                selectedGridMode = mode,
                activeLinesCount = mode.defaultLines,
                currentScreen = CasinoScreen.SLOT_GAME
            )
        }
    }

    fun navigateToRoulette() {
        _uiState.update { it.copy(currentScreen = CasinoScreen.ROULETTE) }
    }

    fun navigateToGuessNumber() {
        _uiState.update { it.copy(currentScreen = CasinoScreen.GUESS_NUMBER) }
    }

    fun navigateToP2PRoom() {
        _uiState.update { it.copy(currentScreen = CasinoScreen.P2P_ROOM) }
    }

    fun selectGridMode(mode: SlotGridMode) {
        if (_uiState.value.isSpinning) return
        audioEngine.playButtonClick()
        _uiState.update {
            it.copy(
                selectedGridMode = mode,
                activeLinesCount = mode.defaultLines,
                activeLineWins = emptyList(),
                lastWinAmount = 0L
            )
        }
    }

    // ==========================================
    // SOUND / VIBRATION / NICKNAME
    // ==========================================
    fun toggleSound() {
        val newState = !_uiState.value.isSoundEnabled
        prefs.isSoundEnabled = newState
        audioEngine.isMuted = !newState
        _uiState.update { it.copy(isSoundEnabled = newState) }
    }

    fun toggleVibration() {
        val newState = !_uiState.value.isVibrationEnabled
        prefs.isVibrationEnabled = newState
        haptics.isEnabled = newState
        _uiState.update { it.copy(isVibrationEnabled = newState) }
    }

    fun setNickname(newNick: String) {
        val trimmed = newNick.trim().ifEmpty { "Игрок_777" }
        prefs.nickname = trimmed
        _uiState.update { state ->
            val updatedP2P = state.p2pState.copy(
                myNickname = trimmed,
                leaderboard = state.p2pState.leaderboard.map { p ->
                    if (p.isHost || p.nickname == state.nickname) p.copy(nickname = trimmed) else p
                }
            )
            state.copy(nickname = trimmed, p2pState = updatedP2P, showNicknameDialog = false)
        }
    }

    fun openNicknameDialog() {
        _uiState.update { it.copy(showNicknameDialog = true) }
    }

    fun closeNicknameDialog() {
        _uiState.update { it.copy(showNicknameDialog = false) }
    }

    // ==========================================
    // TASKS SYSTEM & PROGRESS TRACKING
    // ==========================================
    fun openTasksDialog() {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(showTasksDialog = true) }
    }

    fun closeTasksDialog() {
        _uiState.update { it.copy(showTasksDialog = false) }
    }

    private fun incrementTaskProgress(category: TaskCategory, countIncrement: Int = 1) {
        _uiState.update { state ->
            val updatedTasks = state.tasks.map { task ->
                if (task.category == category && task.isUnlocked && !task.isClaimed) {
                    val newCount = task.currentCount + countIncrement
                    val isDone = newCount >= task.targetCount
                    task.copy(currentCount = newCount, isCompleted = isDone)
                } else {
                    task
                }
            }
            state.copy(tasks = updatedTasks)
        }
    }

    fun claimTaskReward(taskId: String) {
        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.MEDIUM)
        haptics.winRumble()

        _uiState.update { state ->
            var rewardToAdd = 0L
            var claimedSequenceOrder = -1

            val updatedTasks = state.tasks.map { task ->
                if (task.id == taskId && task.isCompleted && !task.isClaimed) {
                    rewardToAdd = task.rewardCoins
                    if (task.type == TaskType.SEQUENTIAL) {
                        claimedSequenceOrder = task.sequenceOrder
                    }
                    task.copy(isClaimed = true)
                } else {
                    task
                }
            }

            // Unlock next task in the sequential chain!
            val unlockedNextTasks = if (claimedSequenceOrder > 0) {
                val nextOrder = claimedSequenceOrder + 1
                updatedTasks.map { task ->
                    if (task.type == TaskType.SEQUENTIAL && task.sequenceOrder == nextOrder) {
                        task.copy(isUnlocked = true)
                    } else {
                        task
                    }
                }
            } else {
                updatedTasks
            }

            val newBalance = state.balance + rewardToAdd
            prefs.balance = newBalance

            state.copy(
                balance = newBalance,
                tasks = unlockedNextTasks
            )
        }
    }

    // ==========================================
    // SECRET ADMIN PANEL & ABUSES (CODE: 148867)
    // ==========================================
    fun openAdminDialog() {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(showAdminDialog = true) }
    }

    fun closeAdminDialog() {
        _uiState.update { it.copy(showAdminDialog = false) }
    }

    fun verifyAdminCode(code: String): Boolean {
        if (code.trim() == "148867") {
            audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.JACKPOT_777)
            haptics.jackpotRumble()
            _uiState.update { it.copy(isAdminUnlocked = true) }
            return true
        }
        audioEngine.playLossSound()
        return false
    }

    fun adminForceJackpotNextSpin() {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(forcedJackpotNextSpin = true) }
        haptics.jackpotRumble()
    }

    fun adminForceMegaMultiplier(multiplier: Int) {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(forcedMultiplierNextSpin = multiplier) }
        haptics.winRumble()
    }

    fun adminGiveFreeSpins(count: Int) {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(freeSpinsLeft = it.freeSpinsLeft + count, freeSpinMultiplier = 5) }
        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.BIG)
    }

    fun adminToggleGodMode(enable: Boolean) {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(isGodModeActive = enable) }
    }

    fun adminAddBalance(amount: Long) {
        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.BIG)
        val newBalance = _uiState.value.balance + amount
        prefs.balance = newBalance
        _uiState.update { it.copy(balance = newBalance) }
    }

    fun adminSetJackpot(amount: Long) {
        audioEngine.playButtonClick()
        prefs.progressiveJackpot = amount
        _uiState.update { it.copy(progressiveJackpot = amount) }
    }

    fun adminToggleDiscoRave(active: Boolean) {
        adminAudioPlayer.toggleDiscoStrobe(active)
        _uiState.update { it.copy(isDiscoStrobeActive = active) }
    }

    fun adminSelectCustomAudio(uri: Uri) {
        adminAudioPlayer.loadAndPlayCustomSong(uri)
    }

    fun adminPlayAudio() {
        adminAudioPlayer.play()
    }

    fun adminPauseAudio() {
        adminAudioPlayer.pause()
    }

    fun adminStopAudio() {
        adminAudioPlayer.stop()
    }

    fun adminPlayPresetSound(preset: String) {
        when (preset) {
            "SIREN" -> audioEngine.playLossSound()
            "FANFARE" -> audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.JACKPOT_777)
            "BASSBOOST" -> audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.BIG)
            "LAUGH" -> audioEngine.playButtonClick()
        }
        haptics.winRumble()
    }

    fun adminBroadcastAudioToP2P(trackTitle: String) {
        val title = trackTitle.ifEmpty { "Секретный трек хоста" }
        addP2PMessage("👑 АДМИН ВКЛЮЧИЛ ТРЕК: $title 🎵🔥", isSystem = true)
        _uiState.update {
            it.copy(
                p2pState = it.p2pState.copy(activeTrackTitle = title)
            )
        }
    }

    fun adminSendTrollAlert(alertText: String) {
        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.BIG)
        haptics.jackpotRumble()
        addP2PMessage(alertText, isSystem = true)
    }

    // ==========================================
    // SLOTS CONTROLS & ANTI-BUG BET GUARDS
    // ==========================================
    fun setBetPerLine(bet: Long) {
        if (_uiState.value.isSpinning) return
        audioEngine.playButtonClick()
        _uiState.update { it.copy(betPerLine = bet.coerceIn(5L, 1000L)) }
    }

    fun increaseBet() {
        if (_uiState.value.isSpinning) return
        val current = _uiState.value.betPerLine
        val next = when {
            current < 20 -> current + 5
            current < 50 -> current + 10
            current < 100 -> current + 25
            current < 500 -> current + 50
            else -> current + 100
        }
        setBetPerLine(next)
    }

    fun decreaseBet() {
        if (_uiState.value.isSpinning) return
        val current = _uiState.value.betPerLine
        val next = when {
            current <= 10 -> 5
            current <= 20 -> current - 5
            current <= 50 -> current - 10
            current <= 100 -> current - 25
            current <= 500 -> current - 50
            else -> current - 100
        }
        setBetPerLine(next)
    }

    fun setMaxBet() {
        if (_uiState.value.isSpinning) return
        audioEngine.playButtonClick()
        val maxLineBet = (_uiState.value.balance / maxOf(1, _uiState.value.activeLinesCount))
            .coerceIn(5L, 500L)
        _uiState.update { it.copy(betPerLine = maxLineBet) }
    }

    fun setActiveLines(count: Int) {
        if (_uiState.value.isSpinning) return
        audioEngine.playButtonClick()
        _uiState.update { it.copy(activeLinesCount = count.coerceIn(1, 5)) }
    }

    fun toggleAutoSpin() {
        audioEngine.playButtonClick()
        if (_uiState.value.isAutoSpinning) {
            stopAutoSpin()
        } else {
            startAutoSpin(25)
        }
    }

    private fun startAutoSpin(count: Int) {
        _uiState.update { it.copy(isAutoSpinning = true, autoSpinsLeft = count) }
        spin()
    }

    fun stopAutoSpin() {
        autoSpinJob?.cancel()
        _uiState.update { it.copy(isAutoSpinning = false, autoSpinsLeft = 0) }
    }

    fun spin() {
        if (_uiState.value.isSpinning) return

        val state = _uiState.value
        val gridMode = state.selectedGridMode
        val isFreeSpin = state.freeSpinsLeft > 0
        val totalBet = when (gridMode) {
            SlotGridMode.GRID_3X1 -> state.betPerLine
            SlotGridMode.GRID_3X3 -> state.betPerLine * state.activeLinesCount
            SlotGridMode.GRID_9X5 -> state.betPerLine * 10
        }

        if (!isFreeSpin && state.balance < totalBet) {
            stopAutoSpin()
            _uiState.update { it.copy(showRefillDialog = true) }
            return
        }

        spinJob?.cancel()
        spinJob = viewModelScope.launch {
            val newBalance = if (isFreeSpin) state.balance else state.balance - totalBet
            val newFreeSpinsLeft = if (isFreeSpin) state.freeSpinsLeft - 1 else 0
            val newJackpot = state.progressiveJackpot + (totalBet / 20)

            prefs.balance = newBalance
            prefs.progressiveJackpot = newJackpot

            val isForcedJackpot = state.forcedJackpotNextSpin
            val forcedMultiplier = state.forcedMultiplierNextSpin
            val modifier = if (forcedMultiplier > 1) SlotModifier.BUFF_7 else SlotModifier.rollRandomModifier()

            _uiState.update {
                it.copy(
                    balance = newBalance,
                    progressiveJackpot = newJackpot,
                    freeSpinsLeft = newFreeSpinsLeft,
                    isSpinning = true,
                    activeModifier = modifier,
                    activeLineWins = emptyList(),
                    lastWinAmount = 0L,
                    celebrationTier = WinTier.NONE,
                    forcedJackpotNextSpin = false,
                    forcedMultiplierNextSpin = 1
                )
            }

            audioEngine.playSpinStart()
            audioEngine.startReelSpinningLoop()
            haptics.tick()

            val theme = state.selectedTheme
            val strip = theme.reelStrip
            val colsCount = gridMode.cols
            val rowsCount = gridMode.rows
            val targetGrid = mutableListOf<List<SlotSymbol>>()

            for (col in 0 until colsCount) {
                val targetColumn = if (isForcedJackpot) {
                    List(rowsCount) { SlotSymbol.SEVEN_RED }
                } else {
                    val topIndex = getRandomStripIndex(strip)
                    (0 until rowsCount).map { r -> strip[(topIndex + r) % strip.size] }
                }
                targetGrid.add(targetColumn)
                if (col < reelStates.size) {
                    reelStates[col].targetSymbols = targetColumn
                    reelStates[col].isSpinning = true
                }
            }

            if (colsCount == 3) {
                delay(900)
                reelStates[0].isSpinning = false
                audioEngine.playReelStop(0)
                haptics.reelStop()

                delay(400)
                reelStates[1].isSpinning = false
                audioEngine.playReelStop(1)
                haptics.reelStop()

                delay(500)
                reelStates[2].isSpinning = false
                audioEngine.playReelStop(2)
                audioEngine.stopReelSpinningLoop()
                haptics.reelStop()
            } else {
                delay(1200)
                for (c in 0 until colsCount) {
                    if (c < reelStates.size) {
                        reelStates[c].isSpinning = false
                    }
                }
                audioEngine.playReelStop(0)
                audioEngine.stopReelSpinningLoop()
                haptics.reelStop()
            }

            delay(200)

            val activePaylines = Payline.getActiveLines(state.activeLinesCount)
            val multiplier = if (isFreeSpin) state.freeSpinMultiplier else 1
            val baseEvaluation = SlotEvaluator.evaluateSpin(
                grid = targetGrid,
                activeLines = activePaylines,
                betPerLine = state.betPerLine,
                freeSpinMultiplier = multiplier,
                gridMode = gridMode,
                modifier = modifier
            )

            val totalWinCalculated = if (forcedMultiplier > 1) {
                maxOf(5000L, baseEvaluation.totalWinCoins * forcedMultiplier)
            } else {
                baseEvaluation.totalWinCoins
            }

            var finalBalance = newBalance + totalWinCalculated
            var jackpotWinBonus = 0L

            if (baseEvaluation.isJackpot777 || isForcedJackpot) {
                jackpotWinBonus = newJackpot
                finalBalance += jackpotWinBonus
                prefs.jackpotsHitCount += 1
                prefs.progressiveJackpot = 500000L
            }

            val grandTotalWin = totalWinCalculated + jackpotWinBonus

            val totalSpins = state.totalSpins + 1
            val totalWon = state.totalWon + grandTotalWin
            val biggestWin = maxOf(state.biggestWin, grandTotalWin)

            prefs.balance = finalBalance
            prefs.totalSpins = totalSpins
            prefs.totalWonCoins = totalWon
            prefs.biggestWin = biggestWin

            var freeSpins = newFreeSpinsLeft
            var freeMultiplier = state.freeSpinMultiplier
            if (baseEvaluation.freeSpinsAwarded > 0) {
                freeSpins += baseEvaluation.freeSpinsAwarded
                freeMultiplier = 2
            }

            val effectiveWinTier = when {
                baseEvaluation.isJackpot777 || isForcedJackpot -> WinTier.JACKPOT_777
                grandTotalWin >= totalBet * 50 -> WinTier.MEGA_WIN
                grandTotalWin >= totalBet * 15 -> WinTier.BIG_WIN
                grandTotalWin > 0 -> WinTier.REGULAR
                else -> WinTier.NONE
            }

            if (grandTotalWin > 0) {
                when (effectiveWinTier) {
                    WinTier.JACKPOT_777 -> {
                        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.JACKPOT_777)
                        haptics.jackpotRumble()
                    }
                    WinTier.MEGA_WIN, WinTier.BIG_WIN -> {
                        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.BIG)
                        haptics.winRumble()
                    }
                    WinTier.REGULAR -> {
                        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.SMALL)
                        haptics.winRumble()
                    }
                    WinTier.NONE -> {}
                }
            } else {
                audioEngine.playLossSound()
            }

            val shouldShowCelebration = effectiveWinTier == WinTier.BIG_WIN ||
                    effectiveWinTier == WinTier.MEGA_WIN ||
                    effectiveWinTier == WinTier.JACKPOT_777

            if (grandTotalWin > 0 && state.p2pState.isConnected) {
                addP2PMessage("${state.nickname} выиграл $grandTotalWin ₴ в слотах! 🔥", isSystem = true)
            }

            // Track task progress!
            incrementTaskProgress(TaskCategory.SLOTS, 1)

            _uiState.update {
                it.copy(
                    balance = finalBalance,
                    progressiveJackpot = if (baseEvaluation.isJackpot777 || isForcedJackpot) 500000L else newJackpot,
                    isSpinning = false,
                    lastWinAmount = grandTotalWin,
                    activeLineWins = baseEvaluation.lineWins,
                    freeSpinsLeft = freeSpins,
                    freeSpinMultiplier = freeMultiplier,
                    totalSpins = totalSpins,
                    totalWon = totalWon,
                    biggestWin = biggestWin,
                    jackpotsHit = prefs.jackpotsHitCount,
                    celebrationTier = if (shouldShowCelebration) effectiveWinTier else WinTier.NONE,
                    celebrationCoins = if (shouldShowCelebration) grandTotalWin else 0L
                )
            }

            if (_uiState.value.isAutoSpinning) {
                val nextLeft = _uiState.value.autoSpinsLeft - 1
                if (nextLeft > 0 && finalBalance >= totalBet) {
                    _uiState.update { it.copy(autoSpinsLeft = nextLeft) }
                    delay(if (shouldShowCelebration) 3000 else 1200)
                    spin()
                } else {
                    stopAutoSpin()
                }
            }
        }
    }

    private fun getRandomStripIndex(strip: List<SlotSymbol>): Int {
        val totalWeight = strip.sumOf { it.weight }
        var randomWeight = Random.nextInt(totalWeight)
        for (i in strip.indices) {
            randomWeight -= strip[i].weight
            if (randomWeight <= 0) return i
        }
        return Random.nextInt(strip.size)
    }

    // ==========================================
    // ROULETTE LOGIC & ANTI-BUG CONTROLS & GOD MODE
    // ==========================================
    fun setRouletteBetType(betType: RouletteBetType) {
        if (_uiState.value.isRouletteSpinning) return
        audioEngine.playButtonClick()
        _uiState.update { it.copy(rouletteBetType = betType, rouletteSelectedNumber = null) }
    }

    fun setRouletteExactNumber(num: Int) {
        if (_uiState.value.isRouletteSpinning) return
        audioEngine.playButtonClick()
        _uiState.update {
            it.copy(
                rouletteSelectedNumber = num.coerceIn(0, 36),
                rouletteBetType = RouletteBetType.GREEN_ZERO
            )
        }
    }

    fun setRouletteBetAmount(amount: Long) {
        if (_uiState.value.isRouletteSpinning) return
        audioEngine.playButtonClick()
        _uiState.update { it.copy(rouletteBetAmount = amount.coerceIn(10L, 50000L)) }
    }

    fun spinRoulette() {
        if (_uiState.value.isRouletteSpinning) return
        val state = _uiState.value
        val bet = state.rouletteBetAmount

        if (state.balance < bet) {
            _uiState.update { it.copy(showRefillDialog = true) }
            return
        }

        rouletteJob?.cancel()
        rouletteJob = viewModelScope.launch {
            val newBalance = state.balance - bet
            prefs.balance = newBalance

            // Pick winning sector (God Mode guarantees player wins!)
            val winningSector = if (state.isGodModeActive) {
                when (state.rouletteBetType) {
                    RouletteBetType.RED -> RouletteConfig.WHEEL_ORDER.first { it.color == com.example.model.RouletteColor.RED }
                    RouletteBetType.BLACK -> RouletteConfig.WHEEL_ORDER.first { it.color == com.example.model.RouletteColor.BLACK }
                    RouletteBetType.GREEN_ZERO -> {
                        if (state.rouletteSelectedNumber != null) {
                            RouletteConfig.getSector(state.rouletteSelectedNumber)
                        } else {
                            RouletteConfig.getSector(0)
                        }
                    }
                    RouletteBetType.EVEN -> RouletteConfig.WHEEL_ORDER.first { it.number > 0 && it.number % 2 == 0 }
                    RouletteBetType.ODD -> RouletteConfig.WHEEL_ORDER.first { it.number > 0 && it.number % 2 != 0 }
                    RouletteBetType.LOW_RANGE -> RouletteConfig.getSector(7)
                    RouletteBetType.HIGH_RANGE -> RouletteConfig.getSector(22)
                }
            } else {
                RouletteConfig.WHEEL_ORDER[Random.nextInt(RouletteConfig.WHEEL_ORDER.size)]
            }

            val winningIndex = RouletteConfig.WHEEL_ORDER.indexOf(winningSector)
            val sectorAngle = 360f / RouletteConfig.WHEEL_ORDER.size
            val targetDegrees = (360f * 6) + (360f - (winningIndex * sectorAngle))

            _uiState.update {
                it.copy(
                    balance = newBalance,
                    isRouletteSpinning = true,
                    rouletteTargetDegrees = targetDegrees,
                    lastRouletteWin = 0L,
                    lastRouletteMessage = "Колесо рулетки вращается..."
                )
            }

            audioEngine.playSpinStart()
            haptics.tick()

            for (i in 0..10) {
                delay(300)
                audioEngine.playBallBounce()
                haptics.tick()
            }
            delay(1200)

            val isWin = when (state.rouletteBetType) {
                RouletteBetType.RED -> winningSector.color == com.example.model.RouletteColor.RED
                RouletteBetType.BLACK -> winningSector.color == com.example.model.RouletteColor.BLACK
                RouletteBetType.GREEN_ZERO -> {
                    if (state.rouletteSelectedNumber != null) {
                        winningSector.number == state.rouletteSelectedNumber
                    } else {
                        winningSector.number == 0
                    }
                }
                RouletteBetType.EVEN -> winningSector.number > 0 && winningSector.number % 2 == 0
                RouletteBetType.ODD -> winningSector.number > 0 && winningSector.number % 2 != 0
                RouletteBetType.LOW_RANGE -> winningSector.number in 1..18
                RouletteBetType.HIGH_RANGE -> winningSector.number in 19..36
            }

            val winMultiplier = if (isWin) {
                if (state.rouletteSelectedNumber != null) 36.0f else state.rouletteBetType.payoutMultiplier
            } else 0.0f

            val winAmount = (bet * winMultiplier).toLong()
            val finalBalance = newBalance + winAmount
            prefs.balance = finalBalance

            val newHistory = (listOf(winningSector) + state.rouletteHistory).take(12)

            if (isWin) {
                audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.BIG)
                haptics.winRumble()
                if (state.p2pState.isConnected) {
                    addP2PMessage("${state.nickname} выиграл $winAmount ₴ в рулетке (${winningSector.number})! 🎉", isSystem = true)
                }
            } else {
                audioEngine.playLossSound()
            }

            val resultMsg = if (isWin) {
                "ВЫИГРЫШ: +$winAmount ₴! Выпало ${winningSector.number} (${winningSector.color.displayName})"
            } else {
                "Выпало ${winningSector.number} (${winningSector.color.displayName}). Не повезло!"
            }

            incrementTaskProgress(TaskCategory.ROULETTE, 1)

            _uiState.update {
                it.copy(
                    balance = finalBalance,
                    isRouletteSpinning = false,
                    lastWinningSector = winningSector,
                    rouletteHistory = newHistory,
                    lastRouletteWin = winAmount,
                    lastRouletteMessage = resultMsg
                )
            }
        }
    }

    // ==========================================
    // GUESS NUMBER LOGIC & ANTI-BUG & GOD MODE
    // ==========================================
    fun setGuessMode(mode: GuessMode) {
        if (_uiState.value.isGuessRevealing) return
        audioEngine.playButtonClick()
        _uiState.update { it.copy(guessMode = mode) }
    }

    fun setGuessExactNumber(num: Int) {
        if (_uiState.value.isGuessRevealing) return
        _uiState.update { it.copy(guessExactNumber = num.coerceIn(1, 100), guessMode = GuessMode.EXACT) }
    }

    fun setGuessBetAmount(amount: Long) {
        if (_uiState.value.isGuessRevealing) return
        audioEngine.playButtonClick()
        _uiState.update { it.copy(guessBetAmount = amount.coerceIn(10L, 50000L)) }
    }

    fun playGuessNumber() {
        if (_uiState.value.isGuessRevealing) return
        val state = _uiState.value
        val bet = state.guessBetAmount

        if (state.balance < bet) {
            _uiState.update { it.copy(showRefillDialog = true) }
            return
        }

        guessJob?.cancel()
        guessJob = viewModelScope.launch {
            val newBalance = state.balance - bet
            prefs.balance = newBalance

            _uiState.update {
                it.copy(
                    balance = newBalance,
                    isGuessRevealing = true,
                    revealedNumber = null,
                    lastGuessWin = 0L,
                    lastGuessMessage = "Карта открывается..."
                )
            }

            audioEngine.playSpinStart()
            haptics.tick()
            delay(1200)

            val targetNumber = if (state.isGodModeActive) {
                when (state.guessMode) {
                    GuessMode.MORE_THAN_50 -> 77
                    GuessMode.LESS_THAN_50 -> 25
                    GuessMode.EVEN -> 42
                    GuessMode.ODD -> 33
                    GuessMode.TIER_1 -> 15
                    GuessMode.TIER_2 -> 50
                    GuessMode.TIER_3 -> 88
                    GuessMode.EXACT -> state.guessExactNumber
                }
            } else {
                Random.nextInt(1, 101)
            }

            val isWin = when (state.guessMode) {
                GuessMode.MORE_THAN_50 -> targetNumber > 50
                GuessMode.LESS_THAN_50 -> targetNumber < 50
                GuessMode.EVEN -> targetNumber % 2 == 0
                GuessMode.ODD -> targetNumber % 2 != 0
                GuessMode.TIER_1 -> targetNumber in 1..33
                GuessMode.TIER_2 -> targetNumber in 34..66
                GuessMode.TIER_3 -> targetNumber in 67..100
                GuessMode.EXACT -> targetNumber == state.guessExactNumber
            }

            val winMultiplier = if (isWin) state.guessMode.multiplier else 0.0f
            val winAmount = (bet * winMultiplier).toLong()
            val finalBalance = newBalance + winAmount
            prefs.balance = finalBalance

            audioEngine.playRevealFlip()

            if (isWin) {
                audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.MEDIUM)
                haptics.winRumble()
                if (state.p2pState.isConnected) {
                    addP2PMessage("${state.nickname} угадал число $targetNumber и выиграл $winAmount ₴! 🧠", isSystem = true)
                }
            } else {
                audioEngine.playLossSound()
            }

            val msg = if (isWin) {
                "УГАДАЛИ! Выпало $targetNumber! Выигрыш: +$winAmount ₴"
            } else {
                "Число: $targetNumber. Не совпало! -$bet ₴"
            }

            val resultEntry = GuessRoundResult(
                targetNumber = targetNumber,
                chosenMode = state.guessMode,
                exactChosenNumber = if (state.guessMode == GuessMode.EXACT) state.guessExactNumber else null,
                betAmount = bet,
                winAmount = winAmount,
                isWin = isWin
            )

            incrementTaskProgress(TaskCategory.GUESS_NUMBER, 1)

            _uiState.update {
                it.copy(
                    balance = finalBalance,
                    isGuessRevealing = false,
                    revealedNumber = targetNumber,
                    lastGuessWin = winAmount,
                    lastGuessMessage = msg,
                    guessHistory = (listOf(resultEntry) + it.guessHistory).take(10)
                )
            }
        }
    }

    // ==========================================
    // P2P ROOM & LEADERBOARD & CHAT
    // ==========================================
    fun createP2PRoom() {
        audioEngine.playButtonClick()
        val code = (10000 + Random.nextInt(90000)).toString() // 5-digit code
        val myNick = _uiState.value.nickname
        val hostPlayer = P2PPlayer("host_${UUID.randomUUID()}", myNick, _uiState.value.balance, 10, _uiState.value.biggestWin, isHost = true)

        val initialMessages = listOf(
            P2PChatMessage("m1", "Система", "Комната #$code создана. Поделитесь 5-значным кодом с другом для подключения!", isSystem = true)
        )

        _uiState.update { state ->
            state.copy(
                p2pState = state.p2pState.copy(
                    roomCode = code,
                    isConnected = true,
                    isHost = true,
                    players = listOf(hostPlayer),
                    messages = initialMessages
                )
            )
        }
    }

    fun joinP2PRoom(code: String) {
        val trimmed = code.trim()
        if (trimmed.length != 5) return
        audioEngine.playButtonClick()

        val myNick = _uiState.value.nickname
        val friendNick = "Друг_${Random.nextInt(100, 999)}"
        val me = P2PPlayer("me_${UUID.randomUUID()}", myNick, _uiState.value.balance, 5, _uiState.value.biggestWin)
        val friend = P2PPlayer("friend_${UUID.randomUUID()}", friendNick, 18500L, 8, 4200L, isHost = true)

        val messages = listOf(
            P2PChatMessage("m1", "Система", "Успешное подключение к комнате #$trimmed! Игроки готовы.", isSystem = true),
            P2PChatMessage("m2", friendNick, "Привет! Удачной игры, крути рулетку или 777! 🔥")
        )

        _uiState.update { state ->
            state.copy(
                p2pState = state.p2pState.copy(
                    roomCode = trimmed,
                    isConnected = true,
                    isHost = false,
                    players = listOf(friend, me),
                    messages = messages
                )
            )
        }
    }

    fun leaveP2PRoom() {
        audioEngine.playButtonClick()
        _uiState.update { state ->
            state.copy(
                p2pState = state.p2pState.copy(
                    roomCode = "",
                    isConnected = false,
                    isHost = false,
                    players = emptyList(),
                    messages = emptyList(),
                    activeTrackTitle = null
                )
            )
        }
    }

    fun sendP2PMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        audioEngine.playButtonClick()
        val myNick = _uiState.value.nickname
        addP2PMessage(trimmed, sender = myNick)
        incrementTaskProgress(TaskCategory.P2P, 1)
    }

    private fun addP2PMessage(text: String, sender: String = "Система", isSystem: Boolean = false) {
        val msg = P2PChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = sender,
            text = text,
            isSystem = isSystem
        )
        _uiState.update { state ->
            state.copy(
                p2pState = state.p2pState.copy(
                    messages = state.p2pState.messages + msg
                )
            )
        }
    }

    // ==========================================
    // BONUSES & DIALOGS
    // ==========================================
    fun dismissCelebration() {
        _uiState.update { it.copy(celebrationTier = WinTier.NONE, celebrationCoins = 0L) }
    }

    fun claimEmergencyBonus() {
        val bonus = 2500L
        val newBalance = _uiState.value.balance + bonus
        prefs.balance = newBalance
        _uiState.update { it.copy(balance = newBalance, showRefillDialog = false) }
        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.SMALL)
    }

    fun claimDailyBonus(coins: Long) {
        val newBalance = _uiState.value.balance + coins
        prefs.balance = newBalance
        prefs.lastDailyBonusTime = System.currentTimeMillis()
        incrementTaskProgress(TaskCategory.WHEEL, 1)
        _uiState.update { it.copy(balance = newBalance, showBonusWheel = false) }
        audioEngine.playWinSound(CasinoAudioEngine.WinSoundTier.BIG)
    }

    fun openBonusWheel() {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(showBonusWheel = true) }
    }

    fun closeBonusWheel() {
        _uiState.update { it.copy(showBonusWheel = false) }
    }

    fun openPaytable() {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(showPaytable = true) }
    }

    fun closePaytable() {
        _uiState.update { it.copy(showPaytable = false) }
    }

    fun openStats() {
        audioEngine.playButtonClick()
        _uiState.update { it.copy(showStats = true) }
    }

    fun closeStats() {
        _uiState.update { it.copy(showStats = false) }
    }

    fun closeRefillDialog() {
        _uiState.update { it.copy(showRefillDialog = false) }
    }

    override fun onCleared() {
        super.onCleared()
        adminAudioPlayer.stop()
    }
}
