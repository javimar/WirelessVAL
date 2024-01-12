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
            // put limit = -1 to get all wifis
            ktorClient.get("api/explore/v2.1/catalog/datasets/punts-wifi-puntos-wifi/records") {
                parameter("limit", limit)
            }.body<WifisDto>().results.toWifiBOList()
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