package com.latihan.storyou.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.remote.models.DetailStoryResponse
import com.latihan.storyou.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
   private val repository: Repository,
   private val authPreferences: AuthPreferences
): ViewModel() {
   private val authToken = authPreferences.authToken

   private var _detailResponse = MutableStateFlow<DetailStoryResponse?>(null)
   val detailResponse: StateFlow<DetailStoryResponse?> = _detailResponse.asStateFlow()

   private var _isLoading = MutableStateFlow<Boolean?>(null)
   val isLoading: StateFlow<Boolean?> = _isLoading.asStateFlow()

   fun getDetailStory(id: String) {
      viewModelScope.launch {
         authToken.collect { token ->
            token?.let {
               try {
                  _isLoading.value = true
                  val response = repository.getDetailStories(token, id)
                  _detailResponse.value = response
               } catch (e: HttpException) {
                  val errorBody = e.response()?.errorBody()?.string()
                  val errorResponse = errorBody?.let {
                     Gson().fromJson(it, DetailStoryResponse::class.java)
                  } ?: DetailStoryResponse(error = true, message = "Unknown error", story = null)
                  Log.d("PostViewModel", "${e.message}")
                  _detailResponse.value = errorResponse
               } finally {
                  _isLoading.value = false
               }
            }
         }
      }
   }
}