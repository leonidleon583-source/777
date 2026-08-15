package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RouletteBetType
import com.example.model.RouletteColor
import com.example.model.RouletteConfig
import com.example.model.RouletteSector
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
fun RouletteScreen(
    balance: Long,
    selectedBetType: RouletteBetType,
    selectedNumber: Int?,
    betAmount: Long,
    isSpinning: Boolean,
    targetDegrees: Float,
    lastWinningSector: RouletteSector?,
    lastWinAmount: Long,
    lastMessage: String,
    history: List<RouletteSector>,
    onBackToLobby: () -> Unit,
    onSelectBetType: (RouletteBetType) -> Unit,
    onSelectBetAmount: (Long) -> Unit,
    onSpin: () -> Unit,
    onClaimRefill: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Smooth physics wheel deceleration
    val animatedRotation by animateFloatAsState(
        targetValue = targetDegrees,
        animationSpec = tween(
            durationMillis = if (isSpinning) 4200 else 0,
            easing = FastOutSlowInEasing
        ),
        label = "roulette_rotation"
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
                enabled = !isSpinning,
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
                    text = "РУЛЕТКА",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = ElegantTextPrimary
                )
                Text(
                    text = "Красное • Зелёное 0 • Чёрное",
                    fontSize = 11.sp,
                    color = ElegantTextMuted
                )
            }

            // Balance Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElegantDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder),
                modifier = Modifier.clickable(enabled = !isSpinning) { onClaimRefill() }
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

        Spacer(modifier = Modifier.height(16.dp))

        // History bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ИСТОРИЯ:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ElegantTextMuted
            )
            history.forEach { sector ->
                val bgCol = when (sector.color) {
                    RouletteColor.GREEN -> AccentEmerald
                    RouletteColor.RED -> AccentRed
                    RouletteColor.BLACK -> Color(0xFF27272A)
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(bgCol, CircleShape)
                        .border(1.dp, Color(0x33FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${sector.number}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Minimalist Roulette Wheel Canvas
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(Color(0xFF141419))
                .border(4.dp, ElegantDarkBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Wheel
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedRotation)
            ) {
                val sectors = RouletteConfig.WHEEL_ORDER
                val sweep = 360f / sectors.size
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2

                for (i in sectors.indices) {
                    val sec = sectors[i]
                    val startAngle = i * sweep
                    val color = when (sec.color) {
                        RouletteColor.GREEN -> AccentEmerald
                        RouletteColor.RED -> AccentRed
                        RouletteColor.BLACK -> Color(0xFF1F2026)
                    }

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height),
                        style = Fill
                    )

                    // Sector divider line
                    drawArc(
                        color = Color(0x33000000),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true,
                        style = Stroke(width = 1.5f)
                    )
                }

                // Inner Hub
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF2E2E3A), Color(0xFF141419))
                    ),
                    radius = radius * 0.38f,
                    center = center
                )
                drawCircle(
                    color = AccentGold,
                    radius = radius * 0.08f,
                    center = center
                )
            }

            // Top Pointer Triangle
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .size(width = 14.dp, height = 18.dp)
                    .background(AccentGold, RoundedCornerShape(bottomStart = 7.dp, bottomEnd = 7.dp))
            )

            // Winning Center Display
            if (lastWinningSector != null && !isSpinning) {
                Surface(
                    shape = CircleShape,
                    color = when (lastWinningSector.color) {
                        RouletteColor.GREEN -> AccentEmerald
                        RouletteColor.RED -> AccentRed
                        RouletteColor.BLACK -> Color(0xFF1F2026)
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .border(2.dp, AccentGold, CircleShape),
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${lastWinningSector.number}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Result Message
        if (lastMessage.isNotEmpty()) {
            Text(
                text = lastMessage,
                color = if (lastWinAmount > 0) AccentEmerald else ElegantTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Betting Buttons (Red / Green 0 / Black)
        Text(
            text = "ВЫБЕРИТЕ СТАВКУ",
            color = ElegantTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // RED BUTTON
            RouletteBetButton(
                title = "КРАСНОЕ (2x)",
                color = AccentRed,
                isSelected = selectedBetType == RouletteBetType.RED,
                enabled = !isSpinning,
                modifier = Modifier.weight(1f),
                onClick = { onSelectBetType(RouletteBetType.RED) }
            )

            // ZERO BUTTON
            RouletteBetButton(
                title = "ЗЕРО 0 (14x)",
                color = AccentEmerald,
                isSelected = selectedBetType == RouletteBetType.GREEN_ZERO,
                enabled = !isSpinning,
                modifier = Modifier.weight(1f),
                onClick = { onSelectBetType(RouletteBetType.GREEN_ZERO) }
            )

            // BLACK BUTTON
            RouletteBetButton(
                title = "ЧЁРНОЕ (2x)",
                color = Color(0xFF27272A),
                isSelected = selectedBetType == RouletteBetType.BLACK,
                enabled = !isSpinning,
                modifier = Modifier.weight(1f),
                onClick = { onSelectBetType(RouletteBetType.BLACK) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Secondary outside bets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RouletteSecondaryBetChip(
                label = "ЧЁТНОЕ (2x)",
                isSelected = selectedBetType == RouletteBetType.EVEN,
                enabled = !isSpinning,
                modifier = Modifier.weight(1f),
                onClick = { onSelectBetType(RouletteBetType.EVEN) }
            )
            RouletteSecondaryBetChip(
                label = "НЕЧЁТНОЕ (2x)",
                isSelected = selectedBetType == RouletteBetType.ODD,
                enabled = !isSpinning,
                modifier = Modifier.weight(1f),
                onClick = { onSelectBetType(RouletteBetType.ODD) }
            )
            RouletteSecondaryBetChip(
                label = "1—18",
                isSelected = selectedBetType == RouletteBetType.LOW_RANGE,
                enabled = !isSpinning,
                modifier = Modifier.weight(1f),
                onClick = { onSelectBetType(RouletteBetType.LOW_RANGE) }
            )
            RouletteSecondaryBetChip(
                label = "19—36",
                isSelected = selectedBetType == RouletteBetType.HIGH_RANGE,
                enabled = !isSpinning,
                modifier = Modifier.weight(1f),
                onClick = { onSelectBetType(RouletteBetType.HIGH_RANGE) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Bet Amount Selector
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
                        .clickable(enabled = !isSpinning) { onSelectBetAmount(amount) },
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

        Spacer(modifier = Modifier.height(20.dp))

        // Spin Action Button
        Button(
            onClick = onSpin,
            enabled = !isSpinning && balance >= betAmount,
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
                text = if (isSpinning) "ВРАЩЕНИЕ..." else "КРУТИТЬ РУЛЕТКУ ($betAmount ₴)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun RouletteBetButton(
    title: String,
    color: Color,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color.White else Color(0x33FFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun RouletteSecondaryBetChip(
    label: String,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AccentPurple else ElegantDarkSurface)
            .border(
                width = 1.dp,
                color = if (isSelected) AccentPurple else ElegantDarkBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else ElegantTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
