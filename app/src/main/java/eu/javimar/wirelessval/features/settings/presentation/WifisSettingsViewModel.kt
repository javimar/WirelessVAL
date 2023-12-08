package eu.javimar.wirelessval.features.settings.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.javimar.wirelessval.core.common.presentation.BaseViewModel
import eu.javimar.wirelessval.core.util.UIEvent
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.IS_DARK_KEY
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.IS_DYNAMIC_KEY
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.WIFIS_ORDER_KEY
import eu.javimar.wirelessval.features.settings.presentation.state.WifiSettingsEvent
import eu.javimar.wirelessval.features.settings.presentation.state.WifiSettingsState
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import eu.javimar.wirelessval.features.wifi.domain.utils.getOrderOptionFromValue
import javax.inject.Inject

@HiltViewModel
class WifisSettingsViewModel @Inject constructor(
    private val sharePrefs: IPreferencesRepository,
): BaseViewModel() {

    var state by mutableStateOf(WifiSettingsState())
        private set

    init {
        getOrderOption()
    }

    override fun <T> onEvent(event: T) {
        when(event) {
            is WifiSettingsEvent.OnBackClick -> sendUiEvent(UIEvent.PopBackStack)
            is WifiSettingsEvent.SetOrderOption -> setOrderOption(event.orderOptions)
            is WifiSettingsEvent.ToggleDynamic -> {
                sharePrefs.setBooleanValue(IS_DYNAMIC_KEY, event.isDynamic)
                state = state.copy(isAppColorsSelected = event.isDynamic)
            }
            is WifiSettingsEvent.ToggleDark -> {
                sharePrefs.setBooleanValue(IS_DARK_KEY, event.isDark)
                state = state.copy(isDarkSelected = event.isDark)
            }
        }
    }

    private fun getOrderOption() {
        state = state.copy(
            orderOptions = getOrderOptionFromValue(sharePrefs.readStringStoredValue(WIFIS_ORDER_KEY)),
            isAppColorsSelected = sharePrefs.readBooleanValue(IS_DYNAMIC_KEY),
            isDarkSelected = sharePrefs.readBooleanValue(IS_DARK_KEY),
        )
    }

    private fun setOrderOption(option: WifiOrderOptions) {
        sharePrefs.setStringStoredValue(WIFIS_ORDER_KEY, option.value)
        state = state.copy(orderOptions = option)
    }
}