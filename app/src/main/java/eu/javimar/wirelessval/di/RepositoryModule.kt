package eu.javimar.wirelessval.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.javimar.wirelessval.features.wifi.data.WifiRepository
import eu.javimar.wirelessval.features.wifi.data.remote.datasource.WifiRemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWifiRepository(
        //localDataSource: FallaLocalDataSource,
        remoteDataSource: WifiRemoteDataSource
    ): WifiRepository = WifiRepository(
        //localDataSource,
        remoteDataSource
    )
}