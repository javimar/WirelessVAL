package eu.javimar.wirelessval.features.wifi.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ArcGIS manda la geometría como {"x": ..., "y": ...} en vez del "geo_point_2d" de
// OpenDataSoft. Con outSR=4326 (ver WifiRemoteDataSource) el servidor ya devuelve
// WGS84: x = longitud, y = latitud. No hace falta convertir el UTM 30N nativo a mano.
@Serializable
data class GeoPointDto(
    @SerialName(value = "x") var longitude: Double?,
    @SerialName(value = "y") var latitude: Double?,
)
