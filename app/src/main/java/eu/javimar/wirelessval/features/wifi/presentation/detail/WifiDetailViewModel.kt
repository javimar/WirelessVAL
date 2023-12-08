package eu.javimar.wirelessval.features.wifi.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.javimar.wirelessval.core.common.presentation.BaseViewModel
import eu.javimar.wirelessval.core.nav.screens.WifiNavArgs
import eu.javimar.wirelessval.core.util.UIEvent
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.usecase.UpdateWifiUseCase
import eu.javimar.wirelessval.features.wifi.presentation.detail.state.WifiDetailEvent
import eu.javimar.wirelessval.features.wifi.presentation.detail.state.WifiDetailState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WifiDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateWifiUseCase: UpdateWifiUseCase
): BaseViewModel() {

    var state by mutableStateOf(WifiDetailState())
        private set

    init {
        savedStateHandle.get<WifiBO>(WifiNavArgs.Wifi.key)?.let {
            state = state.copy(
                wifi = it,
                starValue = it.opinion,
                comments = it.comments
            )
        }
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            WifiDetailEvent.OnBackClick -> sendUiEvent(UIEvent.PopBackStack)
            WifiDetailEvent.StarClicked -> state = state.copy(starValue = calcRating())
            WifiDetailEvent.UpdateWifi -> updateWifi()
            is WifiDetailEvent.CommentsChange -> state = state.copy(comments = event.comments)
        }
    }

    private fun updateWifi() {
        viewModelScope.launch {
            state = state.copy(
                wifi = WifiBO(
                    wifiName = state.wifi!!.wifiName,
                    coordinates = state.wifi!!.coordinates,
                    opinion = state.starValue,
                    comments = state.comments
                )
            )
            updateWifiUseCase.execute(state.wifi!!)
            sendUiEvent(UIEvent.PopBackStack)
        }
    }

    private fun calcRating(): Double {
        if(state.starValue == 5.0) return 0.0
        return when(val adjustedRating = state.starValue % 5.0) {
            in 0.0.. 4.5 -> adjustedRating + 0.5
            else -> 0.0
        }
    }
}