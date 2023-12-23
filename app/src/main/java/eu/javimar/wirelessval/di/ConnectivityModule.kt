package eu.javimar.wirelessval.di

import android.content.Context
import eu.javimar.wirelessval.core.common.connectivity.ConnectivityObserver
import eu.javimar.wirelessval.core.common.connectivity.NetworkConnectivityObserver

interface IConnectivityModule {
    val connectivity: ConnectivityObserver
}

class ConnectivityModule(
    private val context: Context
): IConnectivityModule {
    override val connectivity: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(context)
    }
}