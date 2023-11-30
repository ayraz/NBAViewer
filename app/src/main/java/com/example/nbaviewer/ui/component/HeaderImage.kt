@file:OptIn(ExperimentalGlideComposeApi::class)

package com.example.nbaviewer.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.nbaviewer.R

@Composable
fun HeaderImage(imageUrl: String, containerMaxHeight: Dp) {
    GlideImage(
        model = imageUrl,
        contentDescription = stringResource(R.string.player_logo_image_desc),
        modifier = Modifier
            .heightIn(max = containerMaxHeight / 2)
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp
            )
            .clip(RoundedCornerShape(16.dp)),
    )
}