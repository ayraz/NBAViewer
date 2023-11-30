package com.example.nbaviewer.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HeaderTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
            .baselineHeight(32.dp),
        style = MaterialTheme.typography.headlineSmall
    )
}