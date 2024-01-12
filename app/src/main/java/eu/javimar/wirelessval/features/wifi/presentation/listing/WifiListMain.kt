package eu.javimar.wirelessval.features.wifi.presentation.listing

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
fun WifiListMain(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: WifiListViewModel = viewModel<WifiListViewModel>(
        factory = viewModelFactory {
            WifiListViewModel(
                getWifisUseCase = useCaseModule.getWifis,
                reloadWifisUseCase = useCaseModule.reloadWifis,
                searchWifisUseCase = useCaseModule.searchWifis,
                sharePrefs = prefsModule.sharePrefs
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

    WifiListScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        navController = navController
    )
}