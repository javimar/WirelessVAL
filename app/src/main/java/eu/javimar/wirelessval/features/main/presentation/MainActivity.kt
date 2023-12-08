/**
 * Wireless Valencia
 *
 * @author Javier Martín
 * @email: javimardeveloper@gmail.com
 * @link http://www.javimar.eu
 * @package eu.javimar.wirelessval
 * @version 3.0.0
 *
BSD 3-Clause License

Copyright (c) 2016, 2023 Javier Martín
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

 * Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package eu.javimar.wirelessval.features.main.presentation

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.changeStatusBarColor
import eu.javimar.wirelessval.core.common.hasLocationPermission
import eu.javimar.wirelessval.core.common.location.LocationService
import eu.javimar.wirelessval.core.nav.RootNavGraph
import eu.javimar.wirelessval.features.main.PermissionsSetup
import eu.javimar.wirelessval.ui.theme.WirelessValTheme

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val context = LocalContext.current
            val connected = viewModel.connected.collectAsState().value

            LaunchedEffect(key1 = connected) {
                if(!connected) {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.no_internet_connection),
                        duration = SnackbarDuration.Indefinite,
                        withDismissAction = true
                    )
                } else {
                    snackbarHostState
                        .currentSnackbarData?.dismiss()
                }
            }

            WirelessValTheme(
                darkTheme = viewModel.colorState.isDark,
                dynamicColor = viewModel.colorState.isDynamic,
            ) {
                val surfaceColor = MaterialTheme.colorScheme.surface
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LaunchedEffect(surfaceColor) {
                        (context as Activity).changeStatusBarColor(
                            color = surfaceColor,
                            isAppearanceLightStatusBars = viewModel.getColorMode(),
                        )
                    }
                    PermissionsSetup()
                    RootNavGraph(
                        navHostController = rememberNavController(),
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startService()
    }

    override fun onPause() {
        super.onPause()
        stopService()
    }

    private fun startService() {
        if(hasLocationPermission()) {
            Intent(this, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }
    }

    private fun stopService() {
        if(hasLocationPermission()) {
            Intent(this, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
    }
}