package eu.javimar.wirelessval.features.map

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO

sealed interface WifiMapEvent {
    data class OnWifiClick(val wifi: WifiBO): WifiMapEvent
    data object ReadNightMode: WifiMapEvent
}
