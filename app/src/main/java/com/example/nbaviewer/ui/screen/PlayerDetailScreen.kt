@file:OptIn(ExperimentalGlideComposeApi::class)

package com.example.nbaviewer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.nbaviewer.R
import com.example.nbaviewer.ui.state.PlayerDetailUiState

@Composable
fun PlayerDetail(
    player: PlayerDetailUiState,
    imageUrl: String,
    onTeamNavigate: (Long) -> Unit,
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
            contentDescription = stringResource(R.string.player_logo_image_desc),
            modifier = Modifier
        )
        Text(
            text = stringResource(R.string.player_label, player.firstName, player.lastName),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(
            text = stringResource(
                R.string.position_label,
                player.position.ifBlank { stringResource(R.string.unknown) })
        )
        Text(text = stringResource(R.string.team_label, player.team))
        Text(
            text = stringResource(
                R.string.height_feet_label,
                player.heightFeet.ifBlank { stringResource(R.string.unknown) })
        )
        Text(
            text = stringResource(
                R.string.height_inches_label,
                player.heightInches.ifBlank { stringResource(R.string.unknown) })
        )
        Text(
            text = stringResource(
                R.string.weight_pounds_label,
                player.weightPounds.ifBlank { stringResource(R.string.unknown) })
        )
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