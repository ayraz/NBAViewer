package com.example.nbaviewer.ui

import android.util.Log
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
import com.example.nbaviewer.ui.state.PlayerDetailUiState
import com.example.nbaviewer.ui.state.PlayerItemUiState
import com.example.nbaviewer.ui.state.TeamDetailUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Basic UI state wrapper for handling error states
 */
data class StateWrapper<out T>(
    val state: T? = null,
    val isError: Boolean = false)
class NBAViewerViewModel(
    private val repository: NBARepository,
    private val dispatcher: CoroutineDispatcher) : ViewModel() {

    /**
     * Player detail state with last selected player
     */
    private val _playerState = MutableStateFlow(StateWrapper<PlayerDetailUiState>())
    val playerDetailState: StateFlow<StateWrapper<PlayerDetailUiState>> = _playerState.asStateFlow()

    /**
     * Team detail state with last selected team
     */
    private val _teamState = MutableStateFlow(StateWrapper<TeamDetailUiState>())
    val teamDetailState: StateFlow<StateWrapper<TeamDetailUiState>> = _teamState.asStateFlow()

    /**
     * Get players on init so we can display status/list immediately.
     */
    init {
        getNBAPlayers()
    }

    fun getNBAPlayers(): Flow<PagingData<PlayerItemUiState>> {
        return repository.getPagedPlayersFlow()
            .map {
                it.map { player -> PlayerItemUiState(player)}
            }
            .flowOn(dispatcher) // don't block main thread
            .cachedIn(viewModelScope)
    }

    fun loadPlayer(id: Long) {
        // don't block main thread
        viewModelScope.launch(dispatcher) {
            try {
                _playerState.value = StateWrapper(repository.getPlayer(id).let {
                    PlayerDetailUiState(it)
                })
            } catch (e: Exception) {
                _playerState.value = StateWrapper(isError = true);
            }
        }
    }

    fun loadTeam(id: Long) {
        viewModelScope.launch(dispatcher) {
            try {
                _teamState.value = StateWrapper(repository.getTeam(id).let {
                    TeamDetailUiState(it)
                })
            } catch (e: Exception) {
                _teamState.value = StateWrapper(isError = true);
            }
        }
    }

    fun getPlayerDetailImageUrl(): String {
        return repository.getPlayerDetailImageUrl()
    }

    fun getTeamDetailImageUrl(): String {
        return repository.getTeamDetailImageUrl()
    }

    /**
     * Factory for DI
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NBAViewerApplication)
                val nbaRepository = application.dependencies.nbaRepository
                NBAViewerViewModel(
                    repository = nbaRepository,
                    dispatcher = Dispatchers.IO
                )
            }
        }
    }
}