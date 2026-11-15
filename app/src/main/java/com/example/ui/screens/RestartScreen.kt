package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.ElevatedCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.ObsidianBgDeep
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RestartScreen(
    game: GameStateEntity,
    showConfirmDialog: Boolean,
    onRequestRestart: () -> Unit,
    onConfirmRestart: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requirement = game.restartRequirement
    val progress = (game.diem / requirement.toDouble()).toFloat().coerceIn(0f, 1f)
    val canRestart = game.diem >= requirement

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("restart_screen"),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = stringResource(R.string.restart_header),
                    style = MaterialTheme.typography.titleLarge,
                    color = AmberGold
                )
                Text(
                    text = stringResource(R.string.restart_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Prestige Hero Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = CarbonSurface,
                border = BorderStroke(1.dp, AmberGold.copy(alpha = if (canRestart) 0.7f else 0.35f)),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cấp Restarts: ${game.restartLv}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberGoldDim
                        ) {
                            Text(
                                text = "LV ${game.restartLv} → ${game.restartLv + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = ElevatedCard
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Hệ số nhân khởi điểm",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = Formatters.formatMultiplier(game.baseMultiplier),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = Formatters.formatMultiplier(game.nextBaseMultiplier),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = AmberGold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tiến độ yêu cầu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "${Formatters.formatPoints(game.diem)} / ${Formatters.formatLong(requirement)} điểm",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (canRestart) NeonEmerald else CyberCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(99.dp)),
                        color = if (canRestart) NeonEmerald else AmberGold,
                        trackColor = ElevatedCard
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onRequestRestart,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canRestart) AmberGold else ElevatedCard,
                            contentColor = if (canRestart) ObsidianBgDeep else TextMuted
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .testTag("restart_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (canRestart) {
                                stringResource(R.string.restart_action_btn)
                            } else {
                                "Cần thêm ${Formatters.formatPoints((requirement - game.diem).coerceAtLeast(0.0))} điểm"
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CarbonSurface,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Chi tiết khi Restarts",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "• Giữ lại vĩnh viễn: Cấp Restarts (+1) và Hệ số nhân khởi điểm (+0.5x).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonEmerald
                    )
                    Text(
                        text = "• Đặt lại vòng mới: Điểm, Tiền $, Lượt click, Cửa hàng Điểm, \$Shop\$ và Auto Click.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            containerColor = CarbonSurface,
            titleContentColor = AmberGold,
            textContentColor = TextSecondary,
            title = {
                Text(
                    text = stringResource(R.string.restart_confirm_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.restart_confirm_body,
                        Formatters.formatDecimal(game.baseMultiplier),
                        Formatters.formatDecimal(game.nextBaseMultiplier)
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmRestart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold,
                        contentColor = ObsidianBgDeep
                    ),
                    modifier = Modifier.testTag("confirm_restart_button")
                ) {
                    Text(stringResource(R.string.confirm_action))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissDialog,
                    modifier = Modifier.testTag("cancel_restart_button")
                ) {
                    Text(
                        text = stringResource(R.string.cancel_action),
                        color = TextSecondary
                    )
                }
            }
        )
    }
}
