package eu.javimar.wirelessval.features.wifi.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoPointDto(
    @SerialName(value = "lon") var longitude: Double?,
    @SerialName(value = "lat") var latitude: Double?,
)
