package com.fgieracki.iotapplication.di.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.fgieracki.iotapplication.data.local.ContextCatcher
import kotlinx.coroutines.flow.MutableSharedFlow

class NavbarViewModel() : ViewModel() {
    private val _toastChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastChannel = _toastChannel

    val navChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)

    fun logout() {
        val sharedPreference = ContextCatcher.getContext().getSharedPreferences(
            "USER_DATA",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.clear()
        editor.commit()
        toastChannel.tryEmit("Logged out")
    }
}