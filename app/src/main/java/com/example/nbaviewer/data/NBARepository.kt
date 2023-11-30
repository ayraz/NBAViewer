package com.example.nbaviewer.data

import androidx.paging.PagingData
import com.example.nbaviewer.model.Player
import com.example.nbaviewer.model.Team
import kotlinx.coroutines.flow.Flow

interface NBARepository {

    fun getPagedPlayersFlow(): Flow<PagingData<Player>>

    suspend fun getPlayer(id: Long): Player

    suspend fun getTeam(id: Long): Team

    fun getPlayerDetailImageUrl(): String

    fun getTeamDetailImageUrl(): String
}