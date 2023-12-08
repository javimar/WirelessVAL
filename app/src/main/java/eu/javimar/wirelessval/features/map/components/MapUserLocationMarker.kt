package eu.javimar.wirelessval.features.map.components

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import eu.javimar.wirelessval.features.map.bitmapDescriptorFromVector

@Composable
fun MapUserLocationMarker(
    context: Context,
    position: LatLng,
    title: String,
    @DrawableRes iconResourceId: Int,
) {
    val icon = bitmapDescriptorFromVector(
        context,
        iconResourceId
    )
    Marker(
        state = rememberMarkerState(position = position),
        title = title,
        icon = icon,
    )
}