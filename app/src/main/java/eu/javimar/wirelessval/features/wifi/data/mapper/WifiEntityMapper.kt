package eu.javimar.wirelessval.features.wifi.data.mapper

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint
import eu.javimar.wirelessval.sqldelight.Wifis

fun List<Wifis>?.toWifiBOList(): List<WifiBO> =
    this?.map { it.toWifiBO() }?.toList() ?: run {
        listOf()
    }

fun Wifis.toWifiBO(): WifiBO =
    WifiBO(
        wifiName = wifiName,
        coordinates = GeoPoint(longitude, latitude),
        comments = "",
        opinion = 0.0
    )

fun List<Wifis>.sortByDistance(gps: GeoPoint): List<Wifis> {
    return sortedBy {
        gps.distance(GeoPoint(it.longitude, it.latitude))
    }.map { it }
}