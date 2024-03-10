package eu.javimar.wirelessval.features.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import eu.javimar.wirelessval.core.common.presentation.BaseViewModel
import eu.javimar.wirelessval.core.nav.screens.Screens
import eu.javimar.wirelessval.core.util.Resource
import eu.javimar.wirelessval.core.util.UIEvent
import eu.javimar.wirelessval.core.util.UIText
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.LOCATION_KEY
import eu.javimar.wirelessval.features.wifi.domain.usecase.GetWifisUseCase
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import eu.javimar.wirelessval.features.wifi.presentation.listing.state.WifiState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WifiMapViewModel(
    private val preferences: IPreferencesRepository,
    private val getWifisUseCase: GetWifisUseCase
): BaseViewModel() {

    var state by mutableStateOf(WifiState())
        private set

    init {
        getWifis()
        getLocation()
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is WifiMapEvent.OnWifiClick -> {
                sendUiEvent(
                    UIEvent.Navigate(Screens.Detail.createRoute(event.wifi))
                )
            }
            WifiMapEvent.ReadNightMode -> {
                state = state.copy(isNightMode = preferences.readBooleanValue(SharePrefsKeys.IS_DARK_KEY))
            }
        }
    }
  
    private fun getWifis() {
        viewModelScope.launch {
            getWifisUseCase.execute(
                orderOptions = WifiOrderOptions.NAME, gps = WifiCoordinates(0.0, 0.0)
            ).onEach { result ->
                when(result) {
                    is Resource.Loading -> {
                        state = state.copy(
                            isLoading = true
                        )
                    }
                    is Resource.Success -> {
                        state = state.copy(
                            wifis = result.data!!,
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false
                        )
                        sendUiEvent(
                            UIEvent.ShowSnackbar(
                                result.message ?: UIText.DynamicString("Unkown Error")
                            )
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getLocation() {
        viewModelScope.launch {
            preferences.readLocationStoredValue(LOCATION_KEY).onEach { location ->
                if(location.isNotEmpty()) {
                    location.split(",").let {
                        try {
                            state = state.copy(location = LatLng(it[0].toDouble(), it[1].toDouble()))
                        } catch(e: NumberFormatException) {
                            e.printStackTrace()
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}