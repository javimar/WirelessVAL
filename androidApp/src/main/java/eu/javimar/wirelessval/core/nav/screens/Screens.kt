package eu.javimar.wirelessval.core.nav.screens

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

sealed class Screens(
    val route: String,
    @StringRes val resourceId: Int = 0,
    @DrawableRes val icon: Int,
) {
    data object Home: Screens(route = HOME_DEST, R.string.nav_home, R.drawable.ic_list)
    data object Map: Screens(route = MAP_DEST, R.string.nav_map, R.drawable.ic_location)
    data object About: Screens(route = ABOUT_DEST, R.string.nav_info, R.drawable.ic_info_about)
    data object Detail: Screens(route = "$DETAIL_DEST/{${WifiNavArgs.Wifi.key}}",  R.string.nav_info, R.drawable.ic_info_about) {
        fun createRoute(wifi: WifiBO) = "$DETAIL_DEST/${Uri.encode(Json.encodeToJsonElement(wifi).toString())}"
    }
    data object Settings: Screens(SETTINGS_DEST, R.string.menu_settings, R.drawable.ic_settings)
}

enum class WifiNavArgs(val key: String) {
    Wifi("wifi")
}

const val HOME_DEST = "home_dest"
const val MAP_DEST = "map_dest"
const val ABOUT_DEST = "about_dest"
const val DETAIL_DEST = "detail_wifi_dest"
const val SETTINGS_DEST = "settings_dest"
