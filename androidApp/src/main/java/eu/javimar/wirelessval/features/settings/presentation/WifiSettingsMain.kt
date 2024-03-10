package eu.javimar.wirelessval.features.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.javimar.wirelessval.WirelessValApp
import eu.javimar.wirelessval.core.common.presentation.viewModelFactory
import eu.javimar.wirelessval.core.util.UIEvent

@Composable
fun WifiSettingsMain(
    onPopBackStack: () -> Unit,
    viewModel: WifisSettingsViewModel = viewModel<WifisSettingsViewModel>(
        factory = viewModelFactory {
            WifisSettingsViewModel(
                sharePrefs = WirelessValApp.prefsModule.sharePrefs,
            )
        }
    )
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