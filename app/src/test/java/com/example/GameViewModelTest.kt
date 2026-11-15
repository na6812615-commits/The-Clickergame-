package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.GameStateEntity
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GameViewModelTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: GameRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = GameRepository(db.gameStateDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `point shop exchange cash shop and restart flow work accurately`() = runBlocking(Dispatchers.Main) {
        repository.saveState(
            GameStateEntity(
                diem = 2500.0,
                dollars = 30.0
            )
        )
        val viewModel = GameViewModel(repository)
        delay(100)

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(2500.0, viewModel.uiState.value.game.diem, 0.01)

        // Buy Point Power (costs 25 -> x becomes 2, cost becomes 75, lv becomes 1)
        viewModel.buyPointPower()
        assertEquals(2475.0, viewModel.uiState.value.game.diem, 0.01)
        assertEquals(2.0, viewModel.uiState.value.game.pointsPerClick, 0.01)
        assertEquals(1, viewModel.uiState.value.game.pointPowerLv)

        // Exchange 1000 points -> $1
        viewModel.exchange1000Points()
        assertEquals(1475.0, viewModel.uiState.value.game.diem, 0.01)
        assertEquals(31.0, viewModel.uiState.value.game.dollars, 0.01)

        // Buy Auto Click ($25)
        viewModel.buyAutoClick()
        assertTrue(viewModel.uiState.value.game.autoUnlocked)
        assertTrue(viewModel.uiState.value.game.autoEnabled)
        assertEquals(6.0, viewModel.uiState.value.game.dollars, 0.01)

        // Confirm Restart (requires 150 points, we have 1475)
        viewModel.confirmRestart()
        val afterRestart = viewModel.uiState.value.game
        assertEquals(0.0, afterRestart.diem, 0.01)
        assertEquals(0.0, afterRestart.dollars, 0.01)
        assertEquals(1.5, afterRestart.multiplier, 0.01)
        assertEquals(1.5, afterRestart.baseMultiplier, 0.01)
        assertEquals(1, afterRestart.restartLv)
        assertFalse(afterRestart.autoUnlocked)

        // Tap 5 times waiting out 0.25s cooldown -> reaches 5x combo (1.2x combo multiplier)
        repeat(5) {
            viewModel.onTapCore()
            delay(300)
        }
        assertEquals(5, viewModel.uiState.value.comboCount)
        assertEquals(1.2, viewModel.uiState.value.comboMultiplier, 0.01)
        assertEquals(5, viewModel.uiState.value.game.bestCombo)
    }
}
