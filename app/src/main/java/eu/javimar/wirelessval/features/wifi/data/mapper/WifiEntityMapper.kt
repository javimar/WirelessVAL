package eu.javimar.wirelessval.features.wifi.data.mapper

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import eu.javimar.wirelessval.sqldelight.Wifis
import kotlin.math.pow
import kotlin.math.sqrt

fun List<Wifis>?.toWifiBOList(): List<WifiBO> =
    this?.map { it.toWifiBO() }?.toList() ?: run {
        listOf()
    }

fun Wifis.toWifiBO(): WifiBO =
    WifiBO(
        wifiName = wifiName,
        coordinates = WifiCoordinates(latitude, longitude),
        comments = comments ?: "",
        opinion = opinion
    )

fun List<WifiBO>.sortByDistance(gps: WifiCoordinates): List<WifiBO> {
    return sortedBy {
        val lat = it.coordinates.latitude
        val lng = it.coordinates.longitude
        calculateDistance(gps, WifiCoordinates(lat, lng))
    }.map { it }
}

private fun calculateDistance(point1: WifiCoordinates, point2: WifiCoordinates): Double {
    val latDiff = point2.latitude - point1.latitude
    val lonDiff = point2.longitude - point1.longitude
    return sqrt(latDiff.pow(2) + lonDiff.pow(2))
}