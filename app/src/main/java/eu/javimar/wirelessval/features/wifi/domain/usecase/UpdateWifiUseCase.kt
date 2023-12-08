package eu.javimar.wirelessval.features.wifi.domain.usecase

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository
import javax.inject.Inject

class UpdateWifiUseCase @Inject constructor(
    private val repository: IWifiRepository
) {
    fun execute(
        wifi: WifiBO
    ) {
        repository.updateOpinionComments(
            opinion = wifi.opinion,
            comments = wifi.comments,
            wifiName = wifi.wifiName,
            gps = wifi.coordinates
        )
    }
}