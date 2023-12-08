package eu.javimar.wirelessval.features.wifi.domain.usecase.wifilisting

import eu.javimar.wirelessval.features.wifi.domain.repository.IWifiRepository
import javax.inject.Inject

class GetWifis @Inject constructor(
    private val repository: IWifiRepository
) {
    fun execute() {

    }
}