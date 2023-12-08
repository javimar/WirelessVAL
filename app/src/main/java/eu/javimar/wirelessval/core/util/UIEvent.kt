package eu.javimar.wirelessval.core.util

// Send EVENTS from the VM to the UI
sealed interface UIEvent {
    data object PopBackStack: UIEvent
    data class Navigate(val route: String): UIEvent
    data class ShowSnackbar(val message: UIText, val action: String? = null): UIEvent
}
