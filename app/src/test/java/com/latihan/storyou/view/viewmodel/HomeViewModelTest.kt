package com.latihan.storyou.view.viewmodel

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.latihan.storyou.DataDummy
import com.latihan.storyou.MainDispatcherRule
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.local.room.StoryEntity
import com.latihan.storyou.data.repository.Repository
import com.latihan.storyou.getOrAwaitValue
import com.latihan.storyou.view.adapter.StoriesAdapter
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

   @get:Rule
   val mainDispatcherRules = MainDispatcherRule()

   private lateinit var homeViewModel: HomeViewModel

   @Mock
   private lateinit var repository: Repository

   @Test
   fun `when Get Story Should Not Null and Return Data`() = runTest {
      val dummyStory = DataDummy.generateDummyStoryResponse()
      val pagingData = PagingData.from(dummyStory)

      val flow = MutableStateFlow(pagingData)

      Mockito.`when`(repository.getAllStories()).thenReturn(flow)

      homeViewModel = HomeViewModel(repository)

      val actualStory: PagingData<StoryEntity> = homeViewModel.storiesResponse.getOrAwaitValue()

      val differ = AsyncPagingDataDiffer(
         diffCallback = StoriesAdapter.DIFF_CALLBACK,
         updateCallback = noopListUpdateCallback,
         workerDispatcher = Dispatchers.Main
      )

      differ.submitData(actualStory)

      assertNotNull(differ.snapshot())

      assertEquals(dummyStory.size, differ.snapshot().size)

      assertEquals(dummyStory[0], differ.snapshot()[0])
   }

   @Test
   fun `when Get Story Empty Should Return No Data`() = runTest {
      val data: PagingData<StoryEntity> = PagingData.from(emptyList())

      val expectedFlow = MutableStateFlow<PagingData<StoryEntity>>(data)

      Mockito.`when`(repository.getAllStories()).thenReturn(expectedFlow)

      homeViewModel = HomeViewModel(repository)

      val actualStory: PagingData<StoryEntity> = homeViewModel.storiesResponse.getOrAwaitValue()

      val differ = AsyncPagingDataDiffer(
         diffCallback = StoriesAdapter.DIFF_CALLBACK,
         updateCallback = noopListUpdateCallback,
         workerDispatcher = Dispatchers.Main
      )

      differ.submitData(actualStory)

      assertEquals(0, differ.snapshot().size)
   }
}

val noopListUpdateCallback = object : ListUpdateCallback {
   override fun onInserted(position: Int, count: Int) {}
   override fun onRemoved(position: Int, count: Int) {}
   override fun onMoved(fromPosition: Int, toPosition: Int) {}
   override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
