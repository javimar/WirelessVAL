package eu.javimar.wirelessval.features.wifi.data

import eu.javimar.wirelessval.features.wifi.data.mapper.toWifiBOList
import eu.javimar.wirelessval.features.wifi.data.remote.datasource.WifiRemoteDataSource
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions

class WifiRepository(
    private val remoteDataSource: WifiRemoteDataSource
): IWifiRepository {

    override suspend fun insertWifis(wifis: List<WifiBO>) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWifis(): List<WifiBO> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWifisByOption(
        options: WifiOrderOptions, gps: WifiCoordinates
    ): List<WifiBO> {
        TODO("Not yet implemented")
    }

    override suspend fun findFalla(name: String): WifiBO? {
        TODO("Not yet implemented")
    }

    override suspend fun countNumberOfRows(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWifi(name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllWifis() {
        TODO("Not yet implemented")
    }

    override suspend fun getWifisFromServer(limit: Int): List<WifiBO> {
        return remoteDataSource.getWifisFromServer(limit)?.results.toWifiBOList()
    }

    override suspend fun getSearchResults(query: String): List<WifiBO> {
        TODO("Not yet implemented")
    }
}