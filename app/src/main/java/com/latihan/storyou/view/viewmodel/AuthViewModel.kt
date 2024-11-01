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
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
   private val repository: Repository,
   private val authPreferences: AuthPreferences
): ViewModel() {
   val loginResponse: StateFlow<LoginResponse?> = repository.loginResponse

   private var _registerResponse = MutableStateFlow<RegisterResponse?>(null)
   val registerResponse: StateFlow<RegisterResponse?> = _registerResponse.asStateFlow()

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
      viewModelScope.launch {
        repository.login(email, password)
      }
   }

   fun logout() {
      viewModelScope.launch {
         authPreferences.clearSession()
         Log.d("Logout", "${isLoggedIn.collectLatest {  }}")
      }
   }
}