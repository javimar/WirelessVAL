package eu.javimar.wirelessval.features.wifi.presentation.detail

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import eu.javimar.wirelessval.core.util.UIEvent

@Composable
fun WifiDetailMain(
    onPopBackStack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: WifiDetailViewModel = hiltViewModel()
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
        isNightMode = viewModel.isNightMode,
        snackbarHostState = snackbarHostState,
    )
}