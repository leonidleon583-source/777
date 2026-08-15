package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.LineWin

@Composable
fun PaylineOverlay(
    activeWins: List<LineWin>,
    modifier: Modifier = Modifier
) {
    if (activeWins.isEmpty()) return

    val pulseAnim = remember { Animatable(0.5f) }

    LaunchedEffect(activeWins) {
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val colWidth = w / 3f
        val rowHeight = h / 3f

        activeWins.forEach { lineWin ->
            val payline = lineWin.payline
            val path = Path()

            payline.positions.forEachIndexed { index, pos ->
                val x = pos.col * colWidth + colWidth * 0.5f
                val y = pos.row * rowHeight + rowHeight * 0.5f

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            val glowColor = payline.lineColor.copy(alpha = 0.5f * pulseAnim.value)
            val solidColor = payline.lineColor

            // Outer Neon Glow
            drawPath(
                path = path,
                color = glowColor,
                style = Stroke(
                    width = 12.dp.toPx() * pulseAnim.value,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Inner Bright Core
            drawPath(
                path = path,
                color = solidColor,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Highlight nodes at symbol positions
            payline.positions.forEach { pos ->
                val cx = pos.col * colWidth + colWidth * 0.5f
                val cy = pos.row * rowHeight + rowHeight * 0.5f

                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = solidColor,
                    radius = 8.dp.toPx() * pulseAnim.value,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
