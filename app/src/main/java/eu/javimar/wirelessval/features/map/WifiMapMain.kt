package eu.javimar.wirelessval.features.map

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import eu.javimar.wirelessval.core.util.UIEvent

@Composable
fun WifiMapMain(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: WifiMapViewModel = hiltViewModel()
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