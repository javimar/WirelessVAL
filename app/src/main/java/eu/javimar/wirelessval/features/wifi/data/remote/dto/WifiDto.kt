package eu.javimar.wirelessval.features.wifi.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ArcGIS envuelve cada feature en "attributes" (los campos) + "geometry" (coordenadas),
// no en un objeto plano como el OpenDataSoft antiguo.
@Serializable
data class WifiDto(
    @SerialName(value = "attributes") var attributes: WifiAttributesDto?,
    @SerialName(value = "geometry") var geoPoint: GeoPointDto?
)

@Serializable
data class WifiAttributesDto(
    @SerialName(value = "gid") var gid: Int?,
    @SerialName(value = "descripcion") var wifiName: String?,
    @SerialName(value = "tipo") var tipo: Int?
)
