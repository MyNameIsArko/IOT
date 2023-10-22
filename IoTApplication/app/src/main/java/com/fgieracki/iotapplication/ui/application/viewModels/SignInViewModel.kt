package com.fgieracki.iotapplication.ui.application.viewModels

import androidx.lifecycle.ViewModel
import com.fgieracki.iotapplication.data.model.LoginInputFields
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel() : ViewModel() {
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
        //TODO: process data
        clearInputs()
        navChannel.tryEmit("OK")
    }

    fun register() {
        if(!validateInputs()) return;
        //TODO: register

        login()
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