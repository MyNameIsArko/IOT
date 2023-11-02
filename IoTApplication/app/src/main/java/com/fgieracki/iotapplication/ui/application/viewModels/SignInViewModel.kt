package com.fgieracki.iotapplication.ui.application.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fgieracki.iotapplication.data.DefaultRepository
import com.fgieracki.iotapplication.data.Repository
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.data.model.LoginInputFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(private val repository: Repository = DefaultRepository()) : ViewModel() {
    private var USER_TOKEN = "Token"
    private val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

    private fun getToken() {
        val token: String = sharedPreference.getString("USER_TOKEN", "Token")?:"Token"
        USER_TOKEN = token
    }

    val inputFields: MutableStateFlow<LoginInputFields> = MutableStateFlow(LoginInputFields())

    val navChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)

    private val _toastChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastChannel = _toastChannel

    fun changeUsername(newUsername: String) {
        if(newUsername.length > 20) {
            toastChannel.tryEmit("Username cannot be longer than 20 characters")
            return
        }
        inputFields.update { it.copy(username = newUsername) }
    }

    fun changePassword(newPassword: String) {
        if(newPassword.length > 20) {
            toastChannel.tryEmit("Password cannot be longer than 20 characters")
            return
        }
        inputFields.update { it.copy(password = newPassword) }
    }

    fun changePasswordRepeat(newPasswordRepeat: String) {
        inputFields.update { it.copy(passwordRepeat = newPasswordRepeat) }
    }

    private fun clearInputs() {
        inputFields.update { it.copy(username = "", password = "", passwordRepeat = "") }
    }

    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            val resource = repository.login(inputFields.value.username, inputFields.value.password)
            if (resource.data != null) {
                val token = resource.data.token
                token.let {}
                USER_TOKEN = token
                val editor = sharedPreference.edit()
                editor.putString("USER_TOKEN", token)
                editor.apply()

                clearInputs()
                navChannel.tryEmit("OK")
            }

            else {
                toastChannel.emit("Login failed: " + resource.message)
            }
        }
    }

    fun register() {
        if(!validateInputs()) return;

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.register(inputFields.value.username, inputFields.value.password)
            if (response.data != null) {
                toastChannel.tryEmit("Registration successful")
                login()
            } else {
                toastChannel.emit("Registration failed: " + response.message)
            }
        }
    }

    private fun validateInputs(): Boolean {
        return validateUsername() && validatePassword() && validatePasswordRepeat()
    }

    private fun validateUsername(): Boolean {
        val _username = inputFields.value.username

        if(_username.isBlank()) {
            toastChannel.tryEmit("Username cannot be empty")
            return false
        } else if(_username.length < 4) {
            toastChannel.tryEmit("Username must be at least 4 characters long")
            return false
        } else if(_username.length > 20) {
            toastChannel.tryEmit("Username cannot be longer than 20 characters")
            return false
        } else if(_username.any { it.isWhitespace() }) {
            toastChannel.tryEmit("Username cannot contain whitespace")
            return false
        }
        return true
    }

    private fun validatePassword(): Boolean {
        val _password = inputFields.value.password

        if(_password.isBlank()) {
            toastChannel.tryEmit("Password cannot be empty")
            return false
        } else if(_password.length < 8) {
            toastChannel.tryEmit("Password must be at least 8 characters long")
            return false
        } else if(_password.length > 20) {
            toastChannel.tryEmit("Password cannot be longer than 20 characters")
            return false
        } else if(_password.any { it.isWhitespace() }) {
            toastChannel.tryEmit("Password cannot contain whitespace")
            return false
        }
        return true
    }

    private fun validatePasswordRepeat(): Boolean {
        val _password = inputFields.value.password
        val _passwordRepeat = inputFields.value.passwordRepeat

        if(_password != _passwordRepeat) {
            toastChannel.tryEmit("Passwords do not match")
            return false
        }
        return true
    }


}