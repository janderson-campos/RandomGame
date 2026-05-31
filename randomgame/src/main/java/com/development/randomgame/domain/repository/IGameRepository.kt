package com.development.randomgame.domain.repository

import com.development.randomgame.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface IGameRepository {
    val history: Flow<List<Game>>
    suspend fun saveGameToHistory(game: Game)
    suspend fun clearHistory()
}
