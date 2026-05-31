package com.development.randomgame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.development.randomgame.domain.model.Game
import com.development.randomgame.domain.model.GameSpec
import com.development.randomgame.domain.repository.IGameRepository
import com.development.randomgame.games.GameList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por gerenciar o estado da UI e a lógica de negócios relacionada aos jogos.
 * Utiliza o [IGameRepository] para abstrair a persistência de dados.
 */
class GameViewModel(private val repository: IGameRepository) : ViewModel() {
    
    // Observa o histórico de jogos do repositório e o converte em um StateFlow
    val history: StateFlow<List<Game>> = repository.history.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Estado de carregamento para operações assíncronas
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Lista de jogos disponíveis (filtrados por categoria)
    private val _onlineGames = MutableStateFlow<List<Game>>(emptyList())
    val onlineGames: StateFlow<List<Game>> = _onlineGames.asStateFlow()

    // Configurações do usuário
    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _visualEffectsEnabled = MutableStateFlow(true)
    val visualEffectsEnabled: StateFlow<Boolean> = _visualEffectsEnabled.asStateFlow()

    // Funções para alternar configurações
    fun toggleSound(enabled: Boolean) {
        _soundEnabled.value = enabled
    }

    fun toggleHaptic(enabled: Boolean) {
        _hapticEnabled.value = enabled
    }

    fun toggleVisualEffects(enabled: Boolean) {
        _visualEffectsEnabled.value = enabled
    }

    // Adiciona um jogo sorteado ao histórico
    fun addToHistory(game: Game) {
        viewModelScope.launch {
            repository.saveGameToHistory(game)
        }
    }

    // Remove todos os itens do histórico
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    /**
     * Carrega os jogos com base na especificação técnica (setup) selecionada.
     */
    fun fetchGamesFromInternet(spec: GameSpec) {
        _isLoading.value = true
        viewModelScope.launch {
            // Simula um pequeno atraso para feedback visual
            _onlineGames.value = GameList.getGamesBySpec(spec)
            _isLoading.value = false
        }
    }
}
