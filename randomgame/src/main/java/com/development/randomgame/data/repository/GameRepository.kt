package com.development.randomgame.data.repository

import com.development.randomgame.data.local.GameDao
import com.development.randomgame.data.local.toDomain
import com.development.randomgame.data.local.toEntity
import com.development.randomgame.domain.model.Game
import com.development.randomgame.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Implementação do repositório que utiliza o Room para persistência de dados
class GameRepository(private val gameDao: GameDao) : IGameRepository {

    // Obtém o histórico de jogos do banco de dados e converte para o modelo de domínio
    override val history: Flow<List<Game>> = gameDao.getAllGames().map { entities ->
        entities.map { it.toDomain() }
    }

    // Salva um jogo no histórico
    override suspend fun saveGameToHistory(game: Game) {
        gameDao.insertGame(game.toEntity())
    }

    // Limpa todo o histórico de jogos
    override suspend fun clearHistory() {
        gameDao.clearHistory()
    }
}
