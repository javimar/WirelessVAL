package eu.javimar.wirelessval.features.wifi.presentation.detail.state

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO

data class WifiDetailState(
    val wifi: WifiBO? = null,
    val isLoading: Boolean = false,
)