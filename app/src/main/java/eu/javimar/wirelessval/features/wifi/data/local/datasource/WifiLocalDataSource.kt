package eu.javimar.wirelessval.features.wifi.data.local.datasource

import eu.javimar.wirelessval.features.wifi.data.mapper.sortByDistance
import eu.javimar.wirelessval.features.wifi.data.mapper.toWifiBO
import eu.javimar.wirelessval.features.wifi.data.mapper.toWifiBOList
import eu.javimar.wirelessval.features.wifi.data.mapper.toWifisList
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiLocalRepository
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import eu.javimar.wirelessval.sqldelight.WirelessVALDatabase

class WifiLocalDataSource (db: WirelessVALDatabase): IWifiLocalRepository {

    private val queries = db.wifis_tableQueries

    override suspend fun insertWifis(wifis: List<WifiBO>) {
        val wifisEntity = wifis.toWifisList()
        queries.transaction {
            wifisEntity.forEach {
                queries.insertWifis(
                    wifiName = it.wifiName,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    comments = it.comments,
                    opinion = it.opinion
                )
            }
        }
    }

    override suspend fun getAllWifis(): List<WifiBO> = queries.getAllWifisByName().executeAsList().toWifiBOList()

    override suspend fun getAllWifisByOption(
        options: WifiOrderOptions, gps: WifiCoordinates
    ): List<WifiBO> {
        return when(options) {
            WifiOrderOptions.NAME -> queries.getAllWifisByName().executeAsList().toWifiBOList()
            WifiOrderOptions.OPINION -> queries.getAllWifisByOpinion().executeAsList().toWifiBOList()
            WifiOrderOptions.EMPTY -> queries.getAllWifisByName().executeAsList().toWifiBOList()
            WifiOrderOptions.DISTANCE -> {
                var wifis = queries.getAllWifisByName().executeAsList().toWifiBOList()
                gps.let {
                    wifis = wifis.sortByDistance(it)
                }
                wifis
            }
        }
    }

    override suspend fun findWifi(name: String, gps: WifiCoordinates): WifiBO? {
        return queries.findWifi(name, gps.longitude, gps.latitude).executeAsOne().toWifiBO()
    }

    override suspend fun countNumberOfRows(): Int = queries.getCountNumberOfRows().executeAsOne().toInt()

    override suspend fun deleteWifi(name: String, gps: WifiCoordinates) {
        queries.deleteWifi(name, gps.longitude, gps.latitude)
    }

    override suspend fun deleteAllWifis() = queries.deleteAllWifis()

    override suspend fun getSearchResults(query: String): List<WifiBO> {
        return queries.getSearchResults(query).executeAsList().toWifiBOList()
    }

    override fun checkIfWifiInDb(name: String, gps: WifiCoordinates): Int {
        return queries.checkIfWifiInDatabase(name, gps.latitude, gps.longitude).executeAsOne().toInt()
    }

    override fun updateOpinionComments(
        opinion: Double, comments: String, wifiName: String, gps: WifiCoordinates
    ) {
        queries.updateOpinionComments(opinion, comments, wifiName, gps.longitude, gps.latitude)
    }
}