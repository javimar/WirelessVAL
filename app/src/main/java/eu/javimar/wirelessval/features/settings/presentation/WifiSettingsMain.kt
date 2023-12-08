package eu.javimar.wirelessval.features.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import eu.javimar.wirelessval.core.util.UIEvent

@Composable
fun WifiSettingsMain(
    onPopBackStack: () -> Unit,
    viewModel: WifisSettingsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UIEvent.PopBackStack -> onPopBackStack()
                else -> Unit
            }
        }
    }

    WifisSettingsScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent
    )
}