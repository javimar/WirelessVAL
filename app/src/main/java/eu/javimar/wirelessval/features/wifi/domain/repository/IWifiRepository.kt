package eu.javimar.wirelessval.features.wifi.domain.repository

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions

interface IWifiRepository {
    suspend fun insertWifis(wifis: List<WifiBO>)
    suspend fun getAllWifis(): List<WifiBO>
    suspend fun getAllWifisByOption(options: WifiOrderOptions, gps: GeoPoint): List<WifiBO>
    suspend fun findWifi(name: String, gps: GeoPoint): WifiBO?
    suspend fun countNumberOfRows(): Int
    suspend fun deleteWifi(name: String, gps: GeoPoint)
    suspend fun deleteAllWifis()
    suspend fun getWifisFromServer(limit: Int): List<WifiBO>
    suspend fun getSearchResults(query: String): List<WifiBO>
    fun checkIfWifiInDb(name: String, gps: GeoPoint): Int
    fun updateOpinionComments(opinion: Double, comments: String, wifiName: String, gps: GeoPoint)
}