package eu.javimar.wirelessval.core.common.location

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import eu.javimar.wirelessval.features.settings.domain.repository.IPreferencesRepository
import eu.javimar.wirelessval.features.settings.domain.utils.SharePrefsKeys.LOCATION_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService: Service() {

    @Inject
    lateinit var sharePrefs: IPreferencesRepository

    private val serviceScope = CoroutineScope((SupervisorJob() + Dispatchers.IO))
    private lateinit var locationClient: ILocationClient

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        locationClient.getLocationUpdates(60000L) // every minute
            .catch {
                e -> e.printStackTrace()
            }
            .onEach {
                sharePrefs.setStringStoredValue(
                    LOCATION_KEY, "${it.latitude},${it.longitude}"
                )
            }.launchIn(serviceScope)
    }

    private fun stop() {
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onBind(i: Intent?): IBinder? { return null }
}