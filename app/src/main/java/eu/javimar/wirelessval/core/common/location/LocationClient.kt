package eu.javimar.wirelessval.core.common.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import eu.javimar.myfallasguide.core.location.ILocationClient
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.hasLocationEnabled
import eu.javimar.wirelessval.core.common.hasLocationPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
): ILocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {

        return callbackFlow {
            if(!context.hasLocationPermission()) {
                throw ILocationClient.LocationException(message = context.getString(R.string.permission_location))
            }

            if(!context.hasLocationEnabled()) {
                withContext(Dispatchers.Main) {
                    throw ILocationClient.LocationException(message = context.getString(R.string.permission_gps))
                }
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(interval)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch {
                            send(location)
                        }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}