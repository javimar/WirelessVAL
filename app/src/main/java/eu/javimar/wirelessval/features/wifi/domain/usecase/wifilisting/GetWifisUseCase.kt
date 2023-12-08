package eu.javimar.wirelessval.features.wifi.domain.usecase.wifilisting

import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.util.Resource
import eu.javimar.wirelessval.core.util.UIText
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject

class GetWifisUseCase @Inject constructor(
    private val repository: IWifiRepository
) {
    fun execute(
        limit: Int = -1,
        orderOptions: WifiOrderOptions,
        gps: GeoPoint
    ): Flow<Resource<List<WifiBO>>> = flow {

        var isFirstLoading = false
        emit(Resource.Loading())

        var fallasLocal = listOf<WifiBO>()

        val isDbEmpty = repository.countNumberOfRows() <= 0

        try {
            if(isDbEmpty) {
                val fallasRemote = repository.getWifisFromServer(limit = limit)
                repository.insertWifis(fallasRemote)
                isFirstLoading = true
            } else {
                fallasLocal = repository.getAllWifisByOption(orderOptions, gps)
                isFirstLoading = false
                emit(Resource.Loading(data = fallasLocal))
            }
        } catch (e: IOException) {
            emit(Resource.Error(
                message = UIText.StringResource(R.string.err_loading_wifis),
                data = fallasLocal))
        }

        val fallas = repository.getAllWifisByOption(orderOptions, gps)

        emit(Resource.Success(fallas, isFirstLoading))

    }.flowOn(Dispatchers.IO)
}