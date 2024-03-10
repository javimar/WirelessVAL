package eu.javimar.wirelessval.features.map.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.hasLocationPermission
import eu.javimar.wirelessval.features.map.WifiMapEvent
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun GoogleMapClustering(
    items: List<WifiBO>,
    onEvent: (WifiMapEvent) -> Unit,
    location: LatLng,
    isNightMode: Boolean,
) {
    val context = LocalContext.current
    val valencia = LatLng(39.4697500, -0.3773900)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(valencia, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            compassEnabled = true,
            zoomControlsEnabled = false,
            myLocationButtonEnabled = context.hasLocationPermission()
        ),
        properties = MapProperties(
            isBuildingEnabled = true,
            isTrafficEnabled = false,
            isMyLocationEnabled = context.hasLocationPermission(),
            mapStyleOptions = if(isNightMode) MapStyleOptions.loadRawResourceStyle(
                context, R.raw.map_style) else null
        )
    ) {
        var clusterManager by remember { mutableStateOf<ClusterManager<WifiBO>?>(null) }

        MapEffect(items) { map ->
            clusterManager = ClusterManager<WifiBO>(context, map)
            clusterManager?.addItems(items)
            clusterManager?.renderer = MarkerClusterRender(context, map, clusterManager!!) {
                onEvent(WifiMapEvent.OnWifiClick(it))
            }
        }

        MapUserLocationMarker(
            context = context,
            position = location,
            title = context.getString(R.string.geo_take_me),
            iconResourceId = R.drawable.ic_user_location,
        )

        LaunchedEffect(key1 = cameraPositionState.isMoving) {
            if(!cameraPositionState.isMoving) {
                clusterManager?.onCameraIdle()
            }
        }
    }
}
