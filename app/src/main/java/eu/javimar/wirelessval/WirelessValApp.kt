package eu.javimar.wirelessval

import android.app.Application
import eu.javimar.wirelessval.di.ConnectivityModule
import eu.javimar.wirelessval.di.DbModule
import eu.javimar.wirelessval.di.IConnectivityModule
import eu.javimar.wirelessval.di.IDbModule
import eu.javimar.wirelessval.di.INetworkModule
import eu.javimar.wirelessval.di.IPrefsModule
import eu.javimar.wirelessval.di.IRepoModule
import eu.javimar.wirelessval.di.IUseCaseModule
import eu.javimar.wirelessval.di.NetworkModule
import eu.javimar.wirelessval.di.PrefsModule
import eu.javimar.wirelessval.di.RepoModule
import eu.javimar.wirelessval.di.UseCaseModule

class WirelessValApp: Application() {

    companion object {
        lateinit var connectivityModule: IConnectivityModule
        lateinit var prefsModule: IPrefsModule
        lateinit var dbModule: IDbModule
        lateinit var networkModule: INetworkModule
        lateinit var repoModule: IRepoModule
        lateinit var useCaseModule: IUseCaseModule
    }

    override fun onCreate() {
        super.onCreate()
        connectivityModule = ConnectivityModule(this)
        prefsModule = PrefsModule(this)
        dbModule = DbModule(this)
        networkModule = NetworkModule(this)
        repoModule = RepoModule(
            remoteClient = networkModule.networkClient,
            wifiDb = dbModule.db
        )
        useCaseModule = UseCaseModule(
            localRepository = repoModule.localRepo,
            remoteRepository = repoModule.remoteRepo
        )
    }
}