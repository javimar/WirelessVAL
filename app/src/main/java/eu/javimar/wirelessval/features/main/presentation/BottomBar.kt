package eu.javimar.wirelessval.features.main.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import eu.javimar.wirelessval.core.common.presentation.components.ImagePainter
import eu.javimar.wirelessval.core.nav.screens.Screens

@Composable
fun BottomBar(
    navController: NavHostController,
) {
    val navRoutes = listOf(
        Screens.Home,
        Screens.Map,
        Screens.About
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomNavbarDestinations = navRoutes.any { it.route == currentDestination?.route }

    if(bottomNavbarDestinations) {
        NavigationBar(
            tonalElevation = 5.dp,
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            navRoutes.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                NavigationBarItem(
                    icon = {
                        if(selected)
                            ImagePainter(painter = screen.icon, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        else
                            ImagePainter(painter = screen.icon, color = MaterialTheme.colorScheme.primaryContainer)
                    },
                    label = {
                        Text(
                            text = stringResource(screen.resourceId),
                            color = if(selected) MaterialTheme.colorScheme.secondaryContainer else
                                MaterialTheme.colorScheme.primaryContainer
                        )
                    },
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}