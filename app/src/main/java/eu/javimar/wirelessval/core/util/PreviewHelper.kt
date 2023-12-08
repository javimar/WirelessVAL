package eu.javimar.wirelessval.core.util

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint

val wifiBOMock = WifiBO(
    wifiId = "1969",
    wifiName = "Alameda",
    coordinates = GeoPoint(
        longitude = -3.703790,
        latitude = 40.416775,
    ),
    comments = "Grande",
    opinion = 5.0f,
)