package eu.javimar.wirelessval.features.wifi.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.javimar.wirelessval.core.common.presentation.BaseViewModel
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys
import eu.javimar.wirelessval.features.wifi.presentation.detail.state.WifiDetailState
import javax.inject.Inject

@HiltViewModel
class WifiDetailViewModel @Inject constructor(
    sharePrefs: IPreferencesRepository,
    savedStateHandle: SavedStateHandle,
): BaseViewModel() {

    var state by mutableStateOf(WifiDetailState())
        private set

    private var wifiName = ""
    val isNightMode: Boolean = sharePrefs.readBooleanValue(SharePrefsKeys.IS_DARK_KEY)


    override fun <T> onEvent(event: T) {
        TODO("Not yet implemented")
    }




}