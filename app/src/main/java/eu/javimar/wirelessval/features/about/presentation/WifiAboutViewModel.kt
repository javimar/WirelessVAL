package eu.javimar.wirelessval.features.about.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.LAST_UPDATED_KEY

class WifiAboutViewModel(
    private val sharedPrefs: IPreferencesRepository,
): ViewModel() {

    var dateState by mutableStateOf("")
        private set

    init {
        getLastUpdate()
    }

    private fun getLastUpdate() {
        dateState = sharedPrefs.readStringStoredValue(LAST_UPDATED_KEY)
    }
}