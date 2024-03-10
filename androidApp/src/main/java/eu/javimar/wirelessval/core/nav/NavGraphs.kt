package eu.javimar.wirelessval.core.nav

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.javimar.wirelessval.core.nav.destinations.aboutDestination
import eu.javimar.wirelessval.core.nav.destinations.mapDestination
import eu.javimar.wirelessval.core.nav.destinations.settingsDestination
import eu.javimar.wirelessval.core.nav.destinations.wifiDetailDestination
import eu.javimar.wirelessval.core.nav.destinations.wifiListingDestination
import eu.javimar.wirelessval.core.nav.screens.Screens
import eu.javimar.wirelessval.features.wifi.presentation.HomeScreen

@Composable
fun RootNavGraph(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    NavHost(
        navController = navHostController,
        route = Graph.ROOT,
        startDestination = Graph.HOME
    ) {
        composable(
            route = Graph.HOME
        ) {
            HomeScreen(
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = Screens.Home.route,
    ) {
        wifiListingDestination(navController, snackbarHostState)
        wifiDetailDestination(navController, snackbarHostState)
        mapDestination(navController, snackbarHostState)
        aboutDestination(navController)
        settingsDestination(navController)
    }
}