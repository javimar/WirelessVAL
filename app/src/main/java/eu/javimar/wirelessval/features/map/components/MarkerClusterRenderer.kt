package eu.javimar.wirelessval.features.map.components

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO

class MarkerClusterRender<T : ClusterItem>(
    var context: Context,
    private var googleMap: GoogleMap,
    clusterManager: ClusterManager<T>,
    var onInfoWindowClick: (WifiBO) -> Unit
) :
    DefaultClusterRenderer<T>(context, googleMap, clusterManager) {

    private var clusterMap: HashMap<String, Marker> = hashMapOf()

    override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {
        return cluster.size > 1
    }

    override fun getBucket(cluster: Cluster<T>): Int {
        return cluster.size
    }

    override fun getClusterText(bucket: Int): String {
        return super.getClusterText(bucket).replace("+", "")
    }

    override fun onClusterItemRendered(clusterItem: T, marker: Marker) {
        super.onClusterItemRendered(clusterItem, marker)
        clusterMap[(clusterItem as WifiBO).wifiName] = marker

        setMarker((clusterItem as WifiBO), marker)
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setMarker(poi: WifiBO, marker: Marker?) {
        marker?.let {
            it.tag = poi
            it.snippet = poi.coordinates.toString()
            it.setIcon(setIconColor(poi))
        }
        googleMap.setOnInfoWindowClickListener {
            it.tag?.let { tag ->
                onInfoWindowClick(tag as WifiBO)
            }
        }
    }

    private fun getClusterMarker(itemId: String): Marker? {
        return if (clusterMap.containsKey(itemId)) clusterMap[itemId]
        else null
    }

    fun showRouteInfoWindow(key: String) {
        getClusterMarker(key)?.showInfoWindow()
    }

    private fun setIconColor(wifi: WifiBO): BitmapDescriptor {
        return when (wifi.opinion) {
            in 0.0..0.9  -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            in 1.0..1.9 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            in 2.0..2.9 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
            in 3.0..3.9 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            in 4.0..4.9 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            5.0 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }
    }
}