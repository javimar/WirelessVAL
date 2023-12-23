package eu.javimar.wirelessval.features.wifi.domain.repository

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions

interface IWifiLocalRepository {
    suspend fun insertWifis(wifis: List<WifiBO>)
    suspend fun getAllWifis(): List<WifiBO>
    suspend fun getAllWifisByOption(options: WifiOrderOptions, gps: WifiCoordinates): List<WifiBO>
    suspend fun findWifi(name: String, gps: WifiCoordinates): WifiBO?
    suspend fun countNumberOfRows(): Int
    suspend fun deleteWifi(name: String, gps: WifiCoordinates)
    suspend fun deleteAllWifis()
    suspend fun getSearchResults(query: String): List<WifiBO>
    fun checkIfWifiInDb(name: String, gps: WifiCoordinates): Int
    fun updateOpinionComments(opinion: Double, comments: String, wifiName: String, gps: WifiCoordinates)
}