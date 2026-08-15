package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GridPos
import com.example.model.SlotGridMode
import com.example.model.SlotModifier
import com.example.model.SlotSymbol
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderLight
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantPurple
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import kotlin.math.floor
import kotlin.math.roundToInt

class ReelAnimState(
    val reelIndex: Int,
    val strip: List<SlotSymbol>
) {
    var isSpinning by mutableStateOf(false)
    var currentOffset by mutableFloatStateOf(0f)
    var targetSymbols by mutableStateOf(
        listOf(
            strip[0],
            strip[1],
            strip[2],
            strip[3 % strip.size],
            strip[4 % strip.size]
        )
    )
}

@Composable
fun SlotReelSet(
    reels: List<ReelAnimState>,
    isSpinningAll: Boolean,
    winningPositions: List<GridPos>,
    modifier: Modifier = Modifier,
    gridMode: SlotGridMode = SlotGridMode.GRID_3X3,
    activeModifier: SlotModifier = SlotModifier.NONE,
    frameColor: Color = ElegantDarkBorder,
    cabinetGradient: Brush
) {
    val visibleReelsCount = gridMode.cols.coerceIn(3, 9)
    val rowsCount = gridMode.rows.coerceIn(1, 5)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ElegantDarkSurface)
            .border(2.dp, frameColor, RoundedCornerShape(20.dp))
            .padding(6.dp)
    ) {
        val scrollState = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (gridMode == SlotGridMode.GRID_3X1) 120.dp else 230.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ElegantDarkBackground)
                .border(1.5.dp, ElegantDarkBorder, RoundedCornerShape(14.dp))
                .then(if (gridMode == SlotGridMode.GRID_9X5) Modifier.horizontalScroll(scrollState) else Modifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (colIndex in 0 until visibleReelsCount) {
                val reelState = reels.getOrElse(colIndex) { reels[0] }
                SingleReelDrum(
                    reelState = reelState,
                    isReelSpinning = isSpinningAll || reelState.isSpinning,
                    winningRowIndices = winningPositions.filter { it.col == colIndex }.map { it.row },
                    visibleRows = rowsCount,
                    modifier = if (gridMode == SlotGridMode.GRID_9X5) {
                        Modifier
                            .width(62.dp)
                            .fillMaxHeight()
                            .testTag("reel_$colIndex")
                    } else {
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("reel_$colIndex")
                    }
                )

                if (colIndex < visibleReelsCount - 1) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(ElegantDarkBorder)
                    )
                }
            }
        }

        // Active Buff / Debuff Overlay Badge
        if (activeModifier != SlotModifier.NONE) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(activeModifier.badgeColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${if (activeModifier.isDebuff) "⚠️" else "⚡"} ${activeModifier.label}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun SingleReelDrum(
    reelState: ReelAnimState,
    isReelSpinning: Boolean,
    winningRowIndices: List<Int>,
    visibleRows: Int,
    modifier: Modifier = Modifier
) {
    val animOffset = remember { Animatable(0f) }
    val strip = reelState.strip
    val stripSize = strip.size

    LaunchedEffect(isReelSpinning) {
        if (isReelSpinning) {
            while (isReelSpinning) {
                animOffset.animateTo(
                    targetValue = animOffset.value + stripSize * 3f,
                    animationSpec = tween(durationMillis = 800, easing = LinearEasing)
                )
            }
        } else {
            val currentPos = animOffset.value
            val targetBase = (floor(currentPos / stripSize) + 1) * stripSize
            val targetTopSymbol = reelState.targetSymbols.firstOrNull() ?: strip[0]
            val stripIndex = strip.indexOf(targetTopSymbol).coerceAtLeast(0)
            val finalTarget = targetBase + stripIndex

            animOffset.animateTo(
                targetValue = finalTarget + 0.15f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
            animOffset.animateTo(
                targetValue = finalTarget,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .background(ElegantDarkBackground)
            .clip(RoundedCornerShape(6.dp))
    ) {
        val totalHeight = maxHeight
        val itemHeight = totalHeight / visibleRows.toFloat()
        val itemHeightPx = (totalHeight / visibleRows.toFloat()).value

        val currentScroll = animOffset.value
        val fractionalShift = (currentScroll % 1f)
        val baseIndex = floor(currentScroll).toInt()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(0, (fractionalShift * itemHeightPx * 2f).roundToInt())
                }
        ) {
            for (rowOffset in -1..visibleRows) {
                val symbolIndex = Math.floorMod(baseIndex - rowOffset, stripSize)
                val symbol = if (!isReelSpinning && rowOffset in 0 until visibleRows) {
                    reelState.targetSymbols.getOrElse(rowOffset) { strip[symbolIndex] }
                } else {
                    strip[symbolIndex]
                }

                val isWinning = winningRowIndices.contains(rowOffset) && !isReelSpinning

                ReelSymbolItem(
                    symbol = symbol,
                    height = itemHeight,
                    isWinning = isWinning,
                    isCompact = visibleRows >= 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ReelSymbolItem(
    symbol: SlotSymbol,
    height: Dp,
    isWinning: Boolean,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(height)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .background(
                if (isWinning) AccentPurple.copy(alpha = 0.25f) else Color(0xFF191920),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isWinning) 2.dp else 1.dp,
                color = if (isWinning) AccentPurple else ElegantDarkBorder,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = symbol.iconEmoji,
                fontSize = if (isCompact) 18.sp else 28.sp
            )
            if (!isCompact) {
                Text(
                    text = symbol.displayName,
                    color = if (isWinning) AccentPurple else ElegantTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}
