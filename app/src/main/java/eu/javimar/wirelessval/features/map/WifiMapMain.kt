package eu.javimar.wirelessval.features.map

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import eu.javimar.wirelessval.WirelessValApp.Companion.prefsModule
import eu.javimar.wirelessval.WirelessValApp.Companion.useCaseModule
import eu.javimar.wirelessval.core.common.presentation.viewModelFactory
import eu.javimar.wirelessval.core.util.UIEvent

@Composable
fun WifiMapMain(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: WifiMapViewModel = viewModel<WifiMapViewModel>(
        factory = viewModelFactory {
            WifiMapViewModel(
                preferences = prefsModule.sharePrefs,
                getWifisUseCase = useCaseModule.getWifis
            )
        }
    )
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UIEvent.Navigate -> navController.navigate(event.route)
                is UIEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                else -> Unit
            }
        }
    }

    WifiMapScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        navController = navController
    )
}