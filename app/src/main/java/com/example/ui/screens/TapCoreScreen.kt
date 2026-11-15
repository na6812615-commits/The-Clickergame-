package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.Formatters
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDim
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralCrimson
import com.example.ui.theme.CoralCrimsonDim
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDim
import com.example.ui.theme.ElevatedCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldDim
import com.example.ui.theme.ObsidianBgDeep
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.FloatingPopup
import com.example.viewmodel.GameUiState
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun TapCoreScreen(
    uiState: GameUiState,
    onTapCore: () -> Unit,
    onToggleAuto: () -> Unit,
    onOpenCashShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val game = uiState.game
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    val comboAccent by animateColorAsState(
        targetValue = comboColorFor(uiState.comboCount),
        animationSpec = tween(durationMillis = 220),
        label = "combo_accent"
    )

    val coreScale by animateFloatAsState(
        targetValue = if (uiState.isCooldownActive) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "core_scale"
    )

    val autoPulse by animateFloatAsState(
        targetValue = if (uiState.autoTickCounter % 2L == 0L) 1f else 1.08f,
        animationSpec = tween(durationMillis = 180),
        label = "auto_pulse"
    )

    val currentTapGain = game.effectivePointsPerClick * uiState.comboMultiplier

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Click Combo HUD Banner
        ClickComboBanner(
            comboCount = uiState.comboCount,
            comboMultiplier = uiState.comboMultiplier,
            comboRemainingFraction = uiState.comboRemainingFraction,
            comboTierLabel = uiState.comboTierLabel,
            bestCombo = game.bestCombo,
            accent = comboAccent
        )

        // Central Tap Core Reactor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient glow behind reactor
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                comboAccent.copy(alpha = if (uiState.isCooldownActive) 0.14f else 0.28f),
                                NeonEmerald.copy(alpha = 0.06f),
                                ObsidianBgDeep.copy(alpha = 0f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Cooldown & orbital ring canvas
            val ringProgress = 1f - uiState.cooldownRemainingFraction
            Canvas(modifier = Modifier.size(236.dp)) {
                val strokeWidth = 10.dp.toPx()
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                drawArc(
                    color = CardBorder.copy(alpha = 0.7f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(comboAccent, NeonEmerald, comboAccent)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * ringProgress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Decorative cyber tick marks
                val center = Offset(size.width / 2f, size.height / 2f)
                val radiusOuter = size.minDimension / 2f - strokeWidth * 1.4f
                for (i in 0 until 12) {
                    val angle = Math.toRadians((i * 30).toDouble())
                    val start = Offset(
                        x = center.x + (radiusOuter - 6.dp.toPx()) * cos(angle).toFloat(),
                        y = center.y + (radiusOuter - 6.dp.toPx()) * sin(angle).toFloat()
                    )
                    val end = Offset(
                        x = center.x + radiusOuter * cos(angle).toFloat(),
                        y = center.y + radiusOuter * sin(angle).toFloat()
                    )
                    drawLine(
                        color = comboAccent.copy(alpha = 0.35f),
                        start = start,
                        end = end,
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // Interactive Tap Core Button
            val tapCd = stringResource(R.string.tap_core_button_cd)
            Surface(
                modifier = Modifier
                    .size(190.dp)
                    .scale(coreScale)
                    .clip(CircleShape)
                    .semantics { contentDescription = tapCd }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = comboAccent),
                        role = Role.Button,
                        onClick = {
                            if (!uiState.isCooldownActive) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTapCore()
                            }
                        }
                    )
                    .testTag("tap_core_button"),
                shape = CircleShape,
                color = CyberCyanDim,
                border = BorderStroke(
                    width = 2.dp,
                    color = if (uiState.isCooldownActive) comboAccent.copy(alpha = 0.45f) else comboAccent
                ),
                tonalElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    comboAccent.copy(alpha = 0.25f),
                                    CyberCyanDim,
                                    ObsidianBgDeep
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null,
                            tint = if (uiState.isCooldownActive) TextSecondary else comboAccent,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+${Formatters.formatDecimal(currentTapGain)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.isCooldownActive) {
                                stringResource(R.string.tap_core_cooldown)
                            } else {
                                stringResource(R.string.tap_core_ready)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (uiState.isCooldownActive) AmberGold else comboAccent
                        )
                    }
                }
            }

            // Floating +points popups
            uiState.floatingPopups.forEach { popup ->
                FloatingPointChip(popup = popup)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Auto Clicker Status Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auto_click_status_card"),
            shape = RoundedCornerShape(18.dp),
            color = CarbonSurface,
            border = BorderStroke(
                width = 1.dp,
                color = if (game.autoUnlocked && game.autoEnabled) {
                    NeonEmerald.copy(alpha = 0.5f)
                } else {
                    CardBorder
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .scale(if (game.autoUnlocked && game.autoEnabled) autoPulse else 1f),
                        shape = CircleShape,
                        color = if (game.autoUnlocked && game.autoEnabled) NeonEmeraldDim else ElevatedCard,
                        border = BorderStroke(
                            1.dp,
                            if (game.autoUnlocked && game.autoEnabled) NeonEmerald else CardBorder
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (game.autoUnlocked) Icons.Rounded.AutoMode else Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = if (game.autoUnlocked && game.autoEnabled) NeonEmerald else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = when {
                                !game.autoUnlocked -> stringResource(R.string.auto_status_locked)
                                game.autoEnabled -> stringResource(R.string.auto_status_active)
                                else -> stringResource(R.string.auto_status_paused)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (game.autoUnlocked) {
                                "+${Formatters.formatDecimal(game.effectiveAutoPointsPerTick)} điểm / ${Formatters.formatSeconds(game.autoCooldownSec)}"
                            } else {
                                "Mở khóa với giá $25 tại mục \$Shop\$"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (game.autoUnlocked) {
                    IconButton(
                        onClick = onToggleAuto,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (game.autoEnabled) NeonEmeraldDim else ElevatedCard,
                            contentColor = if (game.autoEnabled) NeonEmerald else AmberGold
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("auto_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (game.autoEnabled) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.auto_toggle_cd)
                        )
                    }
                } else {
                    TextButton(
                        onClick = onOpenCashShop,
                        modifier = Modifier.testTag("go_to_cash_shop_button")
                    ) {
                        Text(
                            text = "Mở \$Shop\$",
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClickComboBanner(
    comboCount: Int,
    comboMultiplier: Double,
    comboRemainingFraction: Float,
    comboTierLabel: String,
    bestCombo: Int,
    accent: Color
) {
    val isActive = comboCount > 0
    val comboScale by animateFloatAsState(
        targetValue = if (isActive && comboCount % 2 == 1) 1.03f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "combo_banner_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(comboScale)
            .testTag("click_combo_banner"),
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) comboContainerFor(comboCount) else CarbonSurface,
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) accent.copy(alpha = 0.6f) else CardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = null,
                        tint = if (isActive) accent else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isActive) {
                        Text(
                            text = "${comboCount}x COMBO",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = accent,
                            modifier = Modifier.testTag("combo_count_text")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ElevatedCard
                        ) {
                            val bonusText = if (comboMultiplier > 1.0) {
                                "$comboTierLabel • x${Formatters.formatDecimal(comboMultiplier)}"
                            } else {
                                "$comboTierLabel (5+ để thưởng)"
                            }
                            Text(
                                text = bonusText,
                                style = MaterialTheme.typography.labelSmall,
                                color = accent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.combo_hint_idle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.combo_best_label, bestCombo),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            if (isActive) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { comboRemainingFraction.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(99.dp)),
                    color = accent,
                    trackColor = ElevatedCard
                )
            }
        }
    }
}

private fun comboColorFor(combo: Int): Color = when {
    combo >= 50 -> CoralCrimson
    combo >= 20 -> AmberGold
    combo >= 5 -> NeonEmerald
    else -> CyberCyan
}

private fun comboContainerFor(combo: Int): Color = when {
    combo >= 50 -> CoralCrimsonDim
    combo >= 20 -> AmberGoldDim
    combo >= 5 -> NeonEmeraldDim
    else -> CyberCyanDim
}

@Composable
private fun FloatingPointChip(popup: FloatingPopup) {
    val riseAnim = remember(popup.id) { Animatable(0f) }
    val alphaAnim = remember(popup.id) { Animatable(1f) }

    LaunchedEffect(popup.id) {
        riseAnim.animateTo(
            targetValue = -110f,
            animationSpec = tween(durationMillis = 600)
        )
    }
    LaunchedEffect(popup.id) {
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 620)
        )
    }

    Text(
        text = popup.text,
        style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        color = comboColorFor(popup.comboCount),
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (popup.offsetXFraction * 180f).roundToInt(),
                    y = (-64f + riseAnim.value).roundToInt()
                )
            }
            .alpha(alphaAnim.value)
    )
}
