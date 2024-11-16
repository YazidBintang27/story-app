package com.latihan.storyou.data.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.local.room.RemoteKeys
import com.latihan.storyou.data.local.room.StoryDatabase
import com.latihan.storyou.data.local.room.StoryEntity
import com.latihan.storyou.data.remote.service.ApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator @Inject constructor (
   private val database: StoryDatabase,
   private val apiService: ApiService,
   private val authPreferences: AuthPreferences
): RemoteMediator<Int, StoryEntity>() {

   companion object {
      const val INITIAL_PAGE_INDEX = 1
   }

   override suspend fun initialize(): InitializeAction {
      return InitializeAction.LAUNCH_INITIAL_REFRESH
   }

   override suspend fun load(
      loadType: LoadType,
      state: PagingState<Int, StoryEntity>
   ): MediatorResult {
      val page = when (loadType) {
         LoadType.REFRESH ->{
            val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
            Log.d("StoryRemoteMediator", "refresh")
            remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
         }
         LoadType.PREPEND -> {
            val remoteKeys = getRemoteKeyForFirstItem(state)
            Log.d("StoryRemoteMediator", "Prepend")
            val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            prevKey
         }
         LoadType.APPEND -> {
            val remoteKeys = getRemoteKeyForLastItem(state)
            Log.d("StoryRemoteMediator", "Append")
            val nextKey = remoteKeys?.nextKey
               ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            nextKey
         }
      }

      try {
         Log.d("StoryRemoteMediator", "Start")
         val token = authPreferences.authToken.first()
         Log.d("StoryRemoteMediator", "This is $token")
         val responseData = apiService.getAllStories("Bearer $token", page, state.config.pageSize)


         val endOfPaginationReached = responseData.listStory.isNullOrEmpty()

         database.withTransaction {
            Log.d("StoryRemoteMediator", "Transaction")
            if (loadType == LoadType.REFRESH) {
               database.remoteKeysDao().deleteRemoteKeys()
               database.storyDao().deleteAll()
            }

            val keys = responseData.listStory?.map {
               RemoteKeys(
                  storyId = it?.id.orEmpty(),
                  prevKey = if (page == 1) null else page - 1,
                  nextKey = if (endOfPaginationReached) null else page + 1
               )
            }
            database.remoteKeysDao().insertAll(keys.orEmpty())

            val stories = responseData.listStory?.map {
               StoryEntity(
                  id = it?.id.orEmpty(),
                  name = it?.name.orEmpty(),
                  description = it?.description.orEmpty(),
                  photoUrl = it?.photoUrl.orEmpty(),
                  createdAt = it?.createdAt.orEmpty(),
                  lat = it?.lat,
                  lon = it?.lon
               )
            }
            database.storyDao().insertStories(stories.orEmpty())
         }
         Log.d("StoryRemoteMediator", "Out Transaction")
         return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
      } catch (exception: Exception) {
         Log.e("StoryRemoteMediator", "Error during API call", exception)
         return MediatorResult.Error(exception)
      }
   }

   private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
      return state.anchorPosition?.let { position ->
         state.closestItemToPosition(position)?.id?.let { id ->
            database.remoteKeysDao().remoteKeysStoryId(id)
         }
      }
   }

   private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
      return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { story ->
         database.remoteKeysDao().remoteKeysStoryId(story.id)
      }
   }

   private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
      return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { story ->
         database.remoteKeysDao().remoteKeysStoryId(story.id)
      }
   }
}