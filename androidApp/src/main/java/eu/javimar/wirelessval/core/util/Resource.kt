package eu.javimar.wirelessval.core.util

sealed class Resource<T>(
    val data: T? = null,
    val message: UIText? = null,
    val isFirstLoading: Boolean = false
) {
    class Loading<T>(data: T? = null): Resource<T>(data)
    class Success<T>(data: T?, isFirstLoading: Boolean = false): Resource<T>(data, null, isFirstLoading)
    class Error<T>(message: UIText, data: T? = null): Resource<T>(data, message)
}