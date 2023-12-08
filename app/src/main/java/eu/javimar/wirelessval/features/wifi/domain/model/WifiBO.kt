package eu.javimar.wirelessval.features.wifi.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import eu.javimar.wirelessval.features.wifi.data.mapper.toLatLng
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import kotlinx.serialization.Serializable

@Serializable
data class WifiBO(
    val wifiName: String,
    val coordinates: WifiCoordinates,
    val comments: String,
    val opinion: Double,
): java.io.Serializable, ClusterItem {
    override fun getPosition(): LatLng = coordinates.toLatLng()
    override fun getTitle(): String = wifiName
    override fun getSnippet(): String = coordinates.toString()
    override fun getZIndex(): Float? = null
}