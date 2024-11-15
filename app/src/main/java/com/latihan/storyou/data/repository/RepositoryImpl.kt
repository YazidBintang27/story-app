package com.latihan.storyou.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.local.room.StoryDatabase
import com.latihan.storyou.data.local.room.StoryEntity
import com.latihan.storyou.data.mediator.StoryRemoteMediator
import com.latihan.storyou.data.remote.models.DetailStoryResponse
import com.latihan.storyou.data.remote.models.LoginResponse
import com.latihan.storyou.data.remote.models.PostResponse
import com.latihan.storyou.data.remote.models.RegisterResponse
import com.latihan.storyou.data.remote.models.StoriesResponse
import com.latihan.storyou.data.remote.service.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
   private val apiService: ApiService,
   private val database: StoryDatabase,
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

   @OptIn(ExperimentalPagingApi::class)
   override suspend fun getAllStories(): Flow<PagingData<StoryEntity>> {
      var token = ""
      authPreferences.authToken.collectLatest {
         it?.let {
            token = it
         }
      }
      return Pager(
         config = PagingConfig(
            pageSize = 5,
            enablePlaceholders = false
         ),
         remoteMediator = StoryRemoteMediator(database, apiService, token),
         pagingSourceFactory = { database.storyDao().getStories() }
      ).flow
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