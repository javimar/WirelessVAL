package eu.javimar.wirelessval.features.wifi.domain.repository

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions

interface IWifiRepository {
    suspend fun insertWifis(wifis: List<WifiBO>)
    suspend fun getAllWifis(): List<WifiBO>
    suspend fun getAllWifisByOption(options: WifiOrderOptions, gps: WifiCoordinates): List<WifiBO>
    suspend fun findFalla(name: String): WifiBO?
    suspend fun countNumberOfRows(): Int
    suspend fun deleteWifi(name: String)
    suspend fun deleteAllWifis()
    suspend fun getWifisFromServer(query: String, rows: Int): List<WifiBO>
    suspend fun getSearchResults(query: String): List<WifiBO>
}