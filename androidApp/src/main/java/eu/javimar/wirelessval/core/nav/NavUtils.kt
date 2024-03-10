package eu.javimar.wirelessval.core.nav

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json

inline fun <reified T: java.io.Serializable> NavType.Companion.serializableTypeOf() = object: NavType<T>(isNullableAllowed = true) {
    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putSerializable(key, value)
    }
    override fun get(bundle: Bundle, key: String): T {
        return bundle.getSerializable(key) as T
    }
    override fun parseValue(value: String): T {
        return Json.decodeFromString(value)
    }
}