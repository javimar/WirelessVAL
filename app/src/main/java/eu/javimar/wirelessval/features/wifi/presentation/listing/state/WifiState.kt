package eu.javimar.wirelessval.features.wifi.presentation.listing.state

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint

data class WifiState(
    val wifis: List<WifiBO> = emptyList(),
    val isLoading: Boolean = false,
    val numWifis: Int = -1,
    val showDialog: Boolean = false,
    val showConnectionDialog: Boolean = false,
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val searchQuery: String = "",
    val showMenu: Boolean = false,
    val isNightMode: Boolean = false,
)
