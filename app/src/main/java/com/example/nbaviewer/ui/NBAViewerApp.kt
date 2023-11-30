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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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

// Composable displaying lazily paginated list of players.
@Composable
fun PlayerListScreen(
    pagingItems: LazyPagingItems<PlayerItemUiState>,
    onPlayerClicked: (Long) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        if (pagingItems.loadState.refresh == LoadState.Loading) {
            item {
                Text(
                    text = stringResource(R.string.waiting_for_items_to_load_from_api),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        items(count = pagingItems.itemCount) { index ->
            val item = pagingItems[index]
            if (item != null) {
                PlayerCard(player = item, onPlayerClicked)
            }
        }

        if (pagingItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun PlayerCard(
    player: PlayerItemUiState,
    onPlayerClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            onPlayerClicked(player.id)
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.player_label, player.firstName, player.lastName),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(text = stringResource(R.string.position_label,
                player.position.ifBlank { stringResource(R.string.unknown) }))
            Text(text = stringResource(R.string.team_label, player.team))
        }
    }
}

@Composable
fun PlayerDetail(player: PlayerDetailUiState,
                 imageUrl: String,
                 onTeamNavigate: (Long) -> Unit,
                 modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.player_logo_image_desc),
            modifier = Modifier
        )
        Text(
            text = stringResource(R.string.player_label, player.firstName, player.lastName),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(text = stringResource(
            R.string.position_label,
            player.position.ifBlank { stringResource(R.string.unknown) }))
        Text(text = stringResource(R.string.team_label, player.team))
        Text(text = stringResource(
            R.string.height_feet_label,
            player.heightFeet.ifBlank { stringResource(R.string.unknown) }))
        Text(text = stringResource(
            R.string.height_inches_label,
            player.heightInches.ifBlank { stringResource(R.string.unknown) }))
        Text(text = stringResource(
            R.string.weight_pounds_label,
            player.weightPounds.ifBlank { stringResource(R.string.unknown) }))
        Button(
            onClick = {
                onTeamNavigate(player.teamId)
            },
            modifier = Modifier
        ) {
            Text(text = "Team Details")
        }
    }
}

@Composable
fun TeamDetail(
    team: TeamDetailUiState,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.team_logo_image_desc),
            modifier = Modifier
        )
        Text(
            text = stringResource(R.string.team_name_label, team.name),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(text = stringResource(
            R.string.abbreviation_label,
            team.abbreviation.ifBlank { stringResource(R.string.unknown) }))
        Text(text = stringResource(
            R.string.full_name_label,
            team.fullName.ifBlank { stringResource(R.string.unknown) }))
        Text(text = stringResource(
            R.string.city_label,
            team.city.ifBlank { stringResource(R.string.unknown) }))
        Text(text = stringResource(
            R.string.conference_label,
            team.conference.ifBlank { stringResource(R.string.unknown) }))
        Text(text = stringResource(
            R.string.division_label,
            team.division.ifBlank { stringResource(R.string.unknown) }))
    }
}