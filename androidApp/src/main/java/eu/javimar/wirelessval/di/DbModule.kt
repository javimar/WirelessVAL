package eu.javimar.wirelessval.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import eu.javimar.wirelessval.sqldelight.WirelessVALDatabase

interface IDbModule {
    val db: WirelessVALDatabase
}

class DbModule(
    private val context: Context
): IDbModule {
    override val db: WirelessVALDatabase by lazy {
        val driver: SqlDriver = AndroidSqliteDriver(
            schema = WirelessVALDatabase.Schema,
            context = context,
            name = "wifis_vlc.db"
        )
        WirelessVALDatabase(driver)
    }
}