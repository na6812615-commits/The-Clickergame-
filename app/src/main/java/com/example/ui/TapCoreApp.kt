package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.FeedbackBanner
import com.example.ui.components.StatsHud
import com.example.ui.screens.CashShopScreen
import com.example.ui.screens.PointShopScreen
import com.example.ui.screens.RestartScreen
import com.example.ui.screens.TapCoreScreen
import com.example.ui.theme.CarbonSurface
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDim
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBgDeep
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.GameTab
import com.example.viewmodel.GameViewModel
import kotlin.math.roundToInt

private data class NavDestination(
    val tab: GameTab,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

private val Destinations = listOf(
    NavDestination(
        tab = GameTab.TAP,
        labelRes = R.string.tab_play,
        selectedIcon = Icons.Rounded.Bolt,
        unselectedIcon = Icons.Outlined.Bolt,
        tag = "nav_tab_play"
    ),
    NavDestination(
        tab = GameTab.POINT_SHOP,
        labelRes = R.string.tab_point_shop,
        selectedIcon = Icons.Rounded.ShoppingBag,
        unselectedIcon = Icons.Outlined.ShoppingBag,
        tag = "nav_tab_point_shop"
    ),
    NavDestination(
        tab = GameTab.CASH_SHOP,
        labelRes = R.string.tab_cash_shop,
        selectedIcon = Icons.Rounded.AttachMoney,
        unselectedIcon = Icons.Outlined.AttachMoney,
        tag = "nav_tab_cash_shop"
    ),
    NavDestination(
        tab = GameTab.RESTARTS,
        labelRes = R.string.tab_restarts,
        selectedIcon = Icons.Rounded.RestartAlt,
        unselectedIcon = Icons.Outlined.RestartAlt,
        tag = "nav_tab_restarts"
    )
)

@Composable
fun TapCoreApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.selectedTab != GameTab.TAP && !uiState.showAutoUpgradeSheet && !uiState.showRestartDialog) {
        BackHandler {
            viewModel.selectTab(GameTab.TAP)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ObsidianBg, ObsidianBgDeep)
                )
            )
    ) {
        val useRail = maxWidth >= 840.dp

        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = CarbonSurface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        Destinations.forEach { dest ->
                            val selected = uiState.selectedTab == dest.tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(dest.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = {
                                    Text(text = stringResource(dest.labelRes))
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CyberCyan,
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyanDim,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag(dest.tag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyberCyan)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useRail) {
                        NavigationRail(
                            containerColor = CarbonSurface,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Destinations.forEach { dest ->
                                val selected = uiState.selectedTab == dest.tab
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { viewModel.selectTab(dest.tab) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                            contentDescription = stringResource(dest.labelRes)
                                        )
                                    },
                                    label = { Text(stringResource(dest.labelRes)) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = CyberCyan,
                                        selectedTextColor = CyberCyan,
                                        indicatorColor = CyberCyanDim,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag(dest.tag)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(max = 620.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            StatsHud(game = uiState.game)

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                val transitionMs = (uiState.game.menuTransitionSec * 280.0)
                                    .roundToInt()
                                    .coerceIn(50, 320)

                                AnimatedContent(
                                    targetState = uiState.selectedTab,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(transitionMs)) togetherWith
                                            fadeOut(animationSpec = tween(transitionMs))
                                    },
                                    label = "tab_content_transition"
                                ) { tab ->
                                    when (tab) {
                                        GameTab.TAP -> TapCoreScreen(
                                            uiState = uiState,
                                            onTapCore = viewModel::onTapCore,
                                            onToggleAuto = viewModel::toggleAutoEnabled,
                                            onOpenCashShop = { viewModel.selectTab(GameTab.CASH_SHOP) }
                                        )
                                        GameTab.POINT_SHOP -> PointShopScreen(
                                            game = uiState.game,
                                            onBuyPointPower = viewModel::buyPointPower,
                                            onBuyMultiplier = viewModel::buyMultiplier,
                                            onBuyCooldown = viewModel::buyCooldownReduction
                                        )
                                        GameTab.CASH_SHOP -> CashShopScreen(
                                            game = uiState.game,
                                            showAutoUpgradeSheet = uiState.showAutoUpgradeSheet,
                                            onExchange1000 = viewModel::exchange1000Points,
                                            onExchangeAll = viewModel::exchangeAllPoints,
                                            onBuyDiscount = viewModel::buyDiscount,
                                            onBuyMenuSpeed = viewModel::buyMenuSpeed,
                                            onBuyToastSpeed = viewModel::buyToastSpeed,
                                            onBuyAutoClick = viewModel::buyAutoClick,
                                            onOpenAutoUpgrade = viewModel::openAutoUpgradeSheet,
                                            onDismissAutoUpgrade = viewModel::dismissAutoUpgradeSheet,
                                            onBuyAutoPower = viewModel::buyAutoPower,
                                            onBuyAutoSpeed = viewModel::buyAutoSpeed
                                        )
                                        GameTab.RESTARTS -> RestartScreen(
                                            game = uiState.game,
                                            showConfirmDialog = uiState.showRestartDialog,
                                            onRequestRestart = viewModel::requestRestartDialog,
                                            onConfirmRestart = viewModel::confirmRestart,
                                            onDismissDialog = viewModel::dismissRestartDialog
                                        )
                                    }
                                }

                                FeedbackBanner(
                                    banner = uiState.toastBanner,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
