package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CasinoTask
import com.example.model.SlotGridMode
import com.example.ui.components.JackpotMarquee
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
fun LobbyScreen(
    balance: Long,
    jackpot: Long,
    nickname: String,
    tasks: List<CasinoTask>,
    isSoundEnabled: Boolean,
    isVibrationEnabled: Boolean,
    onSelectGridMode: (SlotGridMode) -> Unit,
    onOpenRoulette: () -> Unit,
    onOpenGuessNumber: () -> Unit,
    onOpenP2P: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenBonusWheel: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenNicknameDialog: () -> Unit,
    onOpenAdminSecret: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onClaimEmergencyBonus: () -> Unit
) {
    val scrollState = rememberScrollState()
    var secretTapCount by remember { mutableIntStateOf(0) }
    var lastTapTimestamp by remember { mutableLongStateOf(0L) }

    val readyTasksCount = tasks.count { it.isCompleted && !it.isClaimed && it.isUnlocked }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header: Nickname, Balance, Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Nickname Chip
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ElegantDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder),
                modifier = Modifier.clickable { onOpenNicknameDialog() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(AccentPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Никнейм",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = nickname,
                        color = ElegantTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Изменить ник",
                        tint = ElegantTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Controls & Balance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sound Toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(36.dp)
                        .background(ElegantDarkSurface, CircleShape)
                        .border(1.dp, ElegantDarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Звук",
                        tint = if (isSoundEnabled) AccentPurple else ElegantTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Balance with Plus Refill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ElegantDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder),
                    modifier = Modifier.clickable { onClaimEmergencyBonus() }
                ) {
                    Row(
                        modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val formattedBalance = NumberFormat.getNumberInstance(Locale.GERMANY).format(balance)
                        Text(
                            text = "$formattedBalance ₴",
                            color = AccentEmerald,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(AccentEmerald, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Пополнить баланс",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progressive Jackpot Widget
        JackpotMarquee(
            jackpotAmount = jackpot,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // TASKS & QUESTS BANNER
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ElegantDarkSurface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (readyTasksCount > 0) AccentGold else ElegantDarkBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenTasks)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Brush.linearGradient(listOf(AccentGold, AccentPurple)),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎯", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ЗАДАНИЯ И КВЕСТЫ",
                                color = ElegantTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (readyTasksCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(AccentEmerald, CircleShape)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+$readyTasksCount ГОТОВО",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Цепочка квестов и ежедневные задания с наградами ₴",
                            color = ElegantTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(AccentGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "ОТКРЫТЬ",
                        color = AccentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION: СЛОТЫ (3x1, 3x3, 9x5 с бустерами)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎰 ИГРОВЫЕ СЛОТЫ",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = ElegantTextPrimary
            )
            Text(
                text = "Бустеры до x7 • Дебаффы x0.5",
                fontSize = 10.sp,
                color = AccentPurple,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Slot Variations Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SlotGridCard(
                mode = SlotGridMode.GRID_3X1,
                icon = "⚡",
                modifier = Modifier.weight(1f),
                onClick = { onSelectGridMode(SlotGridMode.GRID_3X1) }
            )
            SlotGridCard(
                mode = SlotGridMode.GRID_3X3,
                icon = "🎰",
                isPopular = true,
                modifier = Modifier.weight(1f),
                onClick = { onSelectGridMode(SlotGridMode.GRID_3X3) }
            )
            SlotGridCard(
                mode = SlotGridMode.GRID_9X5,
                icon = "💥",
                modifier = Modifier.weight(1f),
                onClick = { onSelectGridMode(SlotGridMode.GRID_9X5) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION: МИНИ-ИГРЫ И P2P
        Text(
            text = "🎲 АЗАРТНЫЕ МИНИ-ИГРЫ & P2P",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = ElegantTextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Roulette Card
        LobbyGameBanner(
            title = "Европейская Рулетка",
            subtitle = "Красное (2x) • Зелёное Zero 0 (14x) • Чёрное (2x)",
            badge = "РУЛЕТКА",
            badgeColor = AccentRed,
            accentEmoji = "🔴⚫",
            onClick = onOpenRoulette
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Guess Number Card
        LobbyGameBanner(
            title = "Угадай Число",
            subtitle = "Больше / Меньше / Диапазоны / Точное число до 50x",
            badge = "1..100",
            badgeColor = AccentEmerald,
            accentEmoji = "🎲",
            onClick = onOpenGuessNumber
        )

        Spacer(modifier = Modifier.height(10.dp))

        // P2P Room Card
        LobbyGameBanner(
            title = "P2P Комната с Другом",
            subtitle = "Подключение по 5-значному коду • Роли Хост / Друг • Чат",
            badge = "ОНЛАЙН P2P",
            badgeColor = AccentPurple,
            accentEmoji = "👥",
            onClick = onOpenP2P
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Bottom Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElegantDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenBonusWheel() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🎡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Колесо Фортуны",
                        color = ElegantTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElegantDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenStats() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Статистика",
                        tint = AccentPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Статистика",
                        color = ElegantTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stealthy Footer (Hidden Secret Admin Entry by triple tapping within 1.5s)
        Box(
            modifier = Modifier
                .clickable {
                    val now = System.currentTimeMillis()
                    if (now - lastTapTimestamp < 1500) {
                        secretTapCount++
                        if (secretTapCount >= 3) {
                            secretTapCount = 0
                            onOpenAdminSecret()
                        }
                    } else {
                        secretTapCount = 1
                    }
                    lastTapTimestamp = now
                }
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Casino 777 • Лицензия #UA-2026",
                color = ElegantTextMuted.copy(alpha = 0.35f),
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SlotGridCard(
    mode: SlotGridMode,
    icon: String,
    isPopular: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isPopular) Color(0xFF221C2B) else ElegantDarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isPopular) AccentPurple else ElegantDarkBorder
        ),
        modifier = modifier
            .height(115.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 18.sp)
                Box(
                    modifier = Modifier
                        .background(
                            if (isPopular) AccentPurple else ElegantDarkSurfaceElevated,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = mode.badge,
                        color = if (isPopular) Color.White else ElegantTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = mode.title,
                    color = ElegantTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${mode.cols}x${mode.rows} Сетка",
                    color = AccentGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LobbyGameBanner(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    accentEmoji: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = ElegantDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(ElegantDarkSurfaceElevated, RoundedCornerShape(10.dp))
                        .border(1.dp, ElegantDarkBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = accentEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = ElegantTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                color = badgeColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = ElegantTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
