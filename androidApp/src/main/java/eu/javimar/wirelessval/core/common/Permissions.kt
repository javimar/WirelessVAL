package eu.javimar.wirelessval.core.common

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import eu.javimar.wirelessval.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissions(
    multiplePermissionState: MultiplePermissionsState,
) {
    val context = LocalContext.current
    PermissionsRequired(
        multiplePermissionsState = multiplePermissionState,
        permissionsNotGrantedContent = {

            // content that should be shown after the permission is denied
            if(multiplePermissionState.shouldShowRationale) {
                Toast.makeText(
                    context,
                    stringResource(id = R.string.permission_location),
                    Toast.LENGTH_SHORT).show()
            }
        },
        permissionsNotAvailableContent = { // content that should be shown if the permission is not available
        }
    ) {
        // Composable callback that will be called after the permission is granted
        // Use location
    }
}