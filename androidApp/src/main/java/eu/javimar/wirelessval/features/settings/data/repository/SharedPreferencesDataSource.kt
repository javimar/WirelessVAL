package eu.javimar.wirelessval.features.settings.data.repository

import android.content.Context
import android.content.SharedPreferences
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class SharedPreferencesDataSource(val context: Context): IPreferencesRepository {

    companion object {
        private const val MY_WIFIS_SHARED_PREFS = "my_wifis_preferences"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(MY_WIFIS_SHARED_PREFS, Context.MODE_PRIVATE)

    override fun setStringStoredValue(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun setBooleanValue(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun readBooleanValue(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    override suspend fun readIsDynamicColorValue(): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(sharedPreferences.getBoolean(SharePrefsKeys.IS_DYNAMIC_KEY, false))
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.buffer(Channel.UNLIMITED)

    override suspend fun readIsLigthColorValue(): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(sharedPreferences.getBoolean(SharePrefsKeys.IS_DARK_KEY, false))
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.buffer(Channel.UNLIMITED)

    override fun readStringStoredValue(key: String): String = sharedPreferences.getString(key, "") ?: ""

    override suspend fun readLocationStoredValue(key: String): Flow<String> = flow {
        emit(sharedPreferences.getString(key, "") ?: "")
    }

    override fun clearValueFromPrefs(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}