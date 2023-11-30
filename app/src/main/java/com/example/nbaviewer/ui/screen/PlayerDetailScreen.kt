@file:OptIn(ExperimentalGlideComposeApi::class)

package com.example.nbaviewer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.nbaviewer.R
import com.example.nbaviewer.ui.component.HeaderImage
import com.example.nbaviewer.ui.component.HeaderTitle
import com.example.nbaviewer.ui.component.LabeledField
import com.example.nbaviewer.ui.component.baselineHeight
import com.example.nbaviewer.ui.state.PlayerDetailUiState

@Composable
fun PlayerDetail(
    player: PlayerDetailUiState,
    imageUrl: String,
    onTeamNavigate: (Long) -> Unit,
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
                horizontalAlignment = CenterHorizontally,
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

                HeaderTitle(title = "${player.firstName} ${player.lastName}")

                LabeledField(stringResource(R.string.position_label), player.position)

                LabeledField(stringResource(R.string.height_feet_label),
                    player.heightFeet.ifBlank { stringResource(R.string.unknown) })

                LabeledField(stringResource(R.string.height_inches_label),
                    player.heightInches.ifBlank { stringResource(R.string.unknown) })

                LabeledField(stringResource(R.string.weight_pounds_label),
                    player.weightPounds.ifBlank { stringResource(R.string.unknown) })

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onTeamNavigate(player.teamId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .align(CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.team_details_btn_text))
                }
            }
        }
    }
}