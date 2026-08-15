package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantPurple
import com.example.ui.theme.ElegantPurpleDark
import com.example.ui.theme.ElegantPurpleLight
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DailyBonusDialog(
    onClaimReward: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val prizes = listOf(500L, 1000L, 2500L, 5000L, 10000L, 1500L, 3000L, 20000L)
    val sliceAngle = 360f / prizes.size

    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonPrize by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ElegantDarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "КОЛЕСО УДАЧИ 🎡",
                        color = ElegantTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (!isSpinning) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Закрыть",
                                tint = ElegantTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wheel Container with Top Pointer
                Box(
                    modifier = Modifier.size(230.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation.value)
                    ) {
                        val radius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        prizes.forEachIndexed { i, prize ->
                            val startAngle = i * sliceAngle
                            val color = when (i % 4) {
                                0 -> Color(0xFFD0BCFF)
                                1 -> Color(0xFF4F378B)
                                2 -> Color(0xFF80D8FF)
                                else -> Color(0xFFEFB8C8)
                            }

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sliceAngle,
                                useCenter = true,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2)
                            )
                        }

                        // Outer rim
                        drawCircle(
                            color = ElegantDarkBorder,
                            radius = radius,
                            center = center,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }

                    // Center Hub
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(ElegantDarkBackground)
                            .border(2.dp, ElegantPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "₴", fontSize = 24.sp, color = ElegantPurple, fontWeight = FontWeight.Black)
                    }

                    // Top Pointer indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ElegantPurple)
                            .border(1.5.dp, ElegantPurpleLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "▼", color = ElegantPurpleDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (wonPrize != null) {
                    Text(
                        text = "ВЫ ВЫИГРАЛИ: +${wonPrize} ₴! 🎉",
                        color = ElegantPurple,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            wonPrize?.let { onClaimReward(it) }
                            onDismiss()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantPurple,
                            contentColor = ElegantPurpleDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "ЗАБРАТЬ ПРИЗ 🎁", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                val winningIndex = Random.nextInt(prizes.size)
                                val prize = prizes[winningIndex]
                                val targetDegrees = 360f * 5 + (360f - (winningIndex * sliceAngle + sliceAngle / 2f))

                                scope.launch {
                                    rotation.animateTo(
                                        targetValue = targetDegrees,
                                        animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing)
                                    )
                                    isSpinning = false
                                    wonPrize = prize
                                }
                            }
                        },
                        enabled = !isSpinning,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantPurple,
                            contentColor = ElegantPurpleDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSpinning) "КОЛЕСО КРУТИТСЯ..." else "КРУТИТЬ БЕСПЛАТНО 🎡",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
