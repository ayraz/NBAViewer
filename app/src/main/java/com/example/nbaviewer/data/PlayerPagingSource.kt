package com.example.nbaviewer.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nbaviewer.model.Player
import retrofit2.HttpException
import java.io.IOException

class PlayerPagingSource(private val apiService: NBAApiService) : PagingSource<Int, Player>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Player> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getPlayers(page)
            val players = response.data
            LoadResult.Page(
                data = players,
                prevKey = null, // Only paging forward.
                nextKey = response.meta.next_page
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Player>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}