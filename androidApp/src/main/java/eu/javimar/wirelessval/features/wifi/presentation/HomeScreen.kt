package eu.javimar.wirelessval.features.wifi.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import eu.javimar.wirelessval.core.nav.HomeNavGraph

@Composable
fun HomeScreen(
    // All Navgraphs with a single Navhost must have their own NavHostController
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState,
) {
    HomeNavGraph(
        navController = navController,
        snackbarHostState = snackbarHostState,
    )
}