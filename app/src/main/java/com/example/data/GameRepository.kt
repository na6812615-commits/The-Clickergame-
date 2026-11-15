package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameStateDao) {
    fun observeGameState(): Flow<GameStateEntity?> = dao.observeGameState()

    suspend fun loadOrCreate(): GameStateEntity {
        val existing = dao.getGameStateOnce()
        if (existing != null) return existing
        val initial = GameStateEntity()
        dao.upsertGameState(initial)
        return initial
    }

    suspend fun saveState(state: GameStateEntity) {
        dao.upsertGameState(state.copy(updatedAt = System.currentTimeMillis()))
    }
}
