package com.example.nbaviewer

import androidx.paging.PagingData
import com.example.nbaviewer.data.NBARepository
import com.example.nbaviewer.model.Player
import com.example.nbaviewer.model.Team
import com.example.nbaviewer.ui.NBAViewerViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.stub

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class NBAViewerViewModelTest {

    private val testPlayerId = 1L
    private val testTeamId = 1L

    @Mock
    private lateinit var repository: NBARepository
    private lateinit var testCoroutineDispatcher: CoroutineDispatcher
    private lateinit var viewModel: NBAViewerViewModel

    @Before
    fun init() {
        testCoroutineDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testCoroutineDispatcher)
        viewModel = NBAViewerViewModel(repository, testCoroutineDispatcher)
    }

    @Test
    fun testLoadNBAPlayers_LoadSuccessful() {
        repository.stub {
            on { getPagedPlayersFlow() } doReturn flow {
                emit(PagingData.from(mutableListOf<Player>().apply {
                    repeat(3) { this.add(Player(id = it.toLong())) }
                }))
            }
        }

        CoroutineScope(testCoroutineDispatcher).launch {
            val list = viewModel.getNBAPlayers()
                .map { it.toList() }
                .single()

            assertEquals(0, list[0].id)
            assertEquals(1, list[1].id)
            assertEquals(2, list[2].id)
        }
    }

    @Test
    fun testLoadPlayer_LoadCorrectId() {
        repository.stub {
            onBlocking { getPlayer(any()) } doReturn Player(id = testPlayerId)
        }
        viewModel.loadPlayer(testPlayerId)

        assertEquals(testPlayerId, viewModel.playerDetailState.value.state?.id)
    }

    @Test
    fun testLoadPlayer_LoadIncorrectId() {
        repository.stub {
            onBlocking { getPlayer(any()) } doThrow IllegalArgumentException()
        }
        viewModel.loadPlayer(-1)

        assertTrue(viewModel.playerDetailState.value.isError)
    }

    @Test
    fun testLoadTeam_LoadCorrectId() {
        repository.stub {
            onBlocking { getTeam(any()) } doReturn Team(id = testTeamId)
        }
        viewModel.loadTeam(testTeamId)

        assertEquals(testTeamId, viewModel.teamDetailState.value.state?.id)
    }

    @Test
    fun testLoadTeam_LoadIncorrectId() {
        repository.stub {
            onBlocking { getTeam(any()) } doThrow IllegalArgumentException()
        }
        viewModel.loadTeam(-1)

        assertTrue(viewModel.playerDetailState.value.isError)
    }
}