package eu.javimar.wirelessval.features.wifi.presentation.listing.state

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO

sealed interface WifiListEvent {
    data class OnWifiClick(val wifi: WifiBO): WifiListEvent
    data class SearchWifis(val value: String): WifiListEvent
    data object AskReloadWifis: WifiListEvent
    data object ReloadWifis: WifiListEvent
    data object OnSettingsClick: WifiListEvent
    data object CancelDialog: WifiListEvent
    data object CancelConnectionDialog: WifiListEvent
    data object ToggleMenu: WifiListEvent
    data object RefreshWifiOnScreen: WifiListEvent
}
