@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nbaviewer.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.nbaviewer.R
import com.example.nbaviewer.ui.component.HeaderTitle
import com.example.nbaviewer.ui.component.LabeledField
import com.example.nbaviewer.ui.showErrorToast
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
        if (pagingItems.loadState.append is LoadState.Error
            || pagingItems.loadState.refresh is LoadState.Error
        ) {
            item {
                showErrorToast(LocalContext.current)
            }
        }

        if (pagingItems.loadState.append == LoadState.Loading
            || pagingItems.loadState.refresh == LoadState.Loading
        ) {
            item {
                CircularProgressIndicator(
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.image_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(0.5F)
            )
            Column(
                modifier = Modifier
                    .weight(1.5F)
                    .padding(8.dp)
            ) {
                HeaderTitle(title = "${player.firstName} ${player.lastName}")

                LabeledField(stringResource(R.string.position_label),
                    player.position.ifBlank { stringResource(R.string.unknown) })

                LabeledField(stringResource(R.string.team_label), player.team)
            }
        }
    }
}