package com.fgieracki.iotapplication.ui.application.viewModels

var globalUserId = -1

//class SignInViewModel() : ViewModel() {
//
//    private val _toastChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
//    val toastChannel = _toastChannel
//
//    val navChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
//
//
//    val userId = MutableStateFlow<Int>(-1)
//
//    val userToken = MutableStateFlow<String>("Token")
//
//    val username: MutableStateFlow<String> = MutableStateFlow("")
//    val password: MutableStateFlow<String> = MutableStateFlow("")
//
//
//    fun signIn() {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (validateData()) {
//                val response = repository.signIn(username.value, password.value)
//                if (response.isSuccessful) {
//                    userId.value = response.body()!!.userId
//                    userToken.value = "Token " + response.body()!!.token
//                    globalUserId = userId.value
//
//                    val sharedPreference = ContextCatcher.getContext().getSharedPreferences(
//                        "USER_DATA",
//                        Context.MODE_PRIVATE
//                    )
//                    val editor = sharedPreference.edit()
//                    editor.putString("USER_ID", userId.value.toString())
//                    editor.putString("USER_TOKEN", userToken.value)
//                    editor.commit()
//                    globalUserId = userId.value
//
//                    toastChannel.tryEmit("Signed in successfully")
//
//                    navChannel.tryEmit("all_leagues")
//
//                } else {
//                    _toastChannel.emit("Sign in failed")
//                }
//            }
//        }
//    }
//
//    private fun validateData(): Boolean {
//        if(username.value.isEmpty() || password.value.isEmpty()) {
//            _toastChannel.tryEmit("Please fill all fields")
//            return false
//        }
//        return true
//    }
//
//    fun signUp(){
//        if(validateData()){
//            viewModelScope.launch(Dispatchers.IO) {
//                val response = repository.signUp(username.value, password.value)
//                if (response.isSuccessful) {
//                    _toastChannel.emit("Signed up successfully")
//                } else {
//                    _toastChannel.emit("Sign up failed,\n Username already in use!")
//                }
//                clearData()
//            }
//        }
//    }
//
//    fun onUsernameChange(newUsername: String) {
//        username.value = newUsername
//    }
//
//    fun onPasswordChange(newPassword: String) {
//        password.value = newPassword
//    }
//
//    fun clearData() {
//        username.value = ""
//        password.value = ""
//    }
//}