package eu.javimar.wirelessval.features.wifi.presentation.detail.components

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun SetMapMarker(
    lat: Double,
    long: Double,
    title: String,
    icon: BitmapDescriptor?,
) {
    val markerState = rememberMarkerState(position = LatLng(lat, long))
    Marker(
        state = markerState,
        onClick = {
            markerState.showInfoWindow()
            true
        },
        onInfoWindowClick = {
            markerState.hideInfoWindow()
        },
        title = title,
        icon = icon,
    )
}