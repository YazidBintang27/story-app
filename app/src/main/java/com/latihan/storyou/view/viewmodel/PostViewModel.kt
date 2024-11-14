package com.latihan.storyou.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.remote.models.PostResponse
import com.latihan.storyou.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
   private val repository: Repository,
   private val authPreferences: AuthPreferences
): ViewModel() {
   private val authToken: Flow<String?> = authPreferences.authToken

   private var _postResponse = MutableStateFlow<PostResponse?>(null)
   val postResponse: StateFlow<PostResponse?> = _postResponse.asStateFlow()

   private var _isLoading = MutableStateFlow<Boolean?>(null)
   val isLoading: StateFlow<Boolean?> = _isLoading.asStateFlow()

   fun postStories(description: RequestBody, photo: MultipartBody.Part, lat: RequestBody, lon: RequestBody) {
      viewModelScope.launch {
         authToken.collect { token ->
            token?.let {
               try {
                  _isLoading.value = true
                  val response = repository.postStories(token, description, photo, lat, lon)
                  _postResponse.value = response
               } catch (e: HttpException) {
                  val errorBody = e.response()?.errorBody()?.string()
                  val errorResponse = errorBody?.let {
                     Gson().fromJson(it, PostResponse::class.java)
                  } ?: PostResponse(error = true, message = "Unknown error")
                  Log.d("PostViewModel", "${e.message}")
                  _postResponse.value = errorResponse
               } finally {
                  _isLoading.value = false
               }
            }
         }
      }
   }

   fun clearPostResponse() {
      _postResponse.value = null
   }
}