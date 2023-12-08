package eu.javimar.wirelessval.features.wifi.data.local.datasource

import eu.javimar.wirelessval.features.wifi.data.mapper.sortByDistance
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import eu.javimar.wirelessval.sqldelight.Wifis
import eu.javimar.wirelessval.sqldelight.WirelessVALDatabase
import javax.inject.Inject

class WifiLocalDataSource @Inject constructor(db: WirelessVALDatabase) {

    private val queries = db.wifis_tableQueries

    fun insertWifis(wifis: List<Wifis>) {
        queries.transaction {
            wifis.forEach {
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

    fun getAllWifis(): List<Wifis> = queries.getAllWifisByName().executeAsList()

    fun getAllFallasByOption(
        options: WifiOrderOptions,
        gps: GeoPoint?
    ): List<Wifis> {
        return when(options) {
            WifiOrderOptions.NAME -> queries.getAllWifisByName().executeAsList()
            WifiOrderOptions.OPINION -> queries.getAllWifisByOpinion().executeAsList()
            WifiOrderOptions.EMPTY -> queries.getAllWifisByName().executeAsList()
            WifiOrderOptions.DISTANCE -> {
                var fallas = queries.getAllWifisByName().executeAsList()
                gps?.let {
                    fallas = fallas.sortByDistance(it)
                }
                fallas
            }
        }
    }

    fun findWifi(name: String, longitude: Double, latitude: Double): Wifis =
        queries.findWifi(name, longitude, latitude).executeAsOne()

    fun countNumberOfRows(): Int = queries.getCountNumberOfRows().executeAsOne().toInt()

    fun deleteWifi(name: String, longitude: Double, latitude: Double) =
        queries.deleteWifi(name, longitude, latitude)

    fun deleteAllWifis() = queries.deleteAllWifis()

    fun getSearchResults(query: String): List<Wifis> {
        return queries.getSearchResults(query).executeAsList()
    }

    fun updateOpinionComments(opinion: Double, comments: String, wifiName: String, longitude: Double, latitude: Double) {
        queries.updateOpinionComments(opinion, comments, wifiName, longitude, latitude)
    }

    fun checkIfWifiInDb(name: String, longitude: Double, latitude: Double): Int =
        queries.checkIfWifiInDatabase(name, latitude, longitude).executeAsOne().toInt()
}