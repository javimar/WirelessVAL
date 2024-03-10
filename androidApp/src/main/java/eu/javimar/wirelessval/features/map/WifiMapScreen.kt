package eu.javimar.wirelessval.features.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.presentation.components.MyAppBar
import eu.javimar.wirelessval.core.common.presentation.components.MyCustomSnackBar
import eu.javimar.wirelessval.core.common.presentation.components.MyProgressIndicator
import eu.javimar.wirelessval.core.util.ComposableLifecycle
import eu.javimar.wirelessval.core.util.wifiBOMock
import eu.javimar.wirelessval.features.main.presentation.BottomBar
import eu.javimar.wirelessval.features.map.components.GoogleMapClustering
import eu.javimar.wirelessval.features.wifi.presentation.listing.state.WifiState

@Composable
fun WifiMapScreen(
    state: WifiState,
    onEvent: (WifiMapEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            MyAppBar(
                title = stringResource(id = R.string.title_mapa_activity),
                showNavIcon = false
            )
        },
        bottomBar = {
            BottomBar(navController)
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
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                if(state.isLoading) {
                    MyProgressIndicator(isDisplayed = true)
                }
                GoogleMapClustering(
                    items = state.wifis,
                    onEvent = onEvent,
                    location = LatLng(state.location.latitude, state.location.longitude),
                    isNightMode = state.isNightMode
                )
            }
            ComposableLifecycle { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    onEvent(WifiMapEvent.ReadNightMode)
                }
            }
        }
    )
}

@Preview
@Composable
fun MapPreview() {
    WifiMapScreen(
        state = WifiState(
            wifis = listOf(wifiBOMock)
        ),
        onEvent = {},
        snackbarHostState = SnackbarHostState(),
        navController = rememberNavController()
    )
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, vectorResId)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            intrinsicWidth,
            intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}