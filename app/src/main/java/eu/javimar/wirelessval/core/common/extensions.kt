package eu.javimar.wirelessval.core.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavHostController.currentRoute(): String {
    val navBackStackEntry by currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route.toString()
}

@Composable
fun Dp.toSp() = with(LocalDensity.current) { toSp() }

fun Context.hasLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return !(!isGpsEnabled && !isNetworkEnabled)
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

fun Context.openLocationSettings() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.openAppDetailSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = Uri.fromParts("package", packageName,null)
    intent.data = uri
    startActivity(intent)
}

private fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

private fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}

fun Activity.changeStatusBarColor(
    color: Color,
    isAppearanceLightStatusBars: Boolean,
) {
    val backgroundArgb = color.toArgb()
    window.statusBarColor = backgroundArgb
    val wic = WindowCompat.getInsetsController(window, window.decorView)
    wic.isAppearanceLightStatusBars = isAppearanceLightStatusBars
}

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun logd(str: String) {
    Log.d("${"".TAG} - JAVIER "  , str)
}
fun logi(str: String) {
    Log.i("${"".TAG} - JAVIER "  , str)
}
fun loge(str: String) {
    Log.e("${"".TAG} - JAVIER "  , str)
}

fun Long.toBoolean(): Boolean = this == 1L
fun Int.toBoolean(): Boolean = this == 1
fun Boolean.toLong(): Long = if (this) 1L else 0L
fun Boolean.toInt(): Int = if (this) 1 else 0