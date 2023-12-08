package eu.javimar.wirelessval.features.main.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.javimar.wirelessval.core.common.connectivity.ConnectivityObserver
import eu.javimar.wirelessval.features.main.presentation.state.ColorState
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.HAS_INTERNET_KEY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
    private val sharePrefs: IPreferencesRepository,
): ViewModel() {

    var connected = MutableStateFlow(true)
        private set

    var colorState by mutableStateOf(ColorState())
        private set

    init {
        initColors()
        listenForConnectivity()
        listenToColorChange()
    }

    fun getColorMode(): Boolean = !sharePrefs.readBooleanValue(SharePrefsKeys.IS_DARK_KEY)

    private fun initColors() {
        colorState = colorState.copy(
            isDynamic = sharePrefs.readBooleanValue(SharePrefsKeys.IS_DYNAMIC_KEY),
            isDark = sharePrefs.readBooleanValue(SharePrefsKeys.IS_DARK_KEY),
        )
    }
    private fun listenToColorChange() {
        viewModelScope.launch {
            sharePrefs.readIsDynamicColorValue().onEach {
                colorState = colorState.copy(isDynamic = it)
            }.launchIn(viewModelScope)

            sharePrefs.readIsLigthColorValue().onEach {
                colorState = colorState.copy(isDark = it)
            }.launchIn(viewModelScope)
        }
    }

    private fun listenForConnectivity() {
        if(!sharePrefs.readBooleanValue(HAS_INTERNET_KEY)) {
            connected.update { false }
        }
        viewModelScope.launch {
            connectivityObserver.observe().onEach {
                when(it) {
                    ConnectivityObserver.Status.Available -> {
                        sharePrefs.setBooleanValue(HAS_INTERNET_KEY, true)
                        connected.update { true }
                    }
                    else -> {
                        sharePrefs.setBooleanValue(HAS_INTERNET_KEY, false)
                        connected.update { false }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}