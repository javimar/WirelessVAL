package eu.javimar.wirelessval.features.wifi.data

import eu.javimar.wirelessval.features.wifi.data.local.datasource.WifiLocalDataSource
import eu.javimar.wirelessval.features.wifi.data.mapper.toFallasList
import eu.javimar.wirelessval.features.wifi.data.mapper.toWifiBO
import eu.javimar.wirelessval.features.wifi.data.mapper.toWifiBOList
import eu.javimar.wirelessval.features.wifi.data.remote.datasource.WifiRemoteDataSource
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions

class WifiRepository(
    private val localDataSource: WifiLocalDataSource,
    private val remoteDataSource: WifiRemoteDataSource
): IWifiRepository {

    override suspend fun insertWifis(wifis: List<WifiBO>) = localDataSource.insertWifis(wifis.toFallasList())

    override suspend fun getAllWifis(): List<WifiBO> = localDataSource.getAllWifis().toWifiBOList()

    override suspend fun getAllWifisByOption(
        options: WifiOrderOptions, gps: GeoPoint
    ): List<WifiBO> {
        return localDataSource.getAllFallasByOption(options, gps).toWifiBOList()
    }

    override suspend fun findWifi(name: String, gps: GeoPoint): WifiBO =
        localDataSource.findWifi(name, gps.longitude, gps.latitude).toWifiBO()

    override suspend fun countNumberOfRows(): Int = localDataSource.countNumberOfRows()

    override suspend fun deleteWifi(name: String, gps: GeoPoint) =
        localDataSource.deleteWifi(name, gps.longitude, gps.latitude)

    override suspend fun deleteAllWifis() = localDataSource.deleteAllWifis()

    override suspend fun getSearchResults(query: String): List<WifiBO> =
        localDataSource.getSearchResults(query).toWifiBOList()

    override fun checkIfWifiInDb(name: String, gps: GeoPoint): Int =
        localDataSource.checkIfWifiInDb(name, gps.longitude, gps.latitude)

    override fun updateOpinionComments(
        opinion: Double, comments: String, wifiName: String, gps: GeoPoint
    ) {
        localDataSource.updateOpinionComments(opinion, comments, wifiName, gps.longitude, gps.latitude)
    }

    override suspend fun getWifisFromServer(limit: Int): List<WifiBO> {
        return remoteDataSource.getWifisFromServer(limit)?.results.toWifiBOList()
    }
}