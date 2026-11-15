package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GameStateEntity
import com.example.ui.Formatters
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDim
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElevatedCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StatsHud(
    game: GameStateEntity,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("stats_hud"),
        shape = RoundedCornerShape(20.dp),
        color = CarbonSurface,
        border = BorderStroke(1.dp, CardBorder),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ElevatedCard.copy(alpha = 0.7f),
                            CarbonSurface
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyberCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.stats_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                if (game.restartLv > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberGoldDim,
                        border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "RESTART LV ${game.restartLv}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.stats_points_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = Formatters.formatPoints(game.diem),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CyberCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("stats_points_value")
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatBadge(
                        icon = Icons.Rounded.AttachMoney,
                        text = Formatters.formatDollars(game.dollars),
                        tint = NeonEmerald,
                        container = NeonEmeraldDim,
                        tag = "stats_dollars_value"
                    )
                    StatBadge(
                        icon = Icons.Rounded.Bolt,
                        text = Formatters.formatMultiplier(game.multiplier),
                        tint = AmberGold,
                        container = AmberGoldDim,
                        tag = "stats_multiplier_value"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElevatedCard.copy(alpha = 0.75f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MiniStatCell(
                    icon = Icons.Rounded.Bolt,
                    label = stringResource(R.string.stats_per_click_label),
                    value = "+${Formatters.formatDecimal(game.effectivePointsPerClick)}",
                    tint = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                MiniStatCell(
                    icon = Icons.Rounded.TouchApp,
                    label = stringResource(R.string.stats_total_clicks_label),
                    value = Formatters.formatLong(game.clicks),
                    tint = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                MiniStatCell(
                    icon = Icons.Rounded.Speed,
                    label = stringResource(R.string.stats_cooldown_label),
                    value = Formatters.formatSeconds(game.clickCooldownSec),
                    tint = NeonEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatBadge(
    icon: ImageVector,
    text: String,
    tint: Color,
    container: Color,
    tag: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = container,
        border = BorderStroke(1.dp, tint.copy(alpha = 0.35f)),
        modifier = Modifier.testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = tint,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MiniStatCell(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = tint,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
