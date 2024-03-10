package eu.javimar.wirelessval.features.wifi.presentation.detail.state

sealed interface WifiDetailEvent {
    data object OnBackClick: WifiDetailEvent
    data object StarClicked: WifiDetailEvent
    data object UpdateWifi: WifiDetailEvent
    data class CommentsChange(val comments: String): WifiDetailEvent
}
