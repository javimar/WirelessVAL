package eu.javimar.wirelessval.di

import android.content.Context
import eu.javimar.wirelessval.features.settings.data.repository.SharedPreferencesDataSource
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository

interface IPrefsModule {
    val sharePrefsModule: IPreferencesRepository
}

class PrefsModule(
    private val context: Context
): IPrefsModule {

    override val sharePrefsModule: IPreferencesRepository by lazy {
        SharedPreferencesDataSource(context)
    }
}