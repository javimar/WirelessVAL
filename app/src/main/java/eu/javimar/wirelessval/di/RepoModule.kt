package eu.javimar.wirelessval.di

import eu.javimar.wirelessval.features.wifi.data.local.datasource.WifiLocalDataSource
import eu.javimar.wirelessval.features.wifi.data.remote.datasource.WifiRemoteDataSource
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiLocalRepository
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRemoteRepository
import eu.javimar.wirelessval.sqldelight.WirelessVALDatabase
import io.ktor.client.HttpClient

interface IRepoModule {
    val localRepo: IWifiLocalRepository
    val remoteRepo: IWifiRemoteRepository
}

class RepoModule(
    private val remoteClient: HttpClient,
    private val wifiDb: WirelessVALDatabase
): IRepoModule {
    override val localRepo: IWifiLocalRepository by lazy {
        WifiLocalDataSource(wifiDb)
    }
    override val remoteRepo: IWifiRemoteRepository by lazy {
        WifiRemoteDataSource(remoteClient)
    }
}