package eu.javimar.wirelessval.features.wifi.domain.utils

enum class WifiOrderOptions(val value: String) {
    NAME("byName"),
    OPINION("byOpinion"),
    DISTANCE("byDistance"),
    EMPTY("")
}

fun getOrderOptionFromValue(value: String?): WifiOrderOptions {
    return when(value){
        WifiOrderOptions.NAME.value -> WifiOrderOptions.NAME
        WifiOrderOptions.OPINION.value -> WifiOrderOptions.OPINION
        WifiOrderOptions.DISTANCE.value -> WifiOrderOptions.DISTANCE
        WifiOrderOptions.EMPTY.value -> WifiOrderOptions.NAME
        else -> WifiOrderOptions.NAME
    }
}