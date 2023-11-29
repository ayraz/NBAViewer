package com.example.nbaviewer.data

import com.example.nbaviewer.model.Player
import com.example.nbaviewer.model.Team
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NBAApiService {

    @GET("players")
    suspend fun getPlayers(
        @Query("page") page: Int = 0,
        @Query("per_page") perPage: Int = 35, // default page size required by the task
        @Query("search") search: String? = null
    ): PagedResponse<Player>

    @GET("players/{id}")
    suspend fun getPlayer(@Path("id") id: Int): Player

    @GET("teams/{id}")
    suspend fun getTeam(@Path("id") id: Int): Team
}