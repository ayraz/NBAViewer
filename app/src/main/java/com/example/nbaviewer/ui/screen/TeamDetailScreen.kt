@file:OptIn(ExperimentalGlideComposeApi::class)

package com.example.nbaviewer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.nbaviewer.ui.state.TeamDetailUiState

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
        Text(
            text = stringResource(
                R.string.abbreviation_label,
                team.abbreviation.ifBlank { stringResource(R.string.unknown) })
        )
        Text(
            text = stringResource(
                R.string.full_name_label,
                team.fullName.ifBlank { stringResource(R.string.unknown) })
        )
        Text(
            text = stringResource(
                R.string.city_label,
                team.city.ifBlank { stringResource(R.string.unknown) })
        )
        Text(
            text = stringResource(
                R.string.conference_label,
                team.conference.ifBlank { stringResource(R.string.unknown) })
        )
        Text(
            text = stringResource(
                R.string.division_label,
                team.division.ifBlank { stringResource(R.string.unknown) })
        )
    }
}