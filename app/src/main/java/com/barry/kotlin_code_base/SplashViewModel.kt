package com.barry.kotlin_code_base

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Created by Barry Tu on 2022/12/6.
 */
class SplashViewModelFactory (
    private val owner: SavedStateRegistryOwner,
    private val application: Application
): AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, state: SavedStateHandle) =
        SplashViewModel(state, application) as T
}


class SplashViewModel(val savedStateHandle: SavedStateHandle, val application: Application): ViewModel() {





}