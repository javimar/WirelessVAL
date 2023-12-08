package eu.javimar.wirelessval.features.wifi.domain.usecase.wifilisting

import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.util.Resource
import eu.javimar.wirelessval.core.util.UIText
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import eu.javimar.wirelessval.features.wifi.domain.utils.updateWifisList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject

class ReloadFromServerUseCase @Inject constructor(
    private val repository: IWifiRepository,
) {
    fun execute(
        limit: Int = -1,
        orderOptions: WifiOrderOptions,
    ): Flow<Resource<List<WifiBO>>> = flow {

        emit(Resource.Loading())

        try {
            val wifisRemote = repository.getWifisFromServer(limit)
            val existingWifis = repository
                .getAllWifisByOption(orderOptions, GeoPoint(-0.3773900, 39.4697500))
            existingWifis.toMutableList().updateWifisList(wifisRemote)
            repository.deleteAllWifis()
            repository.insertWifis(existingWifis)
            emit(Resource.Success(existingWifis, isFirstLoading = true))
        } catch (e: IOException) {
            emit(Resource.Error(
                message = UIText.StringResource(R.string.err_loading_wifis))
            )
        }
    }.flowOn(Dispatchers.IO)
}