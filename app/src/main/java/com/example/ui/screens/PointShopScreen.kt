package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.ElevatedCard
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldDim
import com.example.ui.theme.ObsidianBgDeep
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PointShopScreen(
    game: GameStateEntity,
    onBuyPointPower: () -> Unit,
    onBuyMultiplier: () -> Unit,
    onBuyCooldown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val powerCost = game.discountedPointCost(game.pointPowerCost)
    val multCost = game.discountedPointCost(game.multiplierCost)
    val cdCost = game.discountedPointCost(game.cooldownCost)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("point_shop_screen"),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.point_shop_header),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.point_shop_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                if (game.discountFactor > 1.0) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NeonEmeraldDim,
                        border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocalOffer,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.discount_active_badge, game.discountFactor),
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonEmerald
                            )
                        }
                    }
                }
            }
        }

        item {
            PointUpgradeCard(
                icon = Icons.Rounded.Bolt,
                accent = CyberCyan,
                accentDim = CyberCyanDim,
                title = "+${Formatters.formatDecimal(game.pointsPerClick)} điểm gốc mỗi click",
                description = "Nhân đôi điểm cơ bản mỗi lần chạm (${Formatters.formatDecimal(game.pointsPerClick)} → ${Formatters.formatDecimal(game.pointsPerClick * 2)})",
                level = game.pointPowerLv,
                cost = powerCost,
                affordable = game.diem >= powerCost,
                missingPoints = (powerCost - game.diem).coerceAtLeast(0.0),
                buttonTag = "buy_point_power_button",
                onBuy = onBuyPointPower
            )
        }

        item {
            PointUpgradeCard(
                icon = Icons.Rounded.TrendingUp,
                accent = AmberGold,
                accentDim = AmberGoldDim,
                title = "Thêm +0.1x hệ số nhân",
                description = "Khuếch đại toàn bộ điểm chạm tay & Auto Click (${Formatters.formatMultiplier(game.multiplier)} → ${Formatters.formatMultiplier(game.multiplier + 0.1)})",
                level = game.multiplierLv,
                cost = multCost,
                affordable = game.diem >= multCost,
                missingPoints = (multCost - game.diem).coerceAtLeast(0.0),
                buttonTag = "buy_multiplier_button",
                onBuy = onBuyMultiplier
            )
        }

        item {
            val nextCd = (game.clickCooldownSec - game.cooldownStep).coerceAtLeast(0.01)
            PointUpgradeCard(
                icon = Icons.Rounded.Speed,
                accent = NeonEmerald,
                accentDim = NeonEmeraldDim,
                title = "Giảm hồi chiêu giữa các lần click",
                description = "Rút ngắn thời gian chờ mỗi lần chạm (${Formatters.formatSeconds(game.clickCooldownSec)} → ${Formatters.formatSeconds(nextCd)})",
                level = game.cooldownLv,
                cost = cdCost,
                affordable = game.diem >= cdCost,
                missingPoints = (cdCost - game.diem).coerceAtLeast(0.0),
                buttonTag = "buy_cooldown_button",
                onBuy = onBuyCooldown
            )
        }
    }
}

@Composable
private fun PointUpgradeCard(
    icon: ImageVector,
    accent: Color,
    accentDim: Color,
    title: String,
    description: String,
    level: Int,
    cost: Long,
    affordable: Boolean,
    missingPoints: Double,
    buttonTag: String,
    onBuy: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CarbonSurface,
        border = BorderStroke(
            1.dp,
            if (affordable) accent.copy(alpha = 0.45f) else CardBorder
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
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
                                text = "LV $level",
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
                Column {
                    Text(
                        text = "${Formatters.formatLong(cost)} điểm",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (affordable) accent else TextMuted
                    )
                    if (!affordable) {
                        Text(
                            text = stringResource(R.string.need_more_points, Formatters.formatPoints(missingPoints)),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                Button(
                    onClick = onBuy,
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
                        text = stringResource(R.string.buy_button),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
