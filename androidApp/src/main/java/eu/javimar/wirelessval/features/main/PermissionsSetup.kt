package eu.javimar.wirelessval.features.main

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import eu.javimar.wirelessval.core.common.LocationPermissions
import eu.javimar.wirelessval.core.common.hasLocationPermission

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsSetup() {
    val context = LocalContext.current

    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION
        )
    )
    LocationPermissions(
        multiplePermissionState = multiplePermissionState,
    )

    SideEffect {
        if (!context.hasLocationPermission()) {
            multiplePermissionState.launchMultiplePermissionRequest()
        }
    }
}