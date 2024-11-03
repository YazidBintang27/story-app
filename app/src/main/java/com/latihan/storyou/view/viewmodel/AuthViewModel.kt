package com.latihan.storyou.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.remote.models.LoginResponse
import com.latihan.storyou.data.remote.models.RegisterResponse
import com.latihan.storyou.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
   private val repository: Repository,
   private val authPreferences: AuthPreferences
): ViewModel() {

   private var _registerResponse = MutableStateFlow<RegisterResponse?>(null)
   val registerResponse: StateFlow<RegisterResponse?> = _registerResponse.asStateFlow()

   private var _loginResponse = MutableStateFlow<LoginResponse?>(null)
   val loginResponse: StateFlow<LoginResponse?> = _loginResponse.asStateFlow()

   val savedLoginResponse: Flow<LoginResponse?> = authPreferences.loginResponse

   val isLoggedIn: Flow<Boolean> = authPreferences.isLoggedIn

   fun register(name: String, email: String, password: String) {
      viewModelScope.launch(Dispatchers.IO) {
         try {
            val response = repository.register(name, email, password)
            _registerResponse.value = response
         } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = errorBody?.let {
               Gson().fromJson(it, RegisterResponse::class.java)
            } ?: RegisterResponse(error = true, message = "Unknown error")
            Log.d("RegisterViewModel", "${e.message}")
            _registerResponse.value = errorResponse
         }
      }
   }

   fun login(email: String, password: String) {
      viewModelScope.launch(Dispatchers.IO) {
         try {
            val response = repository.login(email, password)
            _loginResponse.value = response
            Log.d("CheckLoginResponse", "ViewModel received response: ${response.loginResult?.userId}")
            val saveLoginResponseJob = launch {
               authPreferences.saveLoginResponse(response)
               Log.d("CheckLoginResponse", "Login response saved successfully.")
            }
            saveLoginResponseJob.join()
            response.loginResult?.token?.let { token ->
               Log.d("CheckToken", "Saving token: $token")
               authPreferences.saveAuthToken(token)
               Log.d("CheckToken", "Token saved successfully.")
            }
         } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = errorBody?.let {
               Gson().fromJson(it, LoginResponse::class.java)
            } ?: LoginResponse(error = true, message = "Unknown error", loginResult = null)
            _loginResponse.value = errorResponse
            Log.e("LoginError", "HTTP error: ${e.message}")
         } catch (e: Exception) {
            Log.e("LoginError", "Unexpected error: ${e.localizedMessage}")
            _loginResponse.value = LoginResponse(error = true, message = "Unexpected error", loginResult = null)
         }
      }
   }

   fun logout() {
      viewModelScope.launch {
         authPreferences.clearSession()
         Log.d("Logout", "${isLoggedIn.collectLatest {  }}")
      }
   }

   fun clearLoginResponse() {
      _loginResponse.value = null
   }

   fun clearRegisterResponse() {
      _registerResponse.value = null
   }
}