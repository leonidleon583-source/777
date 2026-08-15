package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.P2PRoomState
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
fun P2PRoomScreen(
    p2pState: P2PRoomState,
    balance: Long,
    onBackToLobby: () -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit,
    onLeaveRoom: () -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenNicknameDialog: () -> Unit
) {
    val context = LocalContext.current
    var joinCodeInput by remember { mutableStateOf("") }
    var chatInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    val discoBorderColor by animateColorAsState(
        targetValue = if (p2pState.isDiscoStrobeActive) AccentGold else ElegantDarkBorder,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "disco_border"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToLobby,
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
                    text = "P2P КОМНАТА",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = ElegantTextPrimary
                )
                Text(
                    text = if (p2pState.isConnected) "Комната #${p2pState.roomCode} • ${if (p2pState.isHost) "👑 Вы Хост (Админ)" else "👤 Вы Друг (Игрок)"}" else "Играйте и общайтесь с другом",
                    fontSize = 11.sp,
                    color = if (p2pState.isConnected) AccentEmerald else ElegantTextMuted
                )
            }

            // Nickname Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElegantDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder),
                modifier = Modifier.clickable { onOpenNicknameDialog() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Ник",
                        tint = AccentPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = p2pState.myNickname,
                        color = ElegantTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Active Track Banner (Admin Abuse Audio Broadcast)
        if (p2pState.activeTrackTitle != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2B1630),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🎵 Трансляция трека: ${p2pState.activeTrackTitle}",
                        color = AccentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!p2pState.isConnected) {
            // Not connected: Room Creation & Join UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "👥 Подключение к другу",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Создайте комнату как Хост (Админ) или подключитесь по 5-значному коду",
                    fontSize = 13.sp,
                    color = ElegantTextMuted,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Create Room Button
                Button(
                    onClick = onCreateRoom,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp)
                ) {
                    Text("СОЗДАТЬ КОМНАТУ (КАК ХОСТ 👑)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(ElegantDarkBorder))
                    Text(" ИЛИ ", color = ElegantTextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(ElegantDarkBorder))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Join Room Input & Button
                OutlinedTextField(
                    value = joinCodeInput,
                    onValueChange = { if (it.length <= 5 && it.all { ch -> ch.isDigit() }) joinCodeInput = it },
                    label = { Text("5-значный код комнаты друга") },
                    placeholder = { Text("например: 74921") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = ElegantDarkBorder,
                        focusedTextColor = ElegantTextPrimary,
                        unfocusedTextColor = ElegantTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onJoinRoom(joinCodeInput) },
                    enabled = joinCodeInput.length == 5,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentEmerald,
                        disabledContainerColor = Color(0xFF2E2E3A),
                        contentColor = Color.White,
                        disabledContentColor = ElegantTextMuted
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp)
                ) {
                    Text("ПОДКЛЮЧИТЬСЯ (КАК ДРУГ 👤)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        } else {
            // Connected State: Room Header & Tabs
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ElegantDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, discoBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Код: #${p2pState.roomCode}",
                                color = AccentGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Скопировать",
                                tint = ElegantTextMuted,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Room Code", p2pState.roomCode))
                                        Toast.makeText(context, "Код скопирован!", Toast.LENGTH_SHORT).show()
                                    }
                            )
                        }
                        Text(
                            text = "🟢 Игроков: ${p2pState.players.size} • Без авто-бустов",
                            color = AccentEmerald,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onLeaveRoom,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3F2025),
                            contentColor = Color(0xFFF87171)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Выйти", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ElegantDarkSurface,
                contentColor = AccentPurple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AccentPurple
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, ElegantDarkBorder, RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Чат комнаты", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Игроки & Роли", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Лидерборд", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    // Chat tab
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(ElegantDarkSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, ElegantDarkBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(p2pState.messages) { msg ->
                                if (msg.isSystem) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF222030), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = msg.text,
                                            color = AccentGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    val isMe = msg.senderName == p2pState.myNickname
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                    ) {
                                        Text(
                                            text = msg.senderName,
                                            color = if (isMe) AccentPurple else ElegantTextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isMe) AccentPurple else ElegantDarkSurfaceElevated,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = msg.text,
                                                color = if (isMe) Color.White else ElegantTextPrimary,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Reactions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("🔥 Джекпот!", "🎉 Красава!", "😎 Удваиваю", "💸 Слил 0").forEach { reaction ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(ElegantDarkSurfaceElevated, RoundedCornerShape(6.dp))
                                        .border(1.dp, ElegantDarkBorder, RoundedCornerShape(6.dp))
                                        .clickable { onSendMessage(reaction) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = reaction,
                                        color = ElegantTextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Message input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Сообщение...") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentPurple,
                                    unfocusedBorderColor = ElegantDarkBorder,
                                    focusedTextColor = ElegantTextPrimary,
                                    unfocusedTextColor = ElegantTextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        onSendMessage(chatInput)
                                        chatInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(AccentPurple, RoundedCornerShape(10.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Отправить",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Players & Roles Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ElegantDarkSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, ElegantDarkBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(p2pState.players) { player ->
                            val isMe = player.nickname == p2pState.myNickname

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = ElegantDarkSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (player.isHost) AccentGold else ElegantDarkBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    if (player.isHost) AccentGold.copy(alpha = 0.2f) else AccentPurple.copy(alpha = 0.2f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(if (player.isHost) "👑" else "👤", fontSize = 18.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = player.nickname + if (isMe) " (Вы)" else "",
                                                    color = ElegantTextPrimary,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = player.roleTitle,
                                                color = if (player.isHost) AccentGold else ElegantTextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${NumberFormat.getNumberInstance(Locale.GERMANY).format(if (isMe) balance else player.balance)} ₴",
                                            color = AccentEmerald,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Базовый режим",
                                            color = ElegantTextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Leaderboard Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ElegantDarkSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, ElegantDarkBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(p2pState.leaderboard.sortedByDescending { it.balance }) { player ->
                            val isMe = player.nickname == p2pState.myNickname
                            val formattedBal = NumberFormat.getNumberInstance(Locale.GERMANY).format(if (isMe) balance else player.balance)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isMe) Color(0xFF2C243B) else ElegantDarkSurfaceElevated,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isMe) AccentPurple else ElegantDarkBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isMe) "⭐" else "👤",
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = player.nickname + if (isMe) " (Вы)" else "",
                                            color = if (isMe) AccentPurple else ElegantTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Побед: ${player.winsCount} • Рекорд: ${player.biggestWin} ₴",
                                            color = ElegantTextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "$formattedBal ₴",
                                    color = AccentEmerald,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
