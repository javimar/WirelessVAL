package eu.javimar.wirelessval.features.wifi.domain.usecase

import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.util.Resource
import eu.javimar.wirelessval.core.util.UIText
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiLocalRepository
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRemoteRepository
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import eu.javimar.wirelessval.features.wifi.domain.utils.updateWifisList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class ReloadFromServerUseCase(
    private val localRepo: IWifiLocalRepository,
    private val remoteRepo: IWifiRemoteRepository,
) {
    fun execute(
        limit: Int = -1,
        orderOptions: WifiOrderOptions,
    ): Flow<Resource<List<WifiBO>>> = flow {

        emit(Resource.Loading())

        try {
            val wifisRemote = remoteRepo.getWifisFromServer(limit)
            val existingWifis = localRepo
                .getAllWifisByOption(orderOptions, WifiCoordinates(39.4697500, -0.3773900))
            existingWifis.toMutableList().updateWifisList(wifisRemote)
            localRepo.deleteAllWifis()
            localRepo.insertWifis(existingWifis)
            emit(Resource.Success(existingWifis, isFirstLoading = true))
        } catch (e: IOException) {
            emit(Resource.Error(
                message = UIText.StringResource(R.string.err_loading_wifis))
            )
        }
    }.flowOn(Dispatchers.IO)
}