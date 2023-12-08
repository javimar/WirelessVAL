package eu.javimar.wirelessval.features.wifi.data.mapper

import eu.javimar.wirelessval.features.wifi.domain.model.WifiBO
import eu.javimar.wirelessval.sqldelight.Wifis

fun List<WifiBO>?.toFallasList(): List<Wifis> =
    this?.map { it.toWifis() }?.toList() ?: run {
        listOf()
    }

fun WifiBO.toWifis(): Wifis =
    Wifis(
        wifiName = wifiName,
        latitude = coordinates.latitude,
        longitude = coordinates.longitude,
        comments = comments,
        opinion = opinion
    )