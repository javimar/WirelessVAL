package eu.javimar.wirelessval.features.wifi.domain.usecase

import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.util.Resource
import eu.javimar.wirelessval.core.util.UIText
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiLocalRepository
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRemoteRepository
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class GetWifisUseCase (
    private val localRepo: IWifiLocalRepository,
    private val remoteRepo: IWifiRemoteRepository,
) {
    fun execute(
        limit: Int = -1,
        orderOptions: WifiOrderOptions,
        gps: WifiCoordinates
    ): Flow<Resource<List<WifiBO>>> = flow {

        var isFirstLoading = false
        emit(Resource.Loading())

        var wifisLocal = listOf<WifiBO>()

        val isDbEmpty = localRepo.countNumberOfRows() <= 0

        try {
            if(isDbEmpty) {
                val wifisRemote = remoteRepo.getWifisFromServer(limit = limit)
                localRepo.insertWifis(wifisRemote)
                isFirstLoading = true
            } else {
                wifisLocal = localRepo.getAllWifisByOption(orderOptions, gps)
                isFirstLoading = false
                emit(Resource.Loading(data = wifisLocal))
            }
        } catch (e: IOException) {
            emit(Resource.Error(
                message = UIText.StringResource(R.string.err_loading_wifis),
                data = wifisLocal))
        }

        val wifis = localRepo.getAllWifisByOption(orderOptions, gps)

        emit(Resource.Success(wifis, isFirstLoading))

    }.flowOn(Dispatchers.IO)
}