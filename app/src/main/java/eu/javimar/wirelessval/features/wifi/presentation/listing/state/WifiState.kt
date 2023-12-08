package eu.javimar.wirelessval.features.wifi.presentation.listing.state

import com.google.android.gms.maps.model.LatLng
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO

data class WifiState(
    val wifis: List<WifiBO> = emptyList(),
    val isLoading: Boolean = false,
    val numWifis: Int = -1,
    val showDialog: Boolean = false,
    val showConnectionDialog: Boolean = false,
    val location: LatLng = LatLng(0.0, 0.0),
    val searchQuery: String = "",
    val showMenu: Boolean = false,
    val isNightMode: Boolean = false,
)
