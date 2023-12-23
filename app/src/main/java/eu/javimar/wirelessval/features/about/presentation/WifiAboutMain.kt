package eu.javimar.wirelessval.features.about.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import eu.javimar.wirelessval.WirelessValApp.Companion.prefsModule
import eu.javimar.wirelessval.core.common.presentation.viewModelFactory

@Composable
fun WifiAboutMain(
    navController: NavHostController,
    viewModel: WifiAboutViewModel = viewModel<WifiAboutViewModel>(
        factory = viewModelFactory {
            WifiAboutViewModel(prefsModule.sharePrefs)
        }
    )
) {
    WifiAboutScreen(
        navController = navController,
        dateState = viewModel.dateState
    )
}