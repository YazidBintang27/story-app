package com.latihan.storyou.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.local.room.StoryEntity
import com.latihan.storyou.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
   private val repository: Repository,
   private val authPreferences: AuthPreferences
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val storiesResponse: Flow<PagingData<StoryEntity>> = authPreferences.authToken.flatMapLatest { token ->
      if (token.isNullOrEmpty()) {
         throw IllegalStateException("Token is missing")
      } else {
         repository.getAllStories(token).cachedIn(viewModelScope)
      }
      }
}