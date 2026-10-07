package com.overandoutnerd.deviceinfo.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ContextViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(Context::class.java)
                .newInstance(context)
        } catch (e: Exception) {
            throw IllegalArgumentException("Unknown View Model Class", e)
        }
    }

}