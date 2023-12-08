package eu.javimar.wirelessval.features.wifi.presentation.detail.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.hasLocationPermission
import eu.javimar.wirelessval.features.map.bitmapDescriptorFromVector

@Composable
fun DetailWifiMap(
    lat: Double,
    long: Double,
    name: String
) {
    val context = LocalContext.current
    val fallaLocation = LatLng(lat, long)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(fallaLocation, 19f)
    }
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .size(400.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = context.hasLocationPermission()
            ),
            properties = MapProperties(
                mapType = MapType.HYBRID,
                isBuildingEnabled = true,
                isTrafficEnabled = true,
                isMyLocationEnabled = context.hasLocationPermission()
            )
        ) {
            SetMapMarker(
                lat = lat,
                long = long,
                title = name,
                icon = bitmapDescriptorFromVector(context, R.drawable.ic_wifi)
            )
        }
    }
}

@Preview
@Composable
fun DetailFallaMapPreview() {
    DetailWifiMap(
        lat = 0.0,
        long = 0.0,
        name = "Madrid"
    )
}
