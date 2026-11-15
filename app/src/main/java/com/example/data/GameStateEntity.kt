package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.roundToLong

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Int = 1,
    val diem: Double = 0.0,
    val dollars: Double = 0.0,
    val clicks: Long = 0L,
    val multiplier: Double = 1.0,
    val baseMultiplier: Double = 1.0,
    val pointsPerClick: Double = 1.0,
    val clickCooldownSec: Double = 0.25,
    val cooldownStep: Double = 0.125,
    val discountFactor: Double = 1.0,
    val discountStep: Double = 0.1,
    val pointPowerLv: Int = 0,
    val pointPowerCost: Double = 25.0,
    val multiplierLv: Int = 0,
    val multiplierCost: Double = 50.0,
    val cooldownLv: Int = 0,
    val cooldownCost: Double = 75.0,
    val discountLv: Int = 0,
    val discountCost: Double = 5.0,
    val menuSpeedLv: Int = 0,
    val menuSpeedCost: Double = 3.0,
    val menuTransitionSec: Double = 1.0,
    val toastSpeedLv: Int = 0,
    val toastSpeedCost: Double = 3.0,
    val toastDurationSec: Double = 1.0,
    val autoUnlocked: Boolean = false,
    val autoEnabled: Boolean = false,
    val autoPoints: Double = 1.0,
    val autoCooldownSec: Double = 1.0,
    val autoPowerLv: Int = 0,
    val autoPowerCost: Double = 15.0,
    val autoSpeedLv: Int = 0,
    val autoSpeedCost: Double = 20.0,
    val autoSpeedStep: Double = 0.25,
    val restartLv: Int = 0,
    val restartCost: Double = 150.0,
    val bestCombo: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun discountedPointCost(baseCost: Double): Long =
        (baseCost / discountFactor).roundToLong().coerceAtLeast(1L)

    val effectivePointsPerClick: Double
        get() = pointsPerClick * multiplier

    val effectiveAutoPointsPerTick: Double
        get() = autoPoints * multiplier

    val restartRequirement: Long
        get() = discountedPointCost(restartCost)

    val nextBaseMultiplier: Double
        get() = baseMultiplier + 0.5
}

object ComboSystem {
    const val COMBO_WINDOW_MS = 2000L

    fun multiplierFor(combo: Int): Double = when {
        combo >= 50 -> 3.0
        combo >= 35 -> 2.5
        combo >= 20 -> 2.0
        combo >= 10 -> 1.5
        combo >= 5 -> 1.2
        else -> 1.0
    }

    fun tierLabelFor(combo: Int): String = when {
        combo >= 50 -> "QUÁ TẢI NEON"
        combo >= 35 -> "SIÊU TỐC"
        combo >= 20 -> "BÙNG NỔ"
        combo >= 10 -> "NHỊP ĐỘ"
        combo >= 5 -> "KHỞI ĐỘNG"
        else -> "CHUỖI COMBO"
    }
}
