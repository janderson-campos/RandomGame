package com.development.randomgame.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interface de Acesso a Dados (DAO) para a tabela de histórico de jogos.
 */
@Dao
interface GameDao {
    // Busca todos os jogos do histórico, ordenados pelo mais recente
    @Query("SELECT * FROM game_history ORDER BY timestamp DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    // Insere um novo jogo no histórico. Se já existir (ID duplicado), substitui.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    // Deleta todos os registros da tabela de histórico
    @Query("DELETE FROM game_history")
    suspend fun clearHistory()
}
