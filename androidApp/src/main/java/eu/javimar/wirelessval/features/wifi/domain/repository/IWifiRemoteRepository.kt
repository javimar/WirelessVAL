package eu.javimar.wirelessval.features.wifi.domain.repository

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO

interface IWifiRemoteRepository {
    suspend fun getWifisFromServer(limit: Int): List<WifiBO>
}