package eu.javimar.wirelessval.core.nav.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import eu.javimar.wirelessval.R

sealed class Screens(
    val route: String,
    @StringRes val resourceId: Int = 0,
    @DrawableRes val icon: Int,
) {
    data object Home: Screens(route = HOME_DEST, R.string.nav_home, R.drawable.ic_list)
    data object Map: Screens(route = MAP_DEST, R.string.nav_map, R.drawable.ic_location)
    data object About: Screens(route = ABOUT_DEST, R.string.nav_info, R.drawable.ic_info_about)
    data object Detail: Screens(DETAIL_DEST, R.string.nav_info, R.drawable.ic_info_about)
    data object Settings: Screens(SETTINGS_DEST, R.string.menu_settings, R.drawable.ic_settings)
}

const val HOME_DEST = "home_dest"
const val MAP_DEST = "map_dest"
const val ABOUT_DEST = "about_dest"
const val DETAIL_DEST = "detail_wifi_dest"
const val SETTINGS_DEST = "settings_dest"
