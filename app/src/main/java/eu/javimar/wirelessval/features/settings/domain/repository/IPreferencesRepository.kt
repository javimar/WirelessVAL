package eu.javimar.wirelessval.features.settings.domain.repository

import kotlinx.coroutines.flow.Flow

interface IPreferencesRepository {

    fun setStringStoredValue(
        key: String,
        value: String
    )

    fun setBooleanValue(
        key: String,
        value: Boolean
    )

    fun readBooleanValue(
        key: String,
    ): Boolean

    suspend fun readIsDynamicColorValue(): Flow<Boolean>

    suspend fun readIsLigthColorValue(): Flow<Boolean>

    fun readStringStoredValue(
        key: String,
    ): String

    suspend fun readLocationStoredValue(
        key: String,
    ): Flow<String>

    fun clearValueFromPrefs(
        key: String
    )
}