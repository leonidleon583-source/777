package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SlotGridMode
import com.example.model.SlotModifier
import com.example.ui.components.CasinoControlPanel
import com.example.ui.components.PaylineOverlay
import com.example.ui.components.ReelAnimState
import com.example.ui.components.SlotReelSet
import com.example.ui.components.WinCelebrationOverlay
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantPurpleLight
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.viewmodel.CasinoUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SlotGameScreen(
    state: CasinoUiState,
    reelStates: List<ReelAnimState>,
    onBackToLobby: () -> Unit,
    onSpinClick: () -> Unit,
    onIncreaseBet: () -> Unit,
    onDecreaseBet: () -> Unit,
    onMaxBetClick: () -> Unit,
    onLinesChange: (Int) -> Unit,
    onToggleAutoSpin: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenPaytable: () -> Unit,
    onClaimEmergencyBonus: () -> Unit,
    onDismissCelebration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridMode = state.selectedGridMode

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Navigation & Actions Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBackToLobby,
                    enabled = !state.isSpinning,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkSurface)
                        .border(1.dp, ElegantDarkBorder, RoundedCornerShape(12.dp))
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "В лобби",
                        tint = ElegantTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Machine Grid Mode Title Pill
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = gridMode.title.uppercase(),
                        color = ElegantTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${gridMode.cols} БАРАБАНОВ • ${gridMode.rows} РЯДОВ",
                        color = AccentPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sound & Paytable Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenPaytable,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantDarkSurface)
                            .border(1.dp, ElegantDarkBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Правила и выплаты",
                            tint = AccentPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantDarkSurface)
                            .border(1.dp, ElegantDarkBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = if (state.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Звук",
                            tint = if (state.isSoundEnabled) AccentPurple else ElegantTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Player Balance & Mini Jackpot Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Balance Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElegantDarkSurface)
                        .border(1.dp, ElegantDarkBorder, RoundedCornerShape(14.dp))
                        .padding(start = 10.dp, end = 4.dp, top = 3.dp, bottom = 3.dp)
                ) {
                    val formattedBalance = NumberFormat.getNumberInstance(Locale.US).format(state.balance)
                    Text(
                        text = "$formattedBalance ₴",
                        color = AccentEmerald,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(
                        onClick = onClaimEmergencyBonus,
                        enabled = !state.isSpinning,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AccentEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Пополнить",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Jackpot Display
                val formattedJackpot = NumberFormat.getNumberInstance(Locale.US).format(state.progressiveJackpot)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElegantDarkSurface)
                        .border(1.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "🎰 ДЖЕКПОТ: ",
                        color = AccentGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "$formattedJackpot ₴",
                        color = ElegantTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Slot Reels Cabinet Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (gridMode == SlotGridMode.GRID_3X1) 140.dp else 240.dp),
                contentAlignment = Alignment.Center
            ) {
                val winningPositions = state.activeLineWins.flatMap { it.winningPositions }
                SlotReelSet(
                    reels = reelStates,
                    isSpinningAll = state.isSpinning,
                    winningPositions = winningPositions,
                    gridMode = gridMode,
                    activeModifier = state.activeModifier,
                    frameColor = ElegantDarkBorder,
                    cabinetGradient = Brush.verticalGradient(
                        listOf(Color(0xFF1E1E26), Color(0xFF141419))
                    ),
                    modifier = Modifier.fillMaxSize()
                )

                if (gridMode == SlotGridMode.GRID_3X3) {
                    PaylineOverlay(
                        activeWins = state.activeLineWins,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Win / Status Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantDarkSurface)
                    .border(
                        1.dp,
                        if (state.lastWinAmount > 0) AccentEmerald else ElegantDarkBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.lastWinAmount > 0) {
                    val formattedWin = NumberFormat.getNumberInstance(Locale.US).format(state.lastWinAmount)
                    val modText = if (state.activeModifier != SlotModifier.NONE) " (${state.activeModifier.label})" else ""
                    Text(
                        text = "🎉 ВЫИГРЫШ: +$formattedWin ₴$modText",
                        color = AccentEmerald,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                } else if (state.isSpinning) {
                    Text(
                        text = "УДАЧИ! БАРАБАНЫ ВРАЩАЮТСЯ... 🎰",
                        color = AccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "ДЕЛАЙТЕ СТАВКУ И КРУТИТЕ БАРАБАНЫ",
                        color = ElegantTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Casino Control Panel (Bets, Lines, Auto Spin, Spin Button)
            CasinoControlPanel(
                betPerLine = state.betPerLine,
                activeLinesCount = state.activeLinesCount,
                isSpinning = state.isSpinning,
                isAutoSpinning = state.isAutoSpinning,
                autoSpinsLeft = state.autoSpinsLeft,
                freeSpinsLeft = state.freeSpinsLeft,
                freeSpinsMultiplier = state.freeSpinMultiplier,
                onSpinClick = onSpinClick,
                onIncreaseBet = onIncreaseBet,
                onDecreaseBet = onDecreaseBet,
                onMaxBetClick = onMaxBetClick,
                onLinesChange = onLinesChange,
                onToggleAutoSpin = onToggleAutoSpin,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Win Celebration Overlay for Big Win / Mega Win / 777 Jackpot
        WinCelebrationOverlay(
            winTier = state.celebrationTier,
            totalWin = state.celebrationCoins,
            onDismiss = onDismissCelebration
        )
    }
}
