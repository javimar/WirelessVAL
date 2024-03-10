package eu.javimar.wirelessval.di

import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiLocalRepository
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRemoteRepository
import eu.javimar.wirelessval.features.wifi.domain.usecase.GetWifisUseCase
import eu.javimar.wirelessval.features.wifi.domain.usecase.ReloadFromServerUseCase
import eu.javimar.wirelessval.features.wifi.domain.usecase.SearchWifisUseCase
import eu.javimar.wirelessval.features.wifi.domain.usecase.UpdateWifiUseCase

interface IUseCaseModule {
    val getWifis: GetWifisUseCase
    val reloadWifis: ReloadFromServerUseCase
    val searchWifis: SearchWifisUseCase
    val updateWifis: UpdateWifiUseCase
}

class UseCaseModule(
    private val localRepository: IWifiLocalRepository,
    private val remoteRepository: IWifiRemoteRepository
): IUseCaseModule {
    override val getWifis: GetWifisUseCase by lazy {
        GetWifisUseCase(localRepository, remoteRepository)
    }
    override val reloadWifis: ReloadFromServerUseCase by lazy {
        ReloadFromServerUseCase(localRepository, remoteRepository)
    }
    override val searchWifis: SearchWifisUseCase by lazy {
        SearchWifisUseCase(localRepository)
    }
    override val updateWifis: UpdateWifiUseCase by lazy {
        UpdateWifiUseCase(localRepository)
    }
}