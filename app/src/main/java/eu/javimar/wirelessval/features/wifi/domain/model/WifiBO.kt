package eu.javimar.wirelessval.features.wifi.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import eu.javimar.wirelessval.features.wifi.data.mapper.toLatLng
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiCoordinates
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WifiBO(
    val wifiName: String,
    val coordinates: WifiCoordinates,
    val comments: String,
    val opinion: Double,
): java.io.Serializable, ClusterItem {
    // android-maps-utils 5.0.0 cambio ClusterItem de metodos Java (getPosition()...) a
    // propiedades Kotlin (position, title...): al pasar a 'val' entran en el descriptor de
    // kotlinx.serialization y LatLng no tiene serializador. @Transient las excluye, igual
    // que antes quedaban fuera por ser funciones (getPosition()) y no propiedades.
    @Transient
    override val position: LatLng = coordinates.toLatLng()
    @Transient
    override val title: String = wifiName
    @Transient
    override val snippet: String = coordinates.toString()
    @Transient
    override val zIndex: Float? = null
}