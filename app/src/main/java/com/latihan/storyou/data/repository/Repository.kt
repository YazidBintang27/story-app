package com.latihan.storyou.data.repository

import com.latihan.storyou.data.remote.models.DetailStoryResponse
import com.latihan.storyou.data.remote.models.LoginResponse
import com.latihan.storyou.data.remote.models.PostResponse
import com.latihan.storyou.data.remote.models.RegisterResponse
import com.latihan.storyou.data.remote.models.StoriesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface Repository {
   val loginResponse: StateFlow<LoginResponse?>
   suspend fun register(
      name: String,
      email: String,
      password: String
   ): RegisterResponse

   suspend fun login(
      email: String,
      password: String
   )

   suspend fun postStories(
      token: String,
      description: RequestBody,
      photo: MultipartBody.Part,
      lat: RequestBody? = null,
      lon: RequestBody? = null
   ): PostResponse

   suspend fun postStoriesGuest(
      description: RequestBody,
      photo: MultipartBody.Part,
      lat: RequestBody? = null,
      lon: RequestBody? = null
   ): PostResponse

   suspend fun getAllStories(token: String): StoriesResponse

   suspend fun getDetailStories(token: String): DetailStoryResponse
}