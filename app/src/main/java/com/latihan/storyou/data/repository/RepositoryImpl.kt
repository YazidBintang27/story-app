package com.latihan.storyou.data.repository

import com.google.gson.Gson
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.remote.models.DetailStoryResponse
import com.latihan.storyou.data.remote.models.LoginResponse
import com.latihan.storyou.data.remote.models.PostResponse
import com.latihan.storyou.data.remote.models.RegisterResponse
import com.latihan.storyou.data.remote.models.StoriesResponse
import com.latihan.storyou.data.remote.service.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
   private val apiService: ApiService,
   private val authPreferences: AuthPreferences
): Repository {

   private var _loginResponse = MutableStateFlow<LoginResponse?>(null)
   override val loginResponse: StateFlow<LoginResponse?> = _loginResponse.asStateFlow()

   override suspend fun register(name: String, email: String, password: String): RegisterResponse {
      return apiService.register(name, email, password)
   }

   override suspend fun login(email: String, password: String) {
      try {
         val response = apiService.login(email, password)
         _loginResponse.value = response
         response.loginResult?.token?.let { token ->
            authPreferences.saveAuthToken(token)
         }
      } catch (e: HttpException) {
         val errorBody = e.response()?.errorBody()?.string()
         val errorResponse = errorBody?.let {
            Gson().fromJson(it, LoginResponse::class.java)
         } ?: LoginResponse(error = true, message = "Unknown error", loginResult = null)
         _loginResponse.value = errorResponse
      }
   }

   override suspend fun postStories(
      token: String,
      description: RequestBody,
      photo: MultipartBody.Part,
      lat: RequestBody?,
      lon: RequestBody?
   ): PostResponse {
      val authHeader = "Bearer $token"
      return apiService.postStories(authHeader, description, photo, lat, lon)
   }

   override suspend fun postStoriesGuest(
      description: RequestBody,
      photo: MultipartBody.Part,
      lat: RequestBody?,
      lon: RequestBody?
   ): PostResponse {
      return apiService.postStoriesGuest(description, photo, lat, lon)
   }

   override suspend fun getAllStories(token: String): StoriesResponse {
      val authHeader = "Bearer $token"
      return apiService.getAllStories(authHeader)
   }

   override suspend fun getDetailStories(token: String): DetailStoryResponse {
      val authHeader = "Bearer $token"
      return apiService.getDetailStories(authHeader)
   }
}