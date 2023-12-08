package eu.javimar.wirelessval.features.settings.presentation.state

import android.os.Build
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions

data class WifiSettingsState(
    val orderOptions: WifiOrderOptions = WifiOrderOptions.EMPTY,
    val isAppColorsSelected: Boolean = false,
    val isDarkSelected: Boolean = false,
    val dynamicColorsEnabled: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
)
