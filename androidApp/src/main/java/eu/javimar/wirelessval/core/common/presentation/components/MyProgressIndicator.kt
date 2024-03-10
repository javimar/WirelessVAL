package eu.javimar.wirelessval.core.common.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun MyProgressIndicator(
    modifier: Modifier = Modifier,
    isDisplayed: Boolean
) {
    if(isDisplayed) {
        CircularProgressIndicator(
            modifier = modifier
                .size(96.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            trackColor = MaterialTheme.colorScheme.primary,
            strokeWidth = 12.dp,
            strokeCap = StrokeCap.Round
        )
    }
}