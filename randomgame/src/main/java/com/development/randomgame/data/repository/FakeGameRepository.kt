package com.development.randomgame.data.repository

import com.development.randomgame.domain.model.Game
import com.development.randomgame.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGameRepository : IGameRepository {
    override val history: Flow<List<Game>> = flowOf(emptyList())
    override suspend fun saveGameToHistory(game: Game) {}
    override suspend fun clearHistory() {}
}