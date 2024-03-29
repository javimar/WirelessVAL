package eu.javimar.wirelessval.core.util

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates

val wifiBOMock = WifiBO(
    wifiName = "Alameda",
    coordinates = WifiCoordinates(
        longitude = -3.703790,
        latitude = 40.416775,
    ),
    comments = "Grande",
    opinion = 5.0,
)