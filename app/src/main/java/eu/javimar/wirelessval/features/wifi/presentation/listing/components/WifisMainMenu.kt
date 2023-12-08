package eu.javimar.wirelessval.features.wifi.presentation.listing.components

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import eu.javimar.wirelessval.R

@Composable
fun WifisMainMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onSettingsClick: () -> Unit,
    onReloadClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.surface
        ),
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.title_settings))
            },
            onClick = onSettingsClick,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "settings",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.menu_load))
            },
            onClick = onReloadClick,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_load),
                    contentDescription = "reload",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FallasMenuPreview() {
    WifisMainMenu(
        expanded = true,
        onSettingsClick = {},
        onReloadClick = {},
        onDismiss = {}
    )
}