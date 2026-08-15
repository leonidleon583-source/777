package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GuessMode
import com.example.model.GuessRoundResult
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceElevated
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GuessNumberScreen(
    balance: Long,
    selectedMode: GuessMode,
    exactNumber: Int,
    betAmount: Long,
    isRevealing: Boolean,
    revealedNumber: Int?,
    lastWinAmount: Long,
    lastMessage: String,
    history: List<GuessRoundResult>,
    onBackToLobby: () -> Unit,
    onSelectMode: (GuessMode) -> Unit,
    onSelectExactNumber: (Int) -> Unit,
    onSelectBetAmount: (Long) -> Unit,
    onPlayRound: () -> Unit,
    onClaimRefill: () -> Unit
) {
    val scrollState = rememberScrollState()
    var sliderVal by remember { mutableStateOf(exactNumber.toFloat()) }

    // Card 3D Flip animation
    val flipRotation by animateFloatAsState(
        targetValue = if (revealedNumber != null) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "card_flip"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToLobby,
                enabled = !isRevealing,
                modifier = Modifier
                    .size(42.dp)
                    .background(ElegantDarkSurface, CircleShape)
                    .border(1.dp, ElegantDarkBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = ElegantTextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "УГАДАЙ ЧИСЛО",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = ElegantTextPrimary
                )
                Text(
                    text = "Числа 1..100 • Множители до 50x",
                    fontSize = 11.sp,
                    color = ElegantTextMuted
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElegantDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder),
                modifier = Modifier.clickable(enabled = !isRevealing) { onClaimRefill() }
            ) {
                val formattedBalance = NumberFormat.getNumberInstance(Locale.US).format(balance)
                Text(
                    text = "$formattedBalance ₴",
                    color = AccentEmerald,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3D Flip Mystery Card
        Box(
            modifier = Modifier
                .size(width = 160.dp, height = 180.dp)
                .graphicsLayer {
                    rotationY = flipRotation
                    cameraDistance = 12f * density
                }
                .clip(RoundedCornerShape(20.dp))
                .background(if (revealedNumber != null && lastWinAmount > 0) Color(0xFF064E3B) else ElegantDarkSurfaceElevated)
                .border(
                    width = 2.dp,
                    color = if (revealedNumber != null && lastWinAmount > 0) AccentEmerald else AccentPurple,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (flipRotation <= 90f) {
                // Front: Hidden question / mystery
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isRevealing) "🎲" else "❓",
                        fontSize = 42.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isRevealing) "Открываем..." else "ТАЙНОЕ ЧИСЛО",
                        color = ElegantTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Back: Revealed target number (counter-flipped for readability)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${revealedNumber ?: 0}",
                            color = if (lastWinAmount > 0) AccentEmerald else AccentGold,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (lastWinAmount > 0) "ПОБЕДА!" else "НЕ СОВПАЛО",
                            color = if (lastWinAmount > 0) AccentEmerald else AccentRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Message
        if (lastMessage.isNotEmpty()) {
            Text(
                text = lastMessage,
                color = if (lastWinAmount > 0) AccentEmerald else ElegantTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Modes Selection Grid
        Text(
            text = "ВЫБЕРИТЕ УСЛОВИЕ (МНОЖИТЕЛЬ)",
            color = ElegantTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GuessModeButton(
                title = "< 50",
                subtitle = "Меньше (2.0x)",
                isSelected = selectedMode == GuessMode.LESS_THAN_50,
                enabled = !isRevealing,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(GuessMode.LESS_THAN_50) }
            )
            GuessModeButton(
                title = "> 50",
                subtitle = "Больше (2.0x)",
                isSelected = selectedMode == GuessMode.MORE_THAN_50,
                enabled = !isRevealing,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(GuessMode.MORE_THAN_50) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GuessModeButton(
                title = "ЧЁТНОЕ",
                subtitle = "2, 4, 6... (2.0x)",
                isSelected = selectedMode == GuessMode.EVEN,
                enabled = !isRevealing,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(GuessMode.EVEN) }
            )
            GuessModeButton(
                title = "НЕЧЁТНОЕ",
                subtitle = "1, 3, 5... (2.0x)",
                isSelected = selectedMode == GuessMode.ODD,
                enabled = !isRevealing,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(GuessMode.ODD) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Range Modes 1-33, 34-66, 67-100 (3x)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            GuessModeButton(
                title = "1 — 33",
                subtitle = "3.0x",
                isSelected = selectedMode == GuessMode.TIER_1,
                enabled = !isRevealing,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(GuessMode.TIER_1) }
            )
            GuessModeButton(
                title = "34 — 66",
                subtitle = "3.0x",
                isSelected = selectedMode == GuessMode.TIER_2,
                enabled = !isRevealing,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(GuessMode.TIER_2) }
            )
            GuessModeButton(
                title = "67 — 100",
                subtitle = "3.0x",
                isSelected = selectedMode == GuessMode.TIER_3,
                enabled = !isRevealing,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(GuessMode.TIER_3) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Exact Mode Selector
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedMode == GuessMode.EXACT) Color(0xFF2B2240) else ElegantDarkSurface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (selectedMode == GuessMode.EXACT) AccentPurple else ElegantDarkBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isRevealing) { onSelectMode(GuessMode.EXACT) }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ТОЧНОЕ ЧИСЛО (50.0x)",
                        color = if (selectedMode == GuessMode.EXACT) AccentPurple else ElegantTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Выбрано: ${sliderVal.toInt()}",
                        color = AccentGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = sliderVal,
                    onValueChange = {
                        sliderVal = it
                        onSelectExactNumber(it.toInt())
                    },
                    valueRange = 1f..100f,
                    steps = 98,
                    enabled = !isRevealing,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentPurple,
                        activeTrackColor = AccentPurple,
                        inactiveTrackColor = ElegantDarkBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bet Selector
        Text(
            text = "СУММА СТАВКИ (₴)",
            color = ElegantTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(25L, 50L, 100L, 250L, 500L, 1000L).forEach { amount ->
                val isSelected = betAmount == amount
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AccentPurple else ElegantDarkSurfaceElevated)
                        .border(1.dp, if (isSelected) AccentPurple else ElegantDarkBorder, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isRevealing) { onSelectBetAmount(amount) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$amount",
                        color = if (isSelected) Color.White else ElegantTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Play Button
        Button(
            onClick = onPlayRound,
            enabled = !isRevealing && balance >= betAmount,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPurple,
                disabledContainerColor = Color(0xFF2E2E3A),
                contentColor = Color.White,
                disabledContentColor = ElegantTextMuted
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (isRevealing) "РАСКРЫВАЕМ..." else "ОТКРЫТЬ КАРТУ ($betAmount ₴)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // History list
        if (history.isNotEmpty()) {
            Text(
                text = "ПОСЛЕДНИЕ РАУНДЫ",
                color = ElegantTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            history.take(4).forEach { res ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .background(ElegantDarkSurface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Число ${res.targetNumber} • ${res.chosenMode.title}",
                        color = ElegantTextPrimary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (res.isWin) "+${res.winAmount} ₴" else "-${res.betAmount} ₴",
                        color = if (res.isWin) AccentEmerald else AccentRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GuessModeButton(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) AccentPurple else ElegantDarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) AccentPurple else ElegantDarkBorder
        ),
        modifier = modifier
            .height(50.dp)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = if (isSelected) Color.White else ElegantTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                color = if (isSelected) Color(0xFFE9D5FF) else ElegantTextMuted,
                fontSize = 10.sp
            )
        }
    }
}
