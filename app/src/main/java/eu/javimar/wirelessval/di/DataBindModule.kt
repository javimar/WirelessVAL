package eu.javimar.wirelessval.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.javimar.wirelessval.features.wifi.data.WifiRepository
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindModule {

    @Binds
    abstract fun bindWifiRepository(repository: WifiRepository): IWifiRepository
}