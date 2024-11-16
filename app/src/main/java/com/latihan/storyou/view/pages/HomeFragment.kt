package com.latihan.storyou.view.pages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.latihan.storyou.R
import com.latihan.storyou.databinding.FragmentHomeBinding
import com.latihan.storyou.view.adapter.LoadingStateAdapter
import com.latihan.storyou.view.adapter.StoriesAdapter
import com.latihan.storyou.view.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

   private lateinit var binding: FragmentHomeBinding
   private lateinit var navController: NavController
   private lateinit var storiesAdapter: StoriesAdapter
   private val homeViewModel: HomeViewModel by viewModels()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      // Inflate the layout for this fragment
      binding = FragmentHomeBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      navController = Navigation.findNavController(view)
      getData()
      navigateToMaps()
   }

   override fun onResume() {
      super.onResume()
      binding.rvStoriesData.smoothScrollToPosition(0)
   }

   private fun getData() {
      storiesAdapter = StoriesAdapter()
      binding.rvStoriesData.layoutManager = LinearLayoutManager(context)
      binding.rvStoriesData.adapter = storiesAdapter.withLoadStateFooter(
         footer = LoadingStateAdapter {
            Log.d("TestLoadPaging", "retry called")
            storiesAdapter.retry()
         }
      )

      storiesAdapter.addLoadStateListener { loadState ->
         if (loadState.refresh is LoadState.Loading) {
            showLoading(true)
         } else {
            showLoading(false)
         }
      }

      lifecycleScope.launch {
         homeViewModel.storiesResponse.collectLatest { pagingData ->
            Log.d("TestLoadPaging", "Ini data: $pagingData")
            showLoading(false)
            storiesAdapter.setFragment(requireParentFragment())
            storiesAdapter.submitData(pagingData)
            storiesAdapter.notifyItemChanged(0)
         }
      }
   }

   private fun showLoading(isVisible: Boolean) {
      binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
   }

   private fun navigateToMaps() {
      binding.icMaps.setOnClickListener {
         navController.navigate(R.id.action_homeFragment_to_mapsFragment)
      }
   }
}