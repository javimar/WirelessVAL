package eu.javimar.wirelessval.features.wifi.presentation.listing.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.core.common.presentation.components.SetCircle
import eu.javimar.wirelessval.core.util.wifiBOMock
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.presentation.listing.state.WifiListEvent

@Composable
fun WifiListItem(
    wifi: WifiBO,
    onWifiClick: (WifiListEvent) -> Unit,
    modifier: Modifier = Modifier,
    distance: String
) {
    Card(
        shape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        Row(
            modifier = modifier
                .clickable {
                    onWifiClick(WifiListEvent.OnWifiClick(wifi))
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            SetCircle(
                opinion = wifi.opinion,
                size = 36.dp,
                font = 16.dp,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = wifi.wifiName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = distance,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WifiListItemPreview() {
    WifiListItem(
        wifi = wifiBOMock,
        onWifiClick = {},
        distance = "20 metros"
    )
}