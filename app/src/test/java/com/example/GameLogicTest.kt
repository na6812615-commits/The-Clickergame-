package com.example

import com.example.data.ComboSystem
import com.example.data.GameStateEntity
import com.example.ui.Formatters
import org.junit.Assert.assertEquals
import org.junit.Test

class GameLogicTest {
    @Test
    fun `initial game state matches Python script defaults`() {
        val state = GameStateEntity()
        assertEquals(0.0, state.diem, 0.0001)
        assertEquals(0.0, state.dollars, 0.0001)
        assertEquals(1.0, state.multiplier, 0.0001)
        assertEquals(1.0, state.pointsPerClick, 0.0001)
        assertEquals(0.25, state.clickCooldownSec, 0.0001)
        assertEquals(25L, state.discountedPointCost(state.pointPowerCost))
        assertEquals(50L, state.discountedPointCost(state.multiplierCost))
        assertEquals(75L, state.discountedPointCost(state.cooldownCost))
        assertEquals(150L, state.restartRequirement)
    }

    @Test
    fun `discount factor reduces point shop and restart costs`() {
        val state = GameStateEntity(discountFactor = 1.5)
        assertEquals(17L, state.discountedPointCost(25.0))
        assertEquals(100L, state.restartRequirement)
    }

    @Test
    fun `combo system returns expected multipliers across tiers`() {
        assertEquals(1.0, ComboSystem.multiplierFor(1), 0.0001)
        assertEquals(1.2, ComboSystem.multiplierFor(5), 0.0001)
        assertEquals(1.5, ComboSystem.multiplierFor(10), 0.0001)
        assertEquals(2.0, ComboSystem.multiplierFor(20), 0.0001)
        assertEquals(2.5, ComboSystem.multiplierFor(35), 0.0001)
        assertEquals(3.0, ComboSystem.multiplierFor(50), 0.0001)
    }

    @Test
    fun `formatters render points dollars and multiplier cleanly`() {
        assertEquals("1,250", Formatters.formatPoints(1250.4))
        assertEquals("$25", Formatters.formatDollars(25.0))
        assertEquals("$12.50", Formatters.formatDollars(12.5))
        assertEquals("1.5x", Formatters.formatMultiplier(1.5))
        assertEquals("0.25s", Formatters.formatSeconds(0.25))
    }
}
