@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nbaviewer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.nbaviewer.R
import com.example.nbaviewer.ui.state.PlayerItemUiState

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
    player: PlayerItemUiState, onPlayerClicked: (Long) -> Unit, modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            onPlayerClicked(player.id)
        }, elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ), modifier = Modifier
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
            Text(
                text = stringResource(R.string.position_label,
                    player.position.ifBlank { stringResource(R.string.unknown) })
            )
            Text(text = stringResource(R.string.team_label, player.team))
        }
    }
}