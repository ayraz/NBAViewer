package com.example.nbaviewer.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LabeledField(label: String, value: String) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()
        Text(
            text = label,
            modifier = Modifier.baselineHeight(24.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            modifier = Modifier.baselineHeight(24.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Applied to a Text, it sets the distance between the top and the first baseline. It
 * also makes the bottom of the element coincide with the last baseline of the text.
 *
 *     _______________
 *     |             |   ↑
 *     |             |   |  heightFromBaseline
 *     |Hello, World!|   ↓
 *     ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 *
 * This modifier can be used to distribute multiple text elements using a certain distance between
 * baselines.
 *
 * @see <a href="https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/components/BaseLineHeightModifier.kt">Source link</a>
 */
data class BaselineHeightModifier(
    val heightFromBaseline: Dp
) : LayoutModifier {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {

        val textPlaceable = measurable.measure(constraints)
        val firstBaseline = textPlaceable[FirstBaseline]
        val lastBaseline = textPlaceable[LastBaseline]

        val height = heightFromBaseline.roundToPx() + lastBaseline - firstBaseline
        return layout(constraints.maxWidth, height) {
            val topY = heightFromBaseline.roundToPx() - firstBaseline
            textPlaceable.place(0, topY)
        }
    }
}

fun Modifier.baselineHeight(heightFromBaseline: Dp): Modifier =
    this.then(BaselineHeightModifier(heightFromBaseline))