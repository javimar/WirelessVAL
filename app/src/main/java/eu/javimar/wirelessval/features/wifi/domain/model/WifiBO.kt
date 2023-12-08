package eu.javimar.wirelessval.features.wifi.domain.model

import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint

data class WifiBO(
    val wifiName: String,
    val coordinates: GeoPoint,
    val comments: String,
    val opinion: Double,
)