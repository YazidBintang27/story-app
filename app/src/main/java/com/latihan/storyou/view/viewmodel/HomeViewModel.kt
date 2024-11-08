package com.latihan.storyou.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.remote.models.StoriesResponse
import com.latihan.storyou.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
   private val repository: Repository,
   private val authPreferences: AuthPreferences
): ViewModel() {

   private val authToken: Flow<String?> = authPreferences.authToken

   private var _storiesResponse = MutableStateFlow<StoriesResponse?>(null)
   val storiesResponse: StateFlow<StoriesResponse?> = _storiesResponse.asStateFlow()

   private var _isLoading = MutableStateFlow<Boolean>(true)
   val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

   init {
      getAllStories()
   }

   private fun getAllStories() {
      viewModelScope.launch {
         authToken.collect { token ->
            Log.d("CheckToken", "$token")
            token?.let {
               try {
                  val response = repository.getAllStories(token)
                  _storiesResponse.value = response
                  _isLoading.value = false
               } catch (e: HttpException) {
                  val errorBody = e.response()?.errorBody()?.string()
                  val errorResponse = errorBody?.let {
                     Gson().fromJson(it, StoriesResponse::class.java)
                  } ?: StoriesResponse(error = true, message = "Unknown error", listStory = null)
                  _storiesResponse.value = errorResponse
               }
            }
         }
      }
   }
}