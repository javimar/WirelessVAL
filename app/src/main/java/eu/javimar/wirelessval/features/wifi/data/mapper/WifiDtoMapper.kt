package eu.javimar.wirelessval.features.wifi.data.mapper

import eu.javimar.wirelessval.features.wifi.data.remote.dto.GeoPointDto
import eu.javimar.wirelessval.features.wifi.data.remote.dto.WifiDto
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates

fun List<WifiDto>?.toWifiBOList(): List<WifiBO> =
    this?.map { it.toWifiBO() }?.toList() ?: run {
        listOf()
    }

fun WifiDto.toWifiBO(): WifiBO {
    return WifiBO(
        wifiName = removePrefix(wifiName),
        coordinates = geoPoint.toGeoPoint(),
        comments = "",
        opinion = 0.0
    )
}

fun GeoPointDto?.toGeoPoint(): WifiCoordinates {
    return WifiCoordinates(
        latitude = this?.latitude ?: 0.0,
        longitude = this?.longitude ?: 0.0,
    )
}

fun removePrefix(input: String?): String {
    val prefix = "WiFi4EU_"
    return input?.removePrefix(prefix) ?: ""
}
