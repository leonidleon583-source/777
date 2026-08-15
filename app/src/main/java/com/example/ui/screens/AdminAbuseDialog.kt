package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AdminAudioState
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminAbuseDialog(
    isAdminUnlocked: Boolean,
    isGodModeActive: Boolean,
    adminAudioState: AdminAudioState,
    onVerifyCode: (String) -> Boolean,
    onSelectCustomAudio: (Uri) -> Unit,
    onPlayAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onPlayPresetSound: (String) -> Unit,
    onToggleDiscoRave: (Boolean) -> Unit,
    onBroadcastAudioToP2P: (String) -> Unit,
    onForceJackpotNextSpin: () -> Unit,
    onForceMegaMultiplier: (Int) -> Unit,
    onGiveFreeSpins: (Int) -> Unit,
    onToggleGodMode: (Boolean) -> Unit,
    onAddBalance: (Long) -> Unit,
    onSetJackpot: (Long) -> Unit,
    onSendTrollAlert: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onSelectCustomAudio(uri)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(if (isAdminUnlocked) 0.88f else 0.45f)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isAdminUnlocked) AccentRed else ElegantDarkBorder)
        ) {
            if (!isAdminUnlocked) {
                // Secret Master Code Entry screen (looks stealthy/confidential)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF2D183A), Color(0xFF160A1F))),
                                CircleShape
                            )
                            .border(1.dp, AccentPurple.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Служебный доступ",
                            tint = AccentPurple,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Служебный доступ",
                        color = ElegantTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Введите 6-значный код хоста",
                        color = ElegantTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 6) {
                                pinInput = it
                                pinError = false
                                if (it.length == 6) {
                                    val success = onVerifyCode(it)
                                    if (!success) pinError = true
                                }
                            }
                        },
                        placeholder = { Text("••••••", color = ElegantTextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            val success = onVerifyCode(pinInput)
                            if (!success) pinError = true
                        }),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = if (pinError) AccentRed else AccentPurple,
                            unfocusedBorderColor = if (pinError) AccentRed.copy(alpha = 0.5f) else ElegantDarkBorder,
                            focusedContainerColor = ElegantDarkBackground,
                            unfocusedContainerColor = ElegantDarkBackground
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    if (pinError) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Неверный код доступа",
                            color = AccentRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = ElegantDarkBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Отмена", color = ElegantTextSecondary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val success = onVerifyCode(pinInput)
                                if (!success) pinError = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Вход", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // FULL UNLOCKED ADMIN ABUSE PANEL
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        Brush.linearGradient(listOf(AccentRed, Color(0xFFFF5722))),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👑", fontSize = 17.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "АДМИН-АБЬЮЗ ПАНЕЛЬ",
                                    color = Color(0xFFFF5252),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Режим Создателя • Полный доступ",
                                    color = ElegantTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Закрыть",
                                tint = ElegantTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Selector
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = ElegantDarkBackground,
                        contentColor = Color(0xFFFF5252),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color(0xFFFF5252),
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("🎵 Музыка", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("🎰 Читы слотов", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("💰 Баланс", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("🎭 Троллинг", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        when (selectedTab) {
                            0 -> {
                                // MUSIC & SOUNDS
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = ElegantDarkBackground),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text(
                                                text = "СВОЯ МУЗЫКА ИЗ ФАЙЛОВ ТЕЛЕФОНА",
                                                color = ElegantTextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Выберите любой MP3 / WAV / FLAC трек и включите на всю комнату!",
                                                color = ElegantTextSecondary,
                                                fontSize = 10.sp
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Button(
                                                onClick = { filePickerLauncher.launch("audio/*") },
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("📁 Выбрать трек из памяти устройства", fontSize = 12.sp, color = Color.White)
                                            }

                                            if (adminAudioState.currentTrackTitle.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(ElegantDarkSurfaceElevated, RoundedCornerShape(10.dp))
                                                        .padding(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        modifier = Modifier.weight(1f),
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
                                                            text = adminAudioState.currentTrackTitle,
                                                            color = ElegantTextPrimary,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1
                                                        )
                                                    }

                                                    Row {
                                                        if (adminAudioState.isPlaying) {
                                                            IconButton(onClick = onPauseAudio) {
                                                                Icon(Icons.Default.Pause, contentDescription = "Пауза", tint = AccentGold)
                                                            }
                                                        } else {
                                                            IconButton(onClick = onPlayAudio) {
                                                                Icon(Icons.Default.PlayArrow, contentDescription = "Играть", tint = AccentEmerald)
                                                            }
                                                        }
                                                        IconButton(onClick = onStopAudio) {
                                                            Icon(Icons.Default.Stop, contentDescription = "Стоп", tint = AccentRed)
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    onClick = { onBroadcastAudioToP2P(adminAudioState.currentTrackTitle) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text("📢 Транслировать песню в P2P чат и комнату", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Text(
                                        text = "ЗВУКОВЫЕ ЭФФЕКТЫ И СПЕЦЭФФЕКТЫ",
                                        color = ElegantTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                item {
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        SoundPresetButton("🚨 Сирена тревоги", AccentRed) { onPlayPresetSound("SIREN") }
                                        SoundPresetButton("👑 Фанфары победы", AccentGold) { onPlayPresetSound("FANFARE") }
                                        SoundPresetButton("🔥 Бассбуст дроп", AccentPurple) { onPlayPresetSound("BASSBOOST") }
                                        SoundPresetButton("😈 Злодейский смех", Color(0xFFE91E63)) { onPlayPresetSound("LAUGH") }
                                    }
                                }

                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = ElegantDarkBackground),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "⚡ Рейв / Диско-стробоскоп",
                                                    color = ElegantTextPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Неоновое мерцание экрана и вибрация",
                                                    color = ElegantTextSecondary,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Switch(
                                                checked = adminAudioState.isDiscoStrobeActive,
                                                onCheckedChange = { onToggleDiscoRave(it) },
                                                colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentPurple)
                                            )
                                        }
                                    }
                                }
                            }

                            1 -> {
                                // SLOTS CHEATS & GOD MODE
                                item {
                                    AdminActionTile(
                                        title = "🔥 100% ГАРАНТИЯ ДЖЕКПОТА 777",
                                        subtitle = "Следующий спин слотов выпадет комбинацией 7-7-7 и сорвет банк!",
                                        buttonText = "Зарядить Джекпот",
                                        buttonColor = AccentRed,
                                        onClick = onForceJackpotNextSpin
                                    )
                                }

                                item {
                                    AdminActionTile(
                                        title = "⚡ МЕГА-МНОЖИТЕЛЬ x100",
                                        subtitle = "Следующий выигрыш будет умножен в 100 раз",
                                        buttonText = "Активировать x100",
                                        buttonColor = AccentGold,
                                        onClick = { onForceMegaMultiplier(100) }
                                    )
                                }

                                item {
                                    AdminActionTile(
                                        title = "🎁 +50 ФРИСПИНОВ С МНОЖИТЕЛЕМ x5",
                                        subtitle = "Начисляет 50 бесплатных вращений с 5-кратным множителем",
                                        buttonText = "Выдать фриспины",
                                        buttonColor = AccentPurple,
                                        onClick = { onGiveFreeSpins(50) }
                                    )
                                }

                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = ElegantDarkBackground),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isGodModeActive) AccentGold else ElegantDarkBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "🛡️ Режим Бога (God Mode)",
                                                    color = if (isGodModeActive) AccentGold else ElegantTextPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "100% победа в Рулетке и игре «Угадай число»",
                                                    color = ElegantTextSecondary,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Switch(
                                                checked = isGodModeActive,
                                                onCheckedChange = { onToggleGodMode(it) },
                                                colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentEmerald)
                                            )
                                        }
                                    }
                                }
                            }

                            2 -> {
                                // BALANCE & TREASURY
                                item {
                                    AdminActionTile(
                                        title = "💵 +100 000 ₴ НА БАЛАНС",
                                        subtitle = "Мгновенно пополняет баланс игрока на 100 тысяч гривен",
                                        buttonText = "Начислить 100k ₴",
                                        buttonColor = AccentEmerald,
                                        onClick = { onAddBalance(100000L) }
                                    )
                                }

                                item {
                                    AdminActionTile(
                                        title = "💎 +1 000 000 ₴ (РЕЖИМ ОЛИГАРХА)",
                                        subtitle = "Зачисляет 1 миллион гривен на счёт",
                                        buttonText = "Начислить 1M ₴",
                                        buttonColor = AccentGold,
                                        onClick = { onAddBalance(1000000L) }
                                    )
                                }

                                item {
                                    AdminActionTile(
                                        title = "🎰 УСТАНОВИТЬ ДЖЕКПОТ В 5 000 000 ₴",
                                        subtitle = "Раздувает прогрессивный джекпот казино до рекордных 5M ₴",
                                        buttonText = "Установить 5M ₴",
                                        buttonColor = Color(0xFFFF5252),
                                        onClick = { onSetJackpot(5000000L) }
                                    )
                                }
                            }

                            3 -> {
                                // TROLLING & P2P ALERTS
                                item {
                                    Text(
                                        text = "ОТПРАВКА СИСТЕМНЫХ ТРОЛЛИНГ-ОПОВЕЩЕНИЙ В P2P",
                                        color = ElegantTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                item {
                                    AdminActionTile(
                                        title = "🚨 «Казино заблокировано за щедрость»",
                                        subtitle = "Отправляет страшный системный алерт о проверке безопасности",
                                        buttonText = "Запустить алерт",
                                        buttonColor = AccentRed,
                                        onClick = { onSendTrollAlert("🚨 СИСТЕМА: Казино временно заблокировано службой безопасности за сверхвысокую щедрость!") }
                                    )
                                }

                                item {
                                    AdminActionTile(
                                        title = "👑 «Хост активировал чит-коды»",
                                        subtitle = "Сообщает всем игрокам, что хост включил читы",
                                        buttonText = "Отправить",
                                        buttonColor = AccentPurple,
                                        onClick = { onSendTrollAlert("👑 АДМИН-ХОСТ активировал чит-коды на отдачу x999!") }
                                    )
                                }

                                item {
                                    AdminActionTile(
                                        title = "💸 «Админ начислил всем +50 000 ₴»",
                                        subtitle = "Праздничный бонусный вброс в чат",
                                        buttonText = "Раздать",
                                        buttonColor = AccentEmerald,
                                        onClick = { onSendTrollAlert("🎉 ПРАЗДНИК: Админ начислил всем игрокам по +50 000 ₴!") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundPresetButton(title: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.2f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(title, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AdminActionTile(
    title: String,
    subtitle: String,
    buttonText: String,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantDarkBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = ElegantTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = ElegantTextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp)
            ) {
                Text(
                    text = buttonText,
                    color = if (buttonColor == AccentGold) Color.Black else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
