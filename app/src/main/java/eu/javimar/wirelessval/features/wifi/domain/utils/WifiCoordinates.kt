package eu.javimar.wirelessval.features.wifi.domain.utils

import kotlinx.serialization.Serializable

@Serializable
data class WifiCoordinates(
    val latitude: Double,
    val longitude: Double
) {
    override fun toString(): String {
        return "Lat: " + latitude.toString()
            .substring(0, 7) + " Lon: " + longitude.toString().substring(0, 7)
    }
}
