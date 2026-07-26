package eu.javimar.wirelessval.features.wifi.data.remote.datasource

import eu.javimar.wirelessval.core.common.loge
import eu.javimar.wirelessval.features.wifi.data.mapper.toWifiBOList
import eu.javimar.wirelessval.features.wifi.data.remote.dto.WifisDto
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRemoteRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WifiRemoteDataSource(private val ktorClient: HttpClient): IWifiRemoteRepository {
    override suspend fun getWifisFromServer(limit: Int): List<WifiBO> {
        return try {
            // ArcGIS Feature Service (geoportal.valencia.es): where=1=1 sin filtro,
            // outSR=4326 fuerza WGS84 (lat/lng) en vez del UTM 30N nativo de la capa.
            // limit = -1 (convención de la app) = traer todas: no se manda resultRecordCount
            // y el servidor usa su propio default, que cubre de sobra los ~310 wifis
            // reales (por debajo del maxRecordCount=2000 de la capa). Solo se manda
            // resultRecordCount cuando el llamante pide un límite positivo concreto.
            ktorClient.get("query") {
                parameter("where", "1=1")
                parameter("outFields", "*")
                parameter("outSR", "4326")
                parameter("f", "json")
                if (limit > 0) {
                    parameter("resultRecordCount", limit)
                }
            }.body<WifisDto>().features.toWifiBOList()
        } catch (e: RedirectResponseException) {
            // 3xx responses
            loge("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ClientRequestException) {
            // 4xx responses
            loge("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ServerResponseException) {
            // 5xx responses
            loge("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: Exception) {
            loge("Error: ${e.message}")
            emptyList()
        }
    }
}