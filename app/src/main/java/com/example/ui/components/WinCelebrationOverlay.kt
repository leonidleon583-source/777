package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WinTier
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.LuckyRed
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random

data class CoinParticle(
    var x: Float,
    var y: Float,
    var speedY: Float,
    var speedX: Float,
    var size: Float,
    var rotation: Float,
    var color: Color
)

@Composable
fun WinCelebrationOverlay(
    winTier: WinTier,
    totalWin: Long,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (winTier == WinTier.NONE || totalWin <= 0) return

    val scaleAnim = remember { Animatable(0.2f) }
    var displayedWin by remember { mutableLongStateOf(0L) }

    // Count-up animation
    LaunchedEffect(totalWin) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        // Count up the win numbers
        val steps = 25
        val stepValue = totalWin / steps
        for (i in 1..steps) {
            displayedWin = (stepValue * i).coerceAtMost(totalWin)
            delay(20)
        }
        displayedWin = totalWin
    }

    // Floating particles
    val particles = remember {
        List(40) {
            CoinParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                speedY = 0.005f + Random.nextFloat() * 0.015f,
                speedX = (Random.nextFloat() - 0.5f) * 0.004f,
                size = 12f + Random.nextFloat() * 16f,
                rotation = Random.nextFloat() * 360f,
                color = if (Random.nextBoolean()) GoldBase else Color(0xFFFF9E00)
            )
        }
    }

    val particleAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        particleAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xBB000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Falling Coin Fountain Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            particles.forEach { p ->
                p.y += p.speedY
                p.x += p.speedX
                if (p.y > 1.1f) {
                    p.y = -0.1f
                    p.x = Random.nextFloat()
                }
                drawCircle(
                    color = p.color,
                    radius = p.size,
                    center = Offset(p.x * w, p.y * h)
                )
                // Inner rim for coin look
                drawCircle(
                    color = GoldLight,
                    radius = p.size * 0.6f,
                    center = Offset(p.x * w, p.y * h)
                )
            }
        }

        // Win Dialog Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .scale(scaleAnim.value),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E0A28)
            ),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF420D3B),
                                Color(0xFF230626),
                                Color(0xFF120215)
                            )
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (winTier == WinTier.JACKPOT_777) "💥 777 💥" else "🎉 ✨ 🎉",
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = winTier.titleRu,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = if (winTier == WinTier.JACKPOT_777) LuckyRed else GoldLight,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = GoldBase,
                            offset = Offset(0f, 2f),
                            blurRadius = 14f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val formattedWin = NumberFormat.getNumberInstance(Locale.US).format(displayedWin)
                Text(
                    text = "+$formattedWin ₴",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldBase,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFFF8C00),
                            offset = Offset(0f, 3f),
                            blurRadius = 18f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldBase,
                        contentColor = Color(0xFF281100)
                    ),
                    modifier = Modifier.fillMaxWidth(0.75f)
                ) {
                    Text(
                        text = "ЗАБРАТЬ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
