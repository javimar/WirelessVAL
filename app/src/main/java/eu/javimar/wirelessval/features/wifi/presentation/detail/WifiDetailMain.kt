package eu.javimar.wirelessval.features.wifi.presentation.detail

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.javimar.wirelessval.WirelessValApp.Companion.useCaseModule
import eu.javimar.wirelessval.core.common.presentation.viewModelFactoryExtras
import eu.javimar.wirelessval.core.util.UIEvent

@Composable
fun WifiDetailMain(
    onPopBackStack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: WifiDetailViewModel = viewModel<WifiDetailViewModel>(
        factory = viewModelFactoryExtras {
            WifiDetailViewModel(
                savedStateHandle = it,
                updateWifiUseCase = useCaseModule.updateWifis,
            )
        }
    )
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.PopBackStack -> onPopBackStack()
                is UIEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                else -> Unit
            }
        }
    }

    WifiDetailScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
    )
}