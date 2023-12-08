package eu.javimar.wirelessval.features.wifi.domain.model

data class WifiBO(
    val wifiName: String,
    val latitude: Double,
    val longitude: Double,
    val comments: String,
    val opinion: Float,
)