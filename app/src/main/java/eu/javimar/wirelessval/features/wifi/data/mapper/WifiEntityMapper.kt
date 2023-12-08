package eu.javimar.wirelessval.features.wifi.data.mapper

import com.google.android.gms.maps.model.LatLng
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

fun List<Wifis>.sortByDistance(gps: LatLng): List<Wifis> {
    return sortedBy {
        val lat = it.latitude
        val lng = it.longitude
        calculateDistance(gps, LatLng(lat, lng))
    }.map { it }
}

private fun calculateDistance(point1: LatLng, point2: LatLng): Double {
    val latDiff = point2.latitude - point1.latitude
    val lonDiff = point2.longitude - point1.longitude
    return sqrt(latDiff.pow(2) + lonDiff.pow(2))
}