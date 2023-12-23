package eu.javimar.wirelessval

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import eu.javimar.wirelessval.di.ConnectivityModule
import eu.javimar.wirelessval.di.IConnectivityModule
import eu.javimar.wirelessval.di.IPrefsModule
import eu.javimar.wirelessval.di.PrefsModule

@HiltAndroidApp
class WirelessValApp: Application() {

    companion object {
        lateinit var connectivityModule: IConnectivityModule
        lateinit var prefsModule: IPrefsModule
    }

    override fun onCreate() {
        super.onCreate()
        connectivityModule = ConnectivityModule(this)
        prefsModule = PrefsModule(this)
    }
}