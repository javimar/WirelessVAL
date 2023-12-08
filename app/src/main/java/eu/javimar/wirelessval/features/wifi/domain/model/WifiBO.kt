package eu.javimar.wirelessval.features.wifi.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint

data class WifiBO(
    val wifiName: String,
    val coordinates: GeoPoint,
    val comments: String,
    val opinion: Double,
): ClusterItem {

    override fun getPosition(): LatLng = LatLng(coordinates.latitude, coordinates.longitude)
    override fun getTitle(): String = wifiName
    override fun getSnippet(): String = coordinates.toString()
    override fun getZIndex(): Float? = null
}