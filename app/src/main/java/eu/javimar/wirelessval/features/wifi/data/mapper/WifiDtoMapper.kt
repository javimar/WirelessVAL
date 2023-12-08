package eu.javimar.wirelessval.features.wifi.data.mapper

import eu.javimar.wirelessval.features.wifi.data.remote.dto.GeoPointDto
import eu.javimar.wirelessval.features.wifi.data.remote.dto.WifiDto
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint

fun List<WifiDto>?.toWifiBOList(): List<WifiBO> =
    this?.map { it.toWifiBO() }?.toList() ?: run {
        listOf()
    }

fun WifiDto.toWifiBO(): WifiBO {
    return WifiBO(
        wifiId = wifiId ?: "",
        wifiName = removePrefix(wifiName),
        coordinates = geoPoint.toGeoPoint(),
        comments = "",
        opinion = 0F
    )
}

fun GeoPointDto?.toGeoPoint(): GeoPoint {
    return GeoPoint(
        longitude = this?.longitude ?: 0.0,
        latitude = this?.latitude ?: 0.0
    )
}

fun removePrefix(input: String?): String {
    val prefix = "WiFi4EU_"
    return input?.removePrefix(prefix) ?: ""
}
