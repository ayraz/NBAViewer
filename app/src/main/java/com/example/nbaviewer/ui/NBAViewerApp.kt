@file:OptIn(
    ExperimentalGlideComposeApi::class,
    ExperimentalMaterial3Api::class,
)

package com.example.nbaviewer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.nbaviewer.R
import com.example.nbaviewer.ui.screen.PlayerDetail
import com.example.nbaviewer.ui.screen.PlayerListScreen
import com.example.nbaviewer.ui.screen.TeamDetail
import com.example.nbaviewer.ui.state.PlayerDetailUiState
import com.example.nbaviewer.ui.state.PlayerItemUiState
import com.example.nbaviewer.ui.state.TeamDetailUiState

/**
 * enum values that represent the screens in the app
 */
enum class NBAScreen(@StringRes val title: Int) {
    PlayerList(title = R.string.nba_players_screen_title),
    PlayerDetail(title = R.string.player_details_screen_title),
    TeamDetail(title = R.string.team_details_screen_title)
}

/**
 * Composable that displays the topBar and displays back button for back navigation.
 */
@Composable
fun NBAViewerAppBar(
    currentScreen: NBAScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_desc)
                    )
                }
            }
        }
    )
}

@Composable
fun NBAViewerApp() {
    val navController: NavHostController = rememberNavController()
    // Inject viewModel with dependencies
    val nbaViewModel: NBAViewerViewModel = viewModel(factory = NBAViewerViewModel.Factory)

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = NBAScreen.valueOf(
        backStackEntry?.destination?.route ?: NBAScreen.PlayerList.name
    )
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            NBAViewerAppBar(
                scrollBehavior = scrollBehavior,
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NBAScreen.PlayerList.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NBAScreen.PlayerList.name) {
                PlayerListScreen(
                    pagingItems = nbaViewModel.getNBAPlayers().collectAsLazyPagingItems(),
                    onPlayerClicked = { id ->
                        nbaViewModel.loadPlayer(id)
                        scrollBehavior.expandTopAppBar()
                        navController.navigate(NBAScreen.PlayerDetail.name)
                    },
                    modifier = Modifier.fillMaxSize()

                )
            }
            composable(route = NBAScreen.PlayerDetail.name) {
                PlayerDetail(
                    player = nbaViewModel.playerDetailState.collectAsState().value,
                    imageUrl = nbaViewModel.getPlayerDetailImageUrl(),
                    onTeamNavigate = { id ->
                        nbaViewModel.loadTeam(id)
                        scrollBehavior.expandTopAppBar()
                        navController.navigate(NBAScreen.TeamDetail.name)
                    }
                )
            }
            composable(route = NBAScreen.TeamDetail.name) {
                TeamDetail(
                    team = nbaViewModel.teamDetailState.collectAsState().value,
                    imageUrl = nbaViewModel.getTeamDetailImageUrl()
                )
            }
        }
    }
}

fun TopAppBarScrollBehavior.expandTopAppBar() {
    this.state.heightOffset = 0f // expand top bar by resetting its height offset
}