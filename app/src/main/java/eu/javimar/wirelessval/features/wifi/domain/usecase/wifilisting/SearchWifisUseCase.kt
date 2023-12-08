package eu.javimar.wirelessval.features.wifi.domain.usecase.wifilisting

import eu.javimar.wirelessval.core.util.Resource
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchWifisUseCase @Inject constructor(
    private val repository: IWifiRepository
) {
    fun execute(
        query: String
    ): Flow<Resource<List<WifiBO>>> = flow {
        emit(Resource.Loading())
        val wifis = repository.getSearchResults(query)
        emit(Resource.Success(wifis))
    }.flowOn(Dispatchers.IO)
}