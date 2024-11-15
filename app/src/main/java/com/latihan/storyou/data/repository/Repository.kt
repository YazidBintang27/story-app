package com.latihan.storyou.data.repository

import androidx.paging.PagingData
import com.latihan.storyou.data.local.room.StoryEntity
import com.latihan.storyou.data.remote.models.DetailStoryResponse
import com.latihan.storyou.data.remote.models.LoginResponse
import com.latihan.storyou.data.remote.models.PostResponse
import com.latihan.storyou.data.remote.models.RegisterResponse
import com.latihan.storyou.data.remote.models.StoriesResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface Repository {
   suspend fun register(
      name: String,
      email: String,
      password: String
   ): RegisterResponse

   suspend fun login(
      email: String,
      password: String
   ): LoginResponse

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

   suspend fun getAllStories(): Flow<PagingData<StoryEntity>>

   suspend fun getDetailStories(token: String, id: String): DetailStoryResponse

   suspend fun getAllStoriesLocation(token: String): StoriesResponse
}