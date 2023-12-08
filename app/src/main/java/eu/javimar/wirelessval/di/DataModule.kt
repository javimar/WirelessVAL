package eu.javimar.wirelessval.di

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eu.javimar.wirelessval.features.settings.data.repository.SharedPreferencesDataSource
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.wifi.data.local.datasource.WifiLocalDataSource
import eu.javimar.wirelessval.sqldelight.WirelessVALDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): WirelessVALDatabase {
        val driver: SqlDriver = AndroidSqliteDriver(WirelessVALDatabase.Schema, appContext, "wifis_vlc.db")
        return WirelessVALDatabase(driver)
    }

    @Provides
    fun provideFallaLocalDataSource(db: WirelessVALDatabase) = WifiLocalDataSource(db)

    @Singleton
    @Provides
    fun provideSharePrefs(application: Application): IPreferencesRepository = SharedPreferencesDataSource(application)

}