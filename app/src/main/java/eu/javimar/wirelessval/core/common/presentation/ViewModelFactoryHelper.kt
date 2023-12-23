package eu.javimar.wirelessval.core.common.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

fun <VM: ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}

fun <VM: ViewModel> viewModelFactoryExtras(initializer: (savedStateHandle: SavedStateHandle) -> VM): ViewModelProvider.Factory {
    return object: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()
            return initializer(savedStateHandle) as T
        }
    }
}