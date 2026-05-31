package com.development.randomgame.domain.model

import androidx.annotation.DrawableRes
// classificação de classes da categoria do pc
enum class GameSpec {
    LOW_END,
    MEDIUM_END,
    HIGH_END
}

data class Game(
    val name: String,
    val spec: GameSpec,
    val description: String,
    @DrawableRes val imageRes: Int
)
