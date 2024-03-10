package eu.javimar.wirelessval.features.wifi.domain.utils

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO

fun MutableList<WifiBO>.updateWifisList(newList: List<WifiBO>) {
    for(newWifi in newList) {
        val existingIndex = indexOfFirst { it.wifiName == newWifi.wifiName }
        if (existingIndex != -1) {
            // Wifi already exists in the list, update everything except Opinion
            this[existingIndex] = newWifi.copy(opinion = this[existingIndex].opinion)
        } else {
            // Wifi is not in the existing list, add it to the updated list
            this.add(newWifi)
        }
    }
}
