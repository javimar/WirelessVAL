package eu.javimar.wirelessval.core.nav.destinations

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import eu.javimar.wirelessval.core.nav.screens.Screens
import eu.javimar.wirelessval.core.nav.screens.WifiNavArgs
import eu.javimar.wirelessval.core.nav.serializableTypeOf
import eu.javimar.wirelessval.features.about.presentation.WifiAboutMain
import eu.javimar.wirelessval.features.map.WifiMapMain
import eu.javimar.wirelessval.features.settings.presentation.WifiSettingsMain
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.presentation.detail.WifiDetailMain
import eu.javimar.wirelessval.features.wifi.presentation.listing.WifiListMain

fun NavGraphBuilder.wifiListingDestination(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    composable(
        route = Screens.Home.route
    ) {
        WifiListMain(
            navController = navController,
            snackbarHostState = snackbarHostState
        )
    }
}

fun NavGraphBuilder.mapDestination(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    composable(
        route = Screens.Map.route
    ) {
        WifiMapMain(
            navController = navController,
            snackbarHostState = snackbarHostState,
        )
    }
}

fun NavGraphBuilder.aboutDestination(
    navController: NavHostController,
) {
    composable(
        route = Screens.About.route
    ) {
        WifiAboutMain(
            navController = navController,
        )
    }
}

fun NavGraphBuilder.wifiDetailDestination(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    composable(
        route = Screens.Detail.route,
        arguments = listOf(
            navArgument(name = WifiNavArgs.Wifi.key) {
                type = NavType.serializableTypeOf<WifiBO>()
            }
        )
    ) {
        WifiDetailMain(
            snackbarHostState = snackbarHostState,
            onPopBackStack = {
                navController.popBackStack()
            }
        )
    }
}

fun NavGraphBuilder.settingsDestination(
    navController: NavController,
) {
    composable(
        route = Screens.Settings.route
    ) {
        WifiSettingsMain(
            onPopBackStack = {
                navController.popBackStack()
            }
        )
    }
}