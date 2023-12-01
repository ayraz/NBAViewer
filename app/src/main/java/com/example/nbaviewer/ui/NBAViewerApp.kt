@file:OptIn(
    ExperimentalMaterial3Api::class,
)

package com.example.nbaviewer.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.nbaviewer.R
import com.example.nbaviewer.ui.screen.PlayerDetail
import com.example.nbaviewer.ui.screen.PlayerListScreen
import com.example.nbaviewer.ui.screen.TeamDetail

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
                val player = nbaViewModel.playerDetailState.collectAsState().value
                when {
                    player.isError -> {
                        showErrorToast(LocalContext.current)
                    }

                    player.state != null -> {
                        PlayerDetail(
                            player = player.state,
                            imageUrl = nbaViewModel.getPlayerDetailImageUrl(),
                            onTeamNavigate = { id ->
                                nbaViewModel.loadTeam(id)
                                scrollBehavior.expandTopAppBar()
                                navController.navigate(NBAScreen.TeamDetail.name)
                            }
                        )
                    }
                }
            }
            composable(route = NBAScreen.TeamDetail.name) {
                val team = nbaViewModel.teamDetailState.collectAsState().value
                when {
                    team.isError -> {
                        showErrorToast(LocalContext.current)
                    }

                    team.state != null -> {
                        TeamDetail(
                            team = team.state,
                            imageUrl = nbaViewModel.getTeamDetailImageUrl()
                        )
                    }
                }
            }
        }
    }
}

fun showErrorToast(context: Context) {
    Toast.makeText(
        context,
        context.getString(R.string.error_has_occurred_while_loading_data),
        Toast.LENGTH_SHORT
    ).show();
}

fun TopAppBarScrollBehavior.expandTopAppBar() {
    this.state.heightOffset = 0f // expand top bar by resetting its height offset
}