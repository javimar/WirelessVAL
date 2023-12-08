package eu.javimar.wirelessval.features.wifi.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WifiDto(
    @SerialName(value = "descripcion") var wifiName: String?,
    @SerialName(value = "geo_point_2d") var geoPoint: GeoPointDto?
)
