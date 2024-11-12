package com.latihan.storyou.data.repository

import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.remote.models.DetailStoryResponse
import com.latihan.storyou.data.remote.models.LoginResponse
import com.latihan.storyou.data.remote.models.PostResponse
import com.latihan.storyou.data.remote.models.RegisterResponse
import com.latihan.storyou.data.remote.models.StoriesResponse
import com.latihan.storyou.data.remote.service.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
   private val apiService: ApiService,
   private val authPreferences: AuthPreferences
): Repository {

   override suspend fun register(name: String, email: String, password: String): RegisterResponse {
      return apiService.register(name, email, password)
   }

   override suspend fun login(email: String, password: String): LoginResponse {
      return apiService.login(email, password)
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

   override suspend fun getDetailStories(token: String, id: String): DetailStoryResponse {
      val authHeader = "Bearer $token"
      return apiService.getDetailStories(authHeader, id)
   }

   override suspend fun getAllStoriesLocation(token: String): StoriesResponse {
      val authHeader = "Bearer $token"
      return apiService.getAllStoriesLocation(authHeader)
   }
}