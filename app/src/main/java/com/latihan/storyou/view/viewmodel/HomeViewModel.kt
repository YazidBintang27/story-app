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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
   private val repository: Repository,
): ViewModel() {

   private val _storiesResponse = MutableStateFlow<PagingData<StoryEntity>>(PagingData.empty())
   val storiesResponse: StateFlow<PagingData<StoryEntity>> by lazy { _storiesResponse }

   init {
      getStories()
   }

   private fun getStories() {
      viewModelScope.launch {
         repository.getAllStories().cachedIn(viewModelScope).collect {
            _storiesResponse.value = it
         }
      }
   }
}