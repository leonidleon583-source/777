package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderLight
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantPurple
import com.example.ui.theme.ElegantPurpleContainer
import com.example.ui.theme.ElegantPurpleDark
import com.example.ui.theme.ElegantPurpleLight
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.SpinButtonGradient
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CasinoControlPanel(
    betPerLine: Long,
    activeLinesCount: Int,
    isSpinning: Boolean,
    isAutoSpinning: Boolean,
    autoSpinsLeft: Int,
    freeSpinsLeft: Int,
    freeSpinsMultiplier: Int,
    onSpinClick: () -> Unit,
    onIncreaseBet: () -> Unit,
    onDecreaseBet: () -> Unit,
    onMaxBetClick: () -> Unit,
    onLinesChange: (Int) -> Unit,
    onToggleAutoSpin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalBet = betPerLine * activeLinesCount
    val spinPulse = remember { Animatable(1f) }

    LaunchedEffect(isSpinning) {
        if (!isSpinning) {
            spinPulse.animateTo(
                targetValue = 1.04f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            spinPulse.snapTo(1f)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        color = ElegantDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ElegantDarkBorder),
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Free spins banner if active
            if (freeSpinsLeft > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ElegantPurpleDark, ElegantPurple, ElegantPurpleDark)
                            )
                        )
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⭐ БЕСПЛАТНЫЕ ВРАЩЕНИЯ: $freeSpinsLeft (МНОЖИТЕЛЬ x$freeSpinsMultiplier) ⭐",
                        color = ElegantPurpleDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Top Row: Lines Selector & Quick Bet Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lines count selector (1, 3, 5)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ЛИНИИ",
                        color = ElegantPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    listOf(1, 3, 5).forEach { lines ->
                        val isSelected = activeLinesCount == lines
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) ElegantPurple else ElegantDarkBackground
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ElegantPurpleLight else ElegantDarkBorder,
                                    shape = CircleShape
                                )
                                .clickable(enabled = !isSpinning) {
                                    onLinesChange(lines)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$lines",
                                color = if (isSelected) ElegantPurpleDark else ElegantTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bet Control (+ / -)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElegantDarkBackground)
                        .border(1.dp, ElegantDarkBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecreaseBet,
                        enabled = !isSpinning,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Уменьшить ставку",
                            tint = if (!isSpinning) ElegantPurple else ElegantDarkBorder,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        Text(
                            text = "СТАВКА/ЛИНИЯ",
                            color = ElegantPurple,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$betPerLine ₴",
                            color = ElegantTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    IconButton(
                        onClick = onIncreaseBet,
                        enabled = !isSpinning,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Увеличить ставку",
                            tint = if (!isSpinning) ElegantPurple else ElegantDarkBorder,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Row: Total Bet Info, Max Bet, Auto Spin, Main Tactile Spin Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total Bet Display
                Column {
                    Text(
                        text = "ОБЩАЯ СТАВКА",
                        color = ElegantPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalBet)
                    Text(
                        text = "$formattedTotal ₴",
                        color = ElegantTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // MAX BET Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkBackground)
                        .border(1.dp, ElegantDarkBorder, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isSpinning) { onMaxBetClick() }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "МАКС.\nСТАВКА",
                        color = ElegantPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 11.sp
                    )
                }

                // AUTO SPIN Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAutoSpinning) ElegantPurpleContainer else ElegantDarkBackground)
                        .border(
                            1.dp,
                            if (isAutoSpinning) ElegantPurple else ElegantDarkBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = !isSpinning || isAutoSpinning) { onToggleAutoSpin() }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAutoSpinning) "АВТО ($autoSpinsLeft)\nСТОП" else "АВТО\nСПИН",
                        color = if (isAutoSpinning) ElegantPurpleLight else ElegantTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 11.sp
                    )
                }

                // Main Tactile SPIN Button
                Box(
                    modifier = Modifier
                        .scale(if (!isSpinning) spinPulse.value else 1f)
                        .size(width = 96.dp, height = 54.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            if (isSpinning) Brush.verticalGradient(listOf(Color(0xFF49454F), Color(0xFF333038)))
                            else SpinButtonGradient
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                if (isSpinning) listOf(Color(0xFF79747E), Color(0xFF49454F))
                                else listOf(Color.White, ElegantPurple, Color(0xFF9A82DB))
                            ),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .clickable(enabled = !isSpinning) { onSpinClick() }
                        .testTag("spin_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = if (isSpinning) Color(0xFFCAC4D0) else ElegantPurpleDark,
                            modifier = Modifier.size(16.dp).padding(end = 3.dp)
                        )
                        Text(
                            text = if (isSpinning) "..." else "КРУТИТЬ",
                            color = if (isSpinning) Color(0xFFCAC4D0) else ElegantPurpleDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
