package eu.javimar.wirelessval.di

import android.content.Context
import eu.javimar.wirelessval.features.settings.data.repository.SharedPreferencesDataSource
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository

interface IPrefsModule {
    val sharePrefs: IPreferencesRepository
}

class PrefsModule(
    private val context: Context
): IPrefsModule {
    override val sharePrefs: IPreferencesRepository by lazy {
        SharedPreferencesDataSource(context)
    }
}