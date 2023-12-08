package eu.javimar.wirelessval.core.common.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.R

@Composable

fun MyEmptyView(
    @StringRes textRes: Int,
    @DrawableRes iconRes: Int,
    iconTint: Color = Color(0xFF67A024),
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = textRes),
                style = MaterialTheme.typography.titleMedium,
                textAlign = Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                modifier = Modifier.size(96.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyEmptyViewPreview() {
    MyEmptyView(
        textRes = R.string.wifis_empty,
        iconRes = R.drawable.ic_cactus,
        iconTint = Color(0xFF67A024)
    )
}