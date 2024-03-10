package eu.javimar.wirelessval.features.wifi.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.presentation.components.MyAppBar
import eu.javimar.wirelessval.core.common.presentation.components.MyCustomSnackBar
import eu.javimar.wirelessval.core.util.wifiBOMock
import eu.javimar.wirelessval.features.wifi.presentation.detail.components.DetailWifiMap
import eu.javimar.wirelessval.features.wifi.presentation.detail.components.MyTextField
import eu.javimar.wirelessval.features.wifi.presentation.detail.components.RatingBar
import eu.javimar.wirelessval.features.wifi.presentation.detail.state.WifiDetailEvent
import eu.javimar.wirelessval.features.wifi.presentation.detail.state.WifiDetailState

@Composable
fun WifiDetailScreen(
    snackbarHostState: SnackbarHostState,
    state: WifiDetailState,
    onEvent: (WifiDetailEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            state.wifi?.wifiName?.let {
                MyAppBar(
                    title = it,
                    style = MaterialTheme.typography.titleSmall,
                    onNavClick = {
                        onEvent(WifiDetailEvent.OnBackClick)
                    }
                )
            }
        },floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(WifiDetailEvent.UpdateWifi)
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null
                )
            }
        },

        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(16.dp),
                hostState = snackbarHostState
            ) { snackbarData: SnackbarData ->
                MyCustomSnackBar(
                    snackbarData.visuals.message,
                    onActionClicked = { snackbarData.dismiss() }
                )
            }
        },
        content = { innerPadding ->
            state.wifi?.let { wifiItem ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(24.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    MyTextField(
                        text = state.comments,
                        label = stringResource(id = R.string.detail_comments),
                        onChange = {
                            onEvent(WifiDetailEvent.CommentsChange(it))
                        },
                        keyBoardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                                focusManager.clearFocus()
                            }
                        ),
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(WifiDetailEvent.StarClicked)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        RatingBar(
                            rating = state.starValue
                        )
                    }

                    if(state.wifi.coordinates.longitude != 0.0) {
                        DetailWifiMap(
                            wifiItem.coordinates.latitude,
                            wifiItem.coordinates.longitude,
                            wifiItem.wifiName
                        )
                    }
                    Spacer(modifier = Modifier.padding(24.dp))
                }
            }
        }
    )
}

@Preview
@Composable
fun WifiDetailPreview() {
    WifiDetailScreen(
        state = WifiDetailState(wifi = wifiBOMock),
        onEvent = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}