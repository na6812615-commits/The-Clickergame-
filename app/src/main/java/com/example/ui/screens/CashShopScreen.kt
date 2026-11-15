package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GameStateEntity
import com.example.ui.Formatters
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDim
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDim
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElevatedCard
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldDim
import com.example.ui.theme.ObsidianBgDeep
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashShopScreen(
    game: GameStateEntity,
    showAutoUpgradeSheet: Boolean,
    onExchange1000: () -> Unit,
    onExchangeAll: () -> Unit,
    onBuyDiscount: () -> Unit,
    onBuyMenuSpeed: () -> Unit,
    onBuyToastSpeed: () -> Unit,
    onBuyAutoClick: () -> Unit,
    onOpenAutoUpgrade: () -> Unit,
    onDismissAutoUpgrade: () -> Unit,
    onBuyAutoPower: () -> Unit,
    onBuyAutoSpeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cash_shop_screen"),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.cash_shop_header),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        }

        // Currency Exchange Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = CarbonSurface,
                border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.4f)),
                tonalElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CurrencyExchange,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.exchange_section_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = stringResource(R.string.exchange_rate_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onExchange1000,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (game.diem >= 1000.0) NeonEmerald else ElevatedCard,
                                contentColor = if (game.diem >= 1000.0) ObsidianBgDeep else TextMuted
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("exchange_1000_button")
                        ) {
                            Text(
                                text = "1.000 điểm → $1",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = onExchangeAll,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (game.diem > 0.0) CyberCyan else CardBorder
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (game.diem > 0.0) CyberCyan else TextMuted
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("exchange_all_button")
                        ) {
                            val preview = Formatters.formatDollars(game.diem / 1000.0)
                            Text(
                                text = "Đổi hết ($preview)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.cash_shop_section_title),
                style = MaterialTheme.typography.titleMedium,
                color = NeonEmerald
            )
        }

        // 1: Giảm giá (giamgia)
        item {
            CashShopCard(
                icon = Icons.Rounded.LocalOffer,
                accent = NeonEmerald,
                accentDim = NeonEmeraldDim,
                title = "Giảm giá cửa hàng Điểm & Restart",
                description = "Tăng hệ số chia giá (${Formatters.formatDecimal(game.discountFactor)} → ${Formatters.formatDecimal(game.discountFactor + game.discountStep)})",
                levelLabel = "LV ${game.discountLv}",
                priceLabel = Formatters.formatDollars(game.discountCost),
                affordable = game.dollars >= game.discountCost,
                purchased = false,
                actionLabel = stringResource(R.string.buy_button),
                buttonTag = "buy_discount_button",
                onClick = onBuyDiscount
            )
        }

        // 2: Giảm thời gian rời khỏi menu (cd3)
        item {
            CashShopCard(
                icon = Icons.Rounded.FlashOn,
                accent = CyberCyan,
                accentDim = CyberCyanDim,
                title = "Giảm thời gian chuyển cảnh",
                description = "Tăng tốc độ chuyển tab và hộp thoại (${Formatters.formatSeconds(game.menuTransitionSec)} → ${Formatters.formatSeconds((game.menuTransitionSec / 2.0).coerceAtLeast(0.05))})",
                levelLabel = "LV ${game.menuSpeedLv}",
                priceLabel = Formatters.formatDollars(game.menuSpeedCost),
                affordable = game.dollars >= game.menuSpeedCost,
                purchased = false,
                actionLabel = stringResource(R.string.buy_button),
                buttonTag = "buy_menu_speed_button",
                onClick = onBuyMenuSpeed
            )
        }

        // 3: Giảm thời gian thông báo (cd4)
        item {
            CashShopCard(
                icon = Icons.Rounded.NotificationsActive,
                accent = AmberGold,
                accentDim = AmberGoldDim,
                title = "Giảm thời gian thông báo",
                description = "Thu gọn nhanh hơn thông báo trạng thái (${Formatters.formatSeconds(game.toastDurationSec)} → ${Formatters.formatSeconds((game.toastDurationSec / 2.0).coerceAtLeast(0.15))})",
                levelLabel = "LV ${game.toastSpeedLv}",
                priceLabel = Formatters.formatDollars(game.toastSpeedCost),
                affordable = game.dollars >= game.toastSpeedCost,
                purchased = false,
                actionLabel = stringResource(R.string.buy_button),
                buttonTag = "buy_toast_speed_button",
                onClick = onBuyToastSpeed
            )
        }

        // 4: Auto Click ($25)
        item {
            CashShopCard(
                icon = Icons.Rounded.AutoMode,
                accent = NeonEmerald,
                accentDim = NeonEmeraldDim,
                title = "Auto Click tự động",
                description = "Tự động cộng điểm mỗi ${Formatters.formatSeconds(game.autoCooldownSec)} ngay cả khi không chạm tay",
                levelLabel = if (game.autoUnlocked) "ĐÃ MỞ" else "1 LẦN/VÒNG",
                priceLabel = if (game.autoUnlocked) stringResource(R.string.purchased_badge) else "$25",
                affordable = !game.autoUnlocked && game.dollars >= 25.0,
                purchased = game.autoUnlocked,
                actionLabel = if (game.autoUnlocked) stringResource(R.string.purchased_badge) else stringResource(R.string.buy_button),
                buttonTag = "buy_auto_click_button",
                onClick = onBuyAutoClick
            )
        }

        // 5: Nâng cấp Auto Click
        item {
            CashShopCard(
                icon = Icons.Rounded.Tune,
                accent = ElectricViolet,
                accentDim = ElevatedCard,
                title = stringResource(R.string.auto_upgrade_sheet_title),
                description = "Tăng sức mạnh điểm Auto (x2) và rút ngắn chu kỳ Auto (${Formatters.formatDecimal(game.autoPoints)} điểm / ${Formatters.formatSeconds(game.autoCooldownSec)})",
                levelLabel = "LV ${game.autoPowerLv + game.autoSpeedLv}",
                priceLabel = if (game.autoUnlocked) "Khả dụng" else "Cần Auto Click",
                affordable = game.autoUnlocked,
                purchased = false,
                actionLabel = stringResource(R.string.open_auto_upgrade_btn),
                buttonTag = "open_auto_upgrade_button",
                onClick = onOpenAutoUpgrade
            )
        }
    }

    if (showAutoUpgradeSheet) {
        BackHandler {
            onDismissAutoUpgrade()
        }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onDismissAutoUpgrade,
            sheetState = sheetState,
            containerColor = CarbonSurface,
            contentColor = TextPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.auto_upgrade_sheet_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                if (!game.autoUnlocked) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ElevatedCard,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text = stringResource(R.string.auto_upgrade_locked_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AmberGold,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                CashShopCard(
                    icon = Icons.Rounded.Bolt,
                    accent = NeonEmerald,
                    accentDim = NeonEmeraldDim,
                    title = "Tăng điểm Auto Click (x2)",
                    description = "Điểm gốc mỗi nhịp Auto: ${Formatters.formatDecimal(game.autoPoints)} → ${Formatters.formatDecimal(game.autoPoints * 2.0)}",
                    levelLabel = "LV ${game.autoPowerLv}",
                    priceLabel = Formatters.formatDollars(game.autoPowerCost),
                    affordable = game.autoUnlocked && game.dollars >= game.autoPowerCost,
                    purchased = false,
                    actionLabel = stringResource(R.string.buy_button),
                    buttonTag = "buy_auto_power_button",
                    onClick = onBuyAutoPower
                )

                val nextAutoCd = (game.autoCooldownSec - game.autoSpeedStep).coerceAtLeast(0.05)
                CashShopCard(
                    icon = Icons.Rounded.Speed,
                    accent = CyberCyan,
                    accentDim = CyberCyanDim,
                    title = "Tăng tốc chu kỳ Auto Click",
                    description = "Chu kỳ kích hoạt: ${Formatters.formatSeconds(game.autoCooldownSec)} → ${Formatters.formatSeconds(nextAutoCd)}",
                    levelLabel = "LV ${game.autoSpeedLv}",
                    priceLabel = Formatters.formatDollars(game.autoSpeedCost),
                    affordable = game.autoUnlocked && game.dollars >= game.autoSpeedCost,
                    purchased = false,
                    actionLabel = stringResource(R.string.buy_button),
                    buttonTag = "buy_auto_speed_button",
                    onClick = onBuyAutoSpeed
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CashShopCard(
    icon: ImageVector,
    accent: Color,
    accentDim: Color,
    title: String,
    description: String,
    levelLabel: String,
    priceLabel: String,
    affordable: Boolean,
    purchased: Boolean,
    actionLabel: String,
    buttonTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CarbonSurface,
        border = BorderStroke(
            1.dp,
            when {
                purchased -> NeonEmerald.copy(alpha = 0.5f)
                affordable -> accent.copy(alpha = 0.45f)
                else -> CardBorder
            }
        ),
        tonalElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = accentDim,
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.4f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElevatedCard
                        ) {
                            Text(
                                text = levelLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = accent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = priceLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (purchased || affordable) accent else TextMuted
                )

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (affordable) accent else ElevatedCard,
                        contentColor = if (affordable) ObsidianBgDeep else TextMuted
                    ),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag(buttonTag)
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
