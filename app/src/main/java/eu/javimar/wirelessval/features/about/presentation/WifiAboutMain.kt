package eu.javimar.wirelessval.features.about.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun WifiAboutMain(
    navController: NavHostController,
    viewModel: WifiAboutViewModel = hiltViewModel()
) {
    WifiAboutScreen(
        navController = navController,
        dateState = viewModel.dateState
    )
}