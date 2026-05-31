package com.development.randomgame.games

import com.development.randomgame.R
import com.development.randomgame.domain.model.Game
import com.development.randomgame.domain.model.GameSpec

object GameList {
    val allGames = listOf(
        // HIGH END
        Game("Cyberpunk 2077", GameSpec.HIGH_END, "Um RPG de ação em mundo aberto ambientado em Night City.", R.drawable.cyberpunk_2077_box_art),
        Game("Starfield", GameSpec.HIGH_END, "Explore o espaço neste RPG épico da Bethesda.", R.drawable.bethesda_starfield),
        Game("Forza Horizon 5", GameSpec.HIGH_END, "O festival de corrida definitivo no México.", R.drawable.forza_horizon_5_cover_art),
        Game("Elden Ring", GameSpec.HIGH_END, "Um vasto mundo de fantasia sombria.", R.drawable.elden_ring_box_art),
        Game("Alan Wake 2", GameSpec.HIGH_END, "Um thriller de terror psicológico.", R.drawable.alan_wake_2_box_art),
        Game("Baldur's Gate 3", GameSpec.HIGH_END, "Um RPG de nova geração baseado no mundo de Dungeons & Dragons.", R.drawable.baldurs_gate_3_cover_art),

        // MEDIUM END
        Game("The Witcher 3: Wild Hunt", GameSpec.MEDIUM_END, "A jornada lendária de Geralt de Rívia.", R.drawable.witcher_3_cover_art),
        Game("Red Dead Redemption 2", GameSpec.MEDIUM_END, "Uma história épica no velho oeste.", R.drawable.red_dead_redemption_ii),
        Game("Grand Theft Auto V", GameSpec.MEDIUM_END, "Três criminosos buscam o sucesso em Los Santos.", R.drawable.grand_theft_auto_v),
        Game("Battlefield 2042", GameSpec.MEDIUM_END, "Guerra total em larga escala.", R.drawable.battlefield_2042_cover_art),
        Game("Resident Evil Village", GameSpec.MEDIUM_END, "Sobreviva ao terror em uma vila isolada.", R.drawable.resident_evil_village),
        Game("Control", GameSpec.MEDIUM_END, "Uma aventura sobrenatural.", R.drawable.control_game_cover_art),

        // LOW END
        Game("Minecraft", GameSpec.LOW_END, "Construa, explore e sobreviva.", R.drawable.minecraft_logo_en_svg),
        Game("Stardew Valley", GameSpec.LOW_END, "Comece uma nova vida na fazenda.", R.drawable.logo_of_stardew_valley),
        Game("Among Us", GameSpec.LOW_END, "Descubra quem é o impostor.", R.drawable.among_us_cover_art),
        Game("Terraria", GameSpec.LOW_END, "Cave, lute, explore, construa!", R.drawable.terraria_steam_artwork),
        Game("Hollow Knight", GameSpec.LOW_END, "Um metroidvania clássico.", R.drawable.hollow_knight_2026_cover_art),
        Game("Celeste", GameSpec.LOW_END, "Enfrente seus demônios internos.", R.drawable.celeste_box_art_full)
    )

    fun getGamesBySpec(spec: GameSpec): List<Game> {
        return allGames.filter { it.spec == spec }
    }
}
