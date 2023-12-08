package eu.javimar.wirelessval.features.wifi.presentation.detail

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import eu.javimar.wirelessval.features.wifi.presentation.detail.state.WifiDetailEvent
import eu.javimar.wirelessval.features.wifi.presentation.detail.state.WifiDetailState

@Composable
fun WifiDetailScreen(
    snackbarHostState: SnackbarHostState,
    state: WifiDetailState,
    isNightMode: Boolean,
    onEvent: (WifiDetailEvent) -> Unit
) {

}