package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AdminAbuseDialog
import com.example.ui.screens.DailyBonusDialog
import com.example.ui.screens.GuessNumberScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.NicknameDialog
import com.example.ui.screens.P2PRoomScreen
import com.example.ui.screens.PaytableDialog
import com.example.ui.screens.RefillDialog
import com.example.ui.screens.RouletteScreen
import com.example.ui.screens.SlotGameScreen
import com.example.ui.screens.StatsDialog
import com.example.ui.screens.TasksDialog
import com.example.ui.theme.CasinoBackground
import com.example.ui.theme.CasinoTheme
import com.example.viewmodel.CasinoScreen
import com.example.viewmodel.CasinoViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CasinoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CasinoTheme {
                CasinoApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CasinoApp(viewModel: CasinoViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize(),
        containerColor = CasinoBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CasinoBackground)
        ) {
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = {
                    if (targetState != CasinoScreen.LOBBY) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    CasinoScreen.LOBBY -> {
                        LobbyScreen(
                            balance = uiState.balance,
                            jackpot = uiState.progressiveJackpot,
                            nickname = uiState.nickname,
                            tasks = uiState.tasks,
                            isSoundEnabled = uiState.isSoundEnabled,
                            isVibrationEnabled = uiState.isVibrationEnabled,
                            onSelectGridMode = { mode -> viewModel.navigateToSlotGame(mode) },
                            onOpenRoulette = { viewModel.navigateToRoulette() },
                            onOpenGuessNumber = { viewModel.navigateToGuessNumber() },
                            onOpenP2P = { viewModel.navigateToP2PRoom() },
                            onOpenTasks = { viewModel.openTasksDialog() },
                            onOpenBonusWheel = { viewModel.openBonusWheel() },
                            onOpenStats = { viewModel.openStats() },
                            onOpenNicknameDialog = { viewModel.openNicknameDialog() },
                            onOpenAdminSecret = { viewModel.openAdminDialog() },
                            onToggleSound = { viewModel.toggleSound() },
                            onToggleVibration = { viewModel.toggleVibration() },
                            onClaimEmergencyBonus = { viewModel.claimEmergencyBonus() }
                        )
                    }

                    CasinoScreen.SLOT_GAME -> {
                        SlotGameScreen(
                            state = uiState,
                            reelStates = viewModel.reelStates,
                            onBackToLobby = { viewModel.navigateToLobby() },
                            onSpinClick = { viewModel.spin() },
                            onIncreaseBet = { viewModel.increaseBet() },
                            onDecreaseBet = { viewModel.decreaseBet() },
                            onMaxBetClick = { viewModel.setMaxBet() },
                            onLinesChange = { lines -> viewModel.setActiveLines(lines) },
                            onToggleAutoSpin = { viewModel.toggleAutoSpin() },
                            onToggleSound = { viewModel.toggleSound() },
                            onOpenPaytable = { viewModel.openPaytable() },
                            onClaimEmergencyBonus = { viewModel.claimEmergencyBonus() },
                            onDismissCelebration = { viewModel.dismissCelebration() }
                        )
                    }

                    CasinoScreen.ROULETTE -> {
                        RouletteScreen(
                            balance = uiState.balance,
                            selectedBetType = uiState.rouletteBetType,
                            selectedNumber = uiState.rouletteSelectedNumber,
                            betAmount = uiState.rouletteBetAmount,
                            isSpinning = uiState.isRouletteSpinning,
                            targetDegrees = uiState.rouletteTargetDegrees,
                            lastWinningSector = uiState.lastWinningSector,
                            lastWinAmount = uiState.lastRouletteWin,
                            lastMessage = uiState.lastRouletteMessage,
                            history = uiState.rouletteHistory,
                            onBackToLobby = { viewModel.navigateToLobby() },
                            onSelectBetType = { type -> viewModel.setRouletteBetType(type) },
                            onSelectBetAmount = { amount -> viewModel.setRouletteBetAmount(amount) },
                            onSpin = { viewModel.spinRoulette() },
                            onClaimRefill = { viewModel.claimEmergencyBonus() }
                        )
                    }

                    CasinoScreen.GUESS_NUMBER -> {
                        GuessNumberScreen(
                            balance = uiState.balance,
                            selectedMode = uiState.guessMode,
                            exactNumber = uiState.guessExactNumber,
                            betAmount = uiState.guessBetAmount,
                            isRevealing = uiState.isGuessRevealing,
                            revealedNumber = uiState.revealedNumber,
                            lastWinAmount = uiState.lastGuessWin,
                            lastMessage = uiState.lastGuessMessage,
                            history = uiState.guessHistory,
                            onBackToLobby = { viewModel.navigateToLobby() },
                            onSelectMode = { mode -> viewModel.setGuessMode(mode) },
                            onSelectExactNumber = { num -> viewModel.setGuessExactNumber(num) },
                            onSelectBetAmount = { amount -> viewModel.setGuessBetAmount(amount) },
                            onPlayRound = { viewModel.playGuessNumber() },
                            onClaimRefill = { viewModel.claimEmergencyBonus() }
                        )
                    }

                    CasinoScreen.P2P_ROOM -> {
                        P2PRoomScreen(
                            p2pState = uiState.p2pState,
                            balance = uiState.balance,
                            onBackToLobby = { viewModel.navigateToLobby() },
                            onCreateRoom = { viewModel.createP2PRoom() },
                            onJoinRoom = { code -> viewModel.joinP2PRoom(code) },
                            onLeaveRoom = { viewModel.leaveP2PRoom() },
                            onSendMessage = { text -> viewModel.sendP2PMessage(text) },
                            onOpenNicknameDialog = { viewModel.openNicknameDialog() }
                        )
                    }
                }
            }

            // Dialogs
            if (uiState.showTasksDialog) {
                TasksDialog(
                    tasks = uiState.tasks,
                    onClaimReward = { taskId -> viewModel.claimTaskReward(taskId) },
                    onDismiss = { viewModel.closeTasksDialog() }
                )
            }

            if (uiState.showAdminDialog) {
                AdminAbuseDialog(
                    isAdminUnlocked = uiState.isAdminUnlocked,
                    isGodModeActive = uiState.isGodModeActive,
                    adminAudioState = uiState.adminAudioState,
                    onVerifyCode = { pin -> viewModel.verifyAdminCode(pin) },
                    onSelectCustomAudio = { uri -> viewModel.adminSelectCustomAudio(uri) },
                    onPlayAudio = { viewModel.adminPlayAudio() },
                    onPauseAudio = { viewModel.adminPauseAudio() },
                    onStopAudio = { viewModel.adminStopAudio() },
                    onPlayPresetSound = { preset -> viewModel.adminPlayPresetSound(preset) },
                    onToggleDiscoRave = { active -> viewModel.adminToggleDiscoRave(active) },
                    onBroadcastAudioToP2P = { trackTitle -> viewModel.adminBroadcastAudioToP2P(trackTitle) },
                    onForceJackpotNextSpin = { viewModel.adminForceJackpotNextSpin() },
                    onForceMegaMultiplier = { mult -> viewModel.adminForceMegaMultiplier(mult) },
                    onGiveFreeSpins = { count -> viewModel.adminGiveFreeSpins(count) },
                    onToggleGodMode = { enable -> viewModel.adminToggleGodMode(enable) },
                    onAddBalance = { amount -> viewModel.adminAddBalance(amount) },
                    onSetJackpot = { amount -> viewModel.adminSetJackpot(amount) },
                    onSendTrollAlert = { msg -> viewModel.adminSendTrollAlert(msg) },
                    onDismiss = { viewModel.closeAdminDialog() }
                )
            }

            if (uiState.showPaytable) {
                PaytableDialog(onDismiss = { viewModel.closePaytable() })
            }

            if (uiState.showBonusWheel) {
                DailyBonusDialog(
                    onClaimReward = { coins -> viewModel.claimDailyBonus(coins) },
                    onDismiss = { viewModel.closeBonusWheel() }
                )
            }

            if (uiState.showStats) {
                StatsDialog(
                    balance = uiState.balance,
                    totalSpins = uiState.totalSpins,
                    totalWon = uiState.totalWon,
                    biggestWin = uiState.biggestWin,
                    jackpotsHit = uiState.jackpotsHit,
                    onDismiss = { viewModel.closeStats() }
                )
            }

            if (uiState.showRefillDialog) {
                RefillDialog(
                    onClaimEmergencyBonus = { viewModel.claimEmergencyBonus() },
                    onOpenFortuneWheel = {
                        viewModel.closeRefillDialog()
                        viewModel.openBonusWheel()
                    },
                    onDismiss = { viewModel.closeRefillDialog() }
                )
            }

            if (uiState.showNicknameDialog) {
                NicknameDialog(
                    currentNickname = uiState.nickname,
                    onSaveNickname = { nick -> viewModel.setNickname(nick) },
                    onDismiss = { viewModel.closeNicknameDialog() }
                )
            }
        }
    }
}
