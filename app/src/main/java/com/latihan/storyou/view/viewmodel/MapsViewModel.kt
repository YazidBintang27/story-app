package com.latihan.storyou.view.viewmodel

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
class MapsViewModel @Inject constructor(
   private val repository: Repository,
   private val authPreferences: AuthPreferences
): ViewModel() {
   private val authToken: Flow<String?> = authPreferences.authToken

   private var _allStoriesLocation = MutableStateFlow<StoriesResponse?>(null)
   val allStoriesLocation: StateFlow<StoriesResponse?> = _allStoriesLocation.asStateFlow()

   init {
      getAllStoriesLocation()
   }

   private fun getAllStoriesLocation() {
      viewModelScope.launch {
         authToken.collect { token ->
            token?.let {
               try {
                  val response = repository.getAllStoriesLocation(token)
                  _allStoriesLocation.value = response
               } catch (e: HttpException) {
                  val errorBody = e.response()?.errorBody()?.string()
                  val errorResponse = errorBody?.let {
                     Gson().fromJson(it, StoriesResponse::class.java)
                  } ?: StoriesResponse(error = true, message = "Unknown error", listStory = null)
                  _allStoriesLocation.value = errorResponse
               }
            }
         }
      }
   }
}