@file:OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)

package com.example.nbaviewer.ui

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
import com.example.nbaviewer.model.Player
import com.example.nbaviewer.model.Team

// Static URLs for exemplar use of Glide
const val TEAM_DETAIL_IMAGE_URL = "https://img.freepik.com/premium-vector/basketball-team-logo_147887-185.jpg"
const val PLAYER_DETAIL_IMAGE_URL = "https://imageio.forbes.com/specials-images/imageserve/5e42ca594637aa0007e87b31/hero-nba-teams-lebron-harden-curry-leonard-kyrie-Anton-Klusener/0x0.jpg"

/**
 * enum values that represent the screens in the app
 */
enum class NBAScreen(val title: String) {
    PlayerList(title = "NBA Players"),
    PlayerDetail(title = "Player Details"),
    TeamDetail(title = "Team Details")
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
        title = { Text(currentScreen.title) },
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
                        contentDescription = "Back Button"
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
                    onTeamNavigate = { id ->
                        nbaViewModel.loadTeam(id)
                        navController.navigate(NBAScreen.TeamDetail.name)
                    }
                )
            }
            composable(route = NBAScreen.TeamDetail.name) {
                TeamDetail(
                    nbaViewModel.teamDetailState.collectAsState().value
                )
            }
        }
    }
}

// Composable displaying lazily paginated list of players.
@Composable
fun PlayerListScreen(
    pagingItems: LazyPagingItems<PlayerUiState>,
    onPlayerClicked: (Long) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        if (pagingItems.loadState.refresh == LoadState.Loading) {
            item {
                Text(
                    text = "Waiting for items to load from API",
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
    player: PlayerUiState,
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
                text = "Player: ${player.firstName} ${player.lastName}",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(text = "Position: ${player.position.ifBlank { "Unknown" }}")
            Text(text = "Team: ${player.team}")
        }
    }
}

@Composable
fun PlayerDetail(player: Player,
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
            model = PLAYER_DETAIL_IMAGE_URL,
            contentDescription = "Player Logo Image",
            modifier = Modifier
        )
        Text(
            text = "Player: ${player.firstName} ${player.lastName}",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(text = "Position: ${player.position.ifBlank { "Unknown" }}")
        Text(text = "Team: ${player.team.name}")
        Text(text = "Height Feet: ${player.heightFeet ?: "Unknown"}")
        Text(text = "Height Inches: ${player.heightInches ?: "Unknown"}")
        Text(text = "Weight Pounds: ${player.weightPounds ?: "Unknown"}")
        Button(
            onClick = {
                onTeamNavigate(player.team.id)
            },
            modifier = Modifier
        ) {
            Text(text = "Team Details")
        }
    }
}

@Composable
fun TeamDetail(team: Team,
               modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        GlideImage(
            model = TEAM_DETAIL_IMAGE_URL,
            contentDescription = "Team Logo Image",
            modifier = Modifier
        )
        Text(
            text = "Team Name: ${team.name}",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(text = "Abbreviation: ${team.abbreviation.ifBlank { "Unknown" }}")
        Text(text = "Full Name: ${team.fullName.ifBlank { "Unknown" }}")
        Text(text = "City: ${team.city.ifBlank { "Unknown" }}")
        Text(text = "Conference: ${team.conference.ifBlank { "Unknown" }}")
        Text(text = "Division: ${team.division.ifBlank { "Unknown" }}")
    }
}