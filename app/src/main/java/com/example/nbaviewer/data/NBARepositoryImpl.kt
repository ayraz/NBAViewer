package com.example.nbaviewer.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.nbaviewer.model.Player
import com.example.nbaviewer.model.Team
import kotlinx.coroutines.flow.Flow

class NBARepositoryImpl(private val apiService: NBAApiService) : NBARepository {

    // Create a paged flow of players
    override fun getPagedPlayersFlow(): Flow<PagingData<Player>> {
        return Pager(
            config = PagingConfig(
                pageSize = 35,
                enablePlaceholders = true
            )
        ) {
            PlayerPagingSource(apiService)
        }.flow
    }

    override suspend fun getPlayer(id: Long): Player {
        return apiService.getPlayer(id.toInt())
    }

    override suspend fun getTeam(id: Long): Team {
        return apiService.getTeam(id.toInt())
    }
}
