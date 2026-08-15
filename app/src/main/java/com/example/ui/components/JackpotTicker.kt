package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderLight
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantPurple
import com.example.ui.theme.ElegantPurpleDark
import com.example.ui.theme.ElegantPurpleLight
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun JackpotMarquee(
    jackpotAmount: Long,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "jackpot_lights")
    val bulbPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bulb_phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ElegantDarkSurface)
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(ElegantDarkBorder, ElegantPurple, ElegantDarkBorder)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Chasing Lights Row Top
            ChasingBulbsRow(bulbPhase = bulbPhase.toInt(), count = 12)

            Spacer(modifier = Modifier.height(4.dp))

            // Main Jackpot Title Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantPurple.copy(alpha = 0.15f))
                    .border(1.dp, ElegantPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "🎰 СУПЕР ДЖЕКПОТ 🎰",
                    color = ElegantPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }

            val formattedJackpot = NumberFormat.getNumberInstance(Locale.US).format(jackpotAmount)
            Text(
                text = "$formattedJackpot ₴",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ElegantTextPrimary,
                style = TextStyle(
                    shadow = Shadow(
                        color = ElegantPurple.copy(alpha = 0.6f),
                        offset = Offset(0f, 0f),
                        blurRadius = 12f
                    )
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Chasing Lights Row Bottom
            ChasingBulbsRow(bulbPhase = (bulbPhase.toInt() + 3) % 6, count = 12)
        }
    }
}

@Composable
private fun ChasingBulbsRow(bulbPhase: Int, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until count) {
            val isOn = (i + bulbPhase) % 2 == 0
            val bulbColor = when ((i + bulbPhase) % 3) {
                0 -> ElegantPurple
                1 -> ElegantPurpleLight
                else -> Color(0xFFEFB8C8)
            }

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isOn) bulbColor else ElegantDarkBorder.copy(alpha = 0.4f))
                    .shadow(
                        elevation = if (isOn) 4.dp else 0.dp,
                        shape = CircleShape,
                        spotColor = bulbColor
                    )
            )
        }
    }
}
