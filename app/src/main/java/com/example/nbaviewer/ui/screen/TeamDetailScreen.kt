@file:OptIn(ExperimentalGlideComposeApi::class)

package com.example.nbaviewer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.nbaviewer.R
import com.example.nbaviewer.ui.component.HeaderImage
import com.example.nbaviewer.ui.component.HeaderTitle
import com.example.nbaviewer.ui.component.LabeledField
import com.example.nbaviewer.ui.component.baselineHeight
import com.example.nbaviewer.ui.state.TeamDetailUiState

@Composable
fun TeamDetail(
    team: TeamDetailUiState,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Surface {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                HeaderImage(
                    imageUrl = imageUrl,
                    containerMaxHeight = this@BoxWithConstraints.maxHeight
                )

                Spacer(modifier = Modifier.height(8.dp))

                HeaderTitle(title = team.name)

                LabeledField(stringResource(R.string.abbreviation_label),
                    team.abbreviation.ifBlank { stringResource(R.string.unknown) })

                LabeledField(stringResource(R.string.full_name_label),
                    team.fullName.ifBlank { stringResource(R.string.unknown) })

                LabeledField(stringResource(R.string.city_label),
                    team.city.ifBlank { stringResource(R.string.unknown) })

                LabeledField(stringResource(R.string.conference_label),
                    team.conference.ifBlank { stringResource(R.string.unknown) })

                LabeledField(stringResource(R.string.division_label),
                    team.division.ifBlank { stringResource(R.string.unknown) })

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}