package eu.javimar.wirelessval.core.common

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import eu.javimar.wirelessval.R

// PermissionsRequired() se retiro de accompanist-permissions en versiones posteriores a la
// 0.23.1 (bump a 0.37.3 con la migracion AGP9/Kotlin2.4). Mismo comportamiento reescrito a
// mano con el estado ya expuesto por MultiplePermissionsState: toast de rationale cuando el
// permiso no esta concedido y toca explicar por que hace falta.
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissions(
    multiplePermissionState: MultiplePermissionsState,
) {
    val context = LocalContext.current
    if (!multiplePermissionState.allPermissionsGranted && multiplePermissionState.shouldShowRationale) {
        Toast.makeText(
            context,
            stringResource(id = R.string.permission_location),
            Toast.LENGTH_SHORT).show()
    }
}