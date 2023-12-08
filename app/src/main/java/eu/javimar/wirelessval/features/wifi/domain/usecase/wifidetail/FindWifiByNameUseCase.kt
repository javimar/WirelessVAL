package eu.javimar.wirelessval.features.wifi.domain.usecase.wifidetail

import eu.javimar.wirelessval.core.util.Resource
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FindWifiByNameUseCase @Inject constructor(
    private val repository: IWifiRepository
) {
    fun execute(
        name: String,
        gps: GeoPoint
    ): Flow<Resource<WifiBO>> = flow {
        emit(Resource.Loading())
        val falla = repository.findWifi(name, gps)
        emit(Resource.Success(falla))
    }.flowOn(Dispatchers.IO)
}