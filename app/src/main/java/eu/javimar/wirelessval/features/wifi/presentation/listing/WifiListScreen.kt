package eu.javimar.wirelessval.features.wifi.presentation.listing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.distance
import eu.javimar.wirelessval.core.common.hasLocationEnabled
import eu.javimar.wirelessval.core.common.location.LocationDialog
import eu.javimar.wirelessval.core.common.openLocationSettings
import eu.javimar.wirelessval.core.common.presentation.components.ImagePainter
import eu.javimar.wirelessval.core.common.presentation.components.MyConfimationDialog
import eu.javimar.wirelessval.core.common.presentation.components.MyCustomSnackBar
import eu.javimar.wirelessval.core.common.presentation.components.MyEmptyView
import eu.javimar.wirelessval.core.common.presentation.components.SearchBar
import eu.javimar.wirelessval.core.common.presentation.components.ShimmerAnimation
import eu.javimar.wirelessval.core.common.presentation.components.ThreeDotsAnimation
import eu.javimar.wirelessval.core.util.ComposableLifecycle
import eu.javimar.wirelessval.core.util.wifiBOMock
import eu.javimar.wirelessval.features.main.presentation.BottomBar
import eu.javimar.wirelessval.features.wifi.presentation.listing.components.WifiListItem
import eu.javimar.wirelessval.features.wifi.presentation.listing.components.WifisMainMenu
import eu.javimar.wirelessval.features.wifi.presentation.listing.state.WifiListEvent
import eu.javimar.wirelessval.features.wifi.presentation.listing.state.WifiState
import eu.javimar.wirelessval.ui.theme.Negative30

@Composable
fun WifiListScreen(
    state: WifiState,
    onEvent: (WifiListEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(16.dp),
                hostState = snackbarHostState
            ) { snackbarData: SnackbarData ->
                MyCustomSnackBar(
                    message = snackbarData.visuals.message,
                    onActionClicked = { snackbarData.dismiss() }
                )
            }
        },
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
        ) {

            if(state.numWifis == 0) {
                MyEmptyView(
                    textRes = R.string.wifis_empty,
                    iconRes = R.drawable.ic_cactus,
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 70.dp, end = 14.dp)
                    .align(Alignment.TopEnd)
            ) {
                WifisMainMenu(
                    expanded = state.showMenu,
                    onSettingsClick = {
                        onEvent(WifiListEvent.OnSettingsClick)
                    },
                    onReloadClick = {
                        onEvent(WifiListEvent.AskReloadWifis)
                    },
                    onDismiss = {
                        onEvent(WifiListEvent.ToggleMenu)
                    }
                )
            }

            Column {
                SearchBar(
                    query = state.searchQuery,
                    onQuerySchange = {
                        onEvent(WifiListEvent.SearchWifis(it))
                    },
                    onMenuSelected = {
                        onEvent(WifiListEvent.ToggleMenu)
                    }
                )

                if(state.isLoading) {
                    Box(Modifier.fillMaxHeight(),
                        contentAlignment = Center) {
                        Column {
                            repeat(12) {
                                ShimmerAnimation()
                            }
                        }
                        ThreeDotsAnimation()
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                AnimatedVisibility(
                    visible = !state.isLoading,
                    enter = fadeIn(
                        animationSpec = TweenSpec(100, 100),
                        initialAlpha = .4f
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp, 0.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.wifis,
                            itemContent = { wifi ->
                                WifiListItem(
                                    wifi = wifi,
                                    onWifiClick = onEvent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    distance = state.location.distance(
                                        wifi.coordinates.latitude,
                                        wifi.coordinates.longitude,
                                        context
                                    )
                                )
                            })
                    }
                }
            }

            MyConfimationDialog(
                onDismiss = {
                    onEvent(WifiListEvent.CancelDialog)
                },
                title = R.string.dialog_refresh_title,
                body = R.string.dialog_refresh_confirmation,
                posText = R.string.dialog_refresh_button_ok,
                negText = R.string.dialog_button_cancel,
                onConfirm = {
                    onEvent(WifiListEvent.ReloadWifis)
                },
                icon = {
                    ImagePainter(
                        modifier = Modifier.size(72.dp),
                        painter = R.drawable.ic_alert,
                        color = Negative30
                    )
                },
                showDialog = state.showDialog
            )

            LocationDialog(
                title = R.string.dialog_access_location_title,
                body = R.string.permission_gps,
                posText = R.string.dialog_access_location,
                negText = R.string.dialog_button_cancel,
                icon = R.drawable.ic_location,
                onConfirm = {
                    context.openLocationSettings()
                    onEvent(WifiListEvent.CancelConnectionDialog)
                },
                onDismiss = {
                    onEvent(WifiListEvent.CancelConnectionDialog)
                },
                showDialog = !context.hasLocationEnabled() && state.showConnectionDialog
            )
        }

        ComposableLifecycle { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onEvent(WifiListEvent.RefreshWifiOnScreen)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WifisListPreview() {
    WifiListScreen(
        state = WifiState(
            wifis = listOf(wifiBOMock)
        ),
        onEvent = {},
        snackbarHostState = remember { SnackbarHostState() },
        navController = rememberNavController(),
    )
}
