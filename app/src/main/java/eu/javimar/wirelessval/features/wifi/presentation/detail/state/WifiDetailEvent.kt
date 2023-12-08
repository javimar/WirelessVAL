package eu.javimar.wirelessval.features.wifi.presentation.detail.state

sealed interface WifiDetailEvent {
    data object OnBackClick: WifiDetailEvent
}
