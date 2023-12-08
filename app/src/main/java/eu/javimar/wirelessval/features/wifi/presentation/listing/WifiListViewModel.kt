package eu.javimar.wirelessval.features.wifi.presentation.listing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.presentation.BaseViewModel
import eu.javimar.wirelessval.core.nav.screens.Screens
import eu.javimar.wirelessval.core.util.Resource
import eu.javimar.wirelessval.core.util.UIEvent
import eu.javimar.wirelessval.core.util.UIText
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.LAST_UPDATED_KEY
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.LOCATION_KEY
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.WIFIS_ORDER_KEY
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.usecase.wifilisting.GetWifisUseCase
import eu.javimar.wirelessval.features.wifi.domain.usecase.wifilisting.ReloadFromServerUseCase
import eu.javimar.wirelessval.features.wifi.domain.usecase.wifilisting.SearchWifisUseCase
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import eu.javimar.wirelessval.features.wifi.domain.utils.getOrderOptionFromValue
import eu.javimar.wirelessval.features.wifi.presentation.listing.state.WifiListEvent
import eu.javimar.wirelessval.features.wifi.presentation.listing.state.WifiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WifiListViewModel @Inject constructor(
    private val getWifisUseCase: GetWifisUseCase,
    private val reloadWifisUseCase: ReloadFromServerUseCase,
    private val searchWifisUseCase: SearchWifisUseCase,
    private val sharePrefs: IPreferencesRepository,
): BaseViewModel() {

    var state by mutableStateOf(WifiState())
        private set

    private var searchJob: Job? = null
    private var isReload = false

    init {
        isReload = false
        getLocation()
    }

    override fun <T> onEvent(event: T) {
        when(event) {
            is WifiListEvent.OnWifiClick -> {
                sendUiEvent(
                    UIEvent.Navigate("${Screens.Detail.route}/${event.wifi.wifiName}")
                )
            }
            is WifiListEvent.SearchWifis -> searchWifis(event.value)
            WifiListEvent.RefreshWifiOnScreen -> getOrderOption()
            WifiListEvent.AskReloadWifis -> {
                state = if(!sharePrefs.readBooleanValue(SharePrefsKeys.HAS_INTERNET_KEY)) {
                    sendUiEvent(UIEvent.ShowSnackbar(UIText.StringResource(R.string.no_internet_connection)))
                    state.copy(
                        showMenu = false
                    )
                } else {
                    state.copy(
                        showDialog = true,
                        showMenu = false
                    )
                }
            }
            WifiListEvent.ReloadWifis -> reloadWifis()
            WifiListEvent.OnSettingsClick -> {
                state = state.copy(showMenu = false)
                sendUiEvent(
                    UIEvent.Navigate(Screens.Settings.route)
                )
            }
            WifiListEvent.CancelConnectionDialog -> cancelConnectionDialog()
            WifiListEvent.CancelDialog -> cancelDialog()
            WifiListEvent.ToggleMenu -> state = state.copy(showMenu = !state.showMenu)
        }
    }

    private fun searchWifis(query: String) {
        state = state.copy(searchQuery = query)

        if(query.isEmpty()) {
            getOrderOption()
        } else {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(1000L)
                searchWifisUseCase.execute(query).onEach { result ->
                    when(result) {
                        is Resource.Loading -> {
                            state = state.copy(
                                isLoading = true,
                            )
                        }
                        is Resource.Success -> {
                            state = state.copy(
                                wifis = result.data ?: emptyList(),
                                isLoading = false,
                                numWifis = result.data!!.size
                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false
                            )
                            sendUiEvent(
                                UIEvent.ShowSnackbar(
                                    result.message ?: UIText.DynamicString("Unknown Error")
                                )
                            )
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun getWifis(orderOptions: WifiOrderOptions) {
        viewModelScope.launch {
            if(isReload) {
                reloadWifisUseCase.execute(orderOptions = orderOptions).onEach { result ->
                    isReload = false
                    cancelDialog()
                    getWifisResult(result)
                }.launchIn(viewModelScope)
            } else {
                getWifisUseCase.execute(orderOptions = orderOptions, gps = state.location).onEach { result ->
                    getWifisResult(result)
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun getWifisResult(result: Resource<List<WifiBO>>) {
        when(result) {
            is Resource.Loading -> {
                state = state.copy(
                    isLoading = true
                )
            }
            is Resource.Success -> {
                state = state.copy(
                    isLoading = false,
                    wifis = result.data!!,
                    numWifis = result.data.size
                )
                if(result.isFirstLoading) {
                    saveLastUpdate()
                }
            }
            is Resource.Error -> {
                state = state.copy(
                    isLoading = false,
                )
                sendUiEvent(
                    UIEvent.ShowSnackbar(
                        result.message ?: UIText.DynamicString("Unknown Error")
                    )
                )
            }
        }
    }

    private fun getOrderOption() {
        val option = sharePrefs.readStringStoredValue(WIFIS_ORDER_KEY)
        getWifis(getOrderOptionFromValue(option))
    }

    private fun getLocation() {
        viewModelScope.launch {
            sharePrefs.readLocationStoredValue(LOCATION_KEY).onEach { location ->
                if(location.isNotEmpty()) {
                    location.split(",").let {
                        try {
                            state = state.copy(
                                location = GeoPoint(it[0].toDouble(), it[1].toDouble())
                            )
                        } catch(e: NumberFormatException) {
                            e.printStackTrace()
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
        state = state.copy(showConnectionDialog = true)
    }
    
    private fun cancelDialog() {
        state = state.copy(showDialog = false)
    }

    private fun cancelConnectionDialog() {
        state = state.copy(showConnectionDialog = false)
    }

    private fun reloadWifis() {
        isReload = true
        getOrderOption()
    }
    
    private fun saveLastUpdate() {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val current = LocalDateTime.now().format(formatter)
        sharePrefs.setStringStoredValue(LAST_UPDATED_KEY, current)
    }
}