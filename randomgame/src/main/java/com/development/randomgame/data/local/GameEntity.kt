package com.development.randomgame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.development.randomgame.domain.model.Game
import com.development.randomgame.domain.model.GameSpec

/**
 * Entidade que representa a tabela "game_history" no banco de dados local.
 */
@Entity(tableName = "game_history")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val spec: String,
    val description: String,
    val imageRes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Converte uma GameEntity (Banco de Dados) para o modelo de domínio Game.
 */
fun GameEntity.toDomain(): Game {
    return Game(
        name = name,
        spec = GameSpec.valueOf(spec),
        description = description,
        imageRes = imageRes
    )
}

/**
 * Converte o modelo de domínio Game para uma GameEntity (Banco de Dados).
 */
fun Game.toEntity(): GameEntity {
    return GameEntity(
        name = name,
        spec = spec.name,
        description = description,
        imageRes = imageRes
    )
}
