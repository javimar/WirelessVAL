package eu.javimar.wirelessval.features.wifi.domain.utils

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class GeoPoint(var longitude: Double, var latitude: Double) {

    fun distance(point: GeoPoint): Double {
        val earthRadius = 6371000.0 // en metros
        val dLat = Math.toRadians(latitude - point.latitude)
        val dLon = Math.toRadians(longitude - point.longitude)
        val lat1 = Math.toRadians(point.latitude)
        val lat2 = Math.toRadians(latitude)
        val a = sin(dLat / 2) * sin(dLat / 2) + sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return c * earthRadius
    }

    override fun toString(): String {
        return if (hasCoordinates()) {
            // solo muestro las primeras posiciones, pero almaceno en BBDD el número entero
            "Latitude: " + latitude.toString()
                .substring(0, 6) + " Longitude: " + longitude.toString().substring(0, 6)
        } else {
            "Lat: $latitude,  Lng: $longitude"
        }
    }

    private fun hasCoordinates(): Boolean {
        return !(latitude == 0.0 && longitude == 0.0)
    }

    /** Necessary methods to support GeoPoint as a HashMap key to reflect "equality" of two objects. */
    override fun hashCode(): Int {
        return abs(longitude + latitude).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is GeoPoint) return false
        val g = other
        return g.latitude == latitude && g.longitude == longitude
    }
}
