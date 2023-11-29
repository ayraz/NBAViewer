package com.example.nbaviewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.nbaviewer.NBAViewerApplication
import com.example.nbaviewer.data.NBARepository
import com.example.nbaviewer.model.Player
import com.example.nbaviewer.model.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * UI state for the main screen
 */
sealed interface NBAState {
    data class Success(val players: List<Player>) : NBAState
    object Error : NBAState
    object Loading : NBAState
}

class NBAViewerViewModel(private val repository: NBARepository) : ViewModel() {

    /**
     * Player detail state with last selected player
     */
    private val _playerState = MutableStateFlow(Player())
    val playerDetailState: StateFlow<Player> = _playerState.asStateFlow()

    /**
     * Team detail state with last selected team
     */
    private val _teamState = MutableStateFlow(Team())
    val teamDetailState: StateFlow<Team> = _teamState.asStateFlow()

    /**
     * Get players on init so we can display status/list immediately.
     */
    init {
        getNBAPlayers()
    }

    fun getNBAPlayers(): Flow<PagingData<PlayerUiState>> {
        return repository.getPagedPlayersFlow()
            .map {
                it.map { player ->
                    PlayerUiState(
                        player.firstName,
                        player.lastName,
                        player.position,
                        player.team.name,
                        player.id
                    )
                }
            }
            .flowOn(Dispatchers.IO) // don't block main thread
            .cachedIn(viewModelScope)
    }

    fun loadPlayer(id: Long) {
        viewModelScope.launch(
            Dispatchers.IO // don't block main thread
        ) {
            // TODO: handle errors
            _playerState.value = repository.getPlayer(id)
        }
    }

    fun loadTeam(id: Long) {
        viewModelScope.launch(
            Dispatchers.IO
        ) {
            // TODO: handle errors
            _teamState.value = repository.getTeam(id)
        }
    }

    /**
     * Factory for DI
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NBAViewerApplication)
                val nbaRepository = application.dependencies.nbaRepository
                NBAViewerViewModel(repository = nbaRepository)
            }
        }
    }
}