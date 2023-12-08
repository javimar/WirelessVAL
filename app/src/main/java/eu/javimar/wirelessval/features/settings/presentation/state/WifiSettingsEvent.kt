package eu.javimar.wirelessval.features.settings.presentation.state

import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions


sealed interface WifiSettingsEvent {
    data class SetOrderOption(val orderOptions: WifiOrderOptions): WifiSettingsEvent
    data object OnBackClick: WifiSettingsEvent
    data class ToggleDynamic(val isDynamic: Boolean): WifiSettingsEvent
    data class ToggleDark(val isDark: Boolean): WifiSettingsEvent
}

