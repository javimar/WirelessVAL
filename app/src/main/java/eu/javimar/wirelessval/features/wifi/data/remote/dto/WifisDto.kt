package eu.javimar.wirelessval.features.wifi.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WifisDto(
    @SerialName(value = "results") var results: List<WifiDto>?
)
