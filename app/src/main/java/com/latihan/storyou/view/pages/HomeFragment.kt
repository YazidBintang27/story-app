package com.latihan.storyou.view.pages

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.latihan.storyou.R
import com.latihan.storyou.databinding.FragmentHomeBinding
import com.latihan.storyou.view.adapter.StoriesAdapter
import com.latihan.storyou.view.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

   private var _binding: FragmentHomeBinding? = null
   private val binding get() = _binding!!
   private lateinit var navController: NavController
   private val storiesAdapter: StoriesAdapter = StoriesAdapter()
   private val homeViewModel: HomeViewModel by viewModels()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      // Inflate the layout for this fragment
      _binding = FragmentHomeBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      navController = Navigation.findNavController(view)
      setupAdapter()
      getData()
   }

   private fun setupAdapter() {
      binding.rvStoriesData.apply {
         adapter = storiesAdapter
         layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
         setHasFixedSize(true)
      }
   }

   private fun getData() {
      lifecycleScope.launch {
         repeatOnLifecycle(Lifecycle.State.STARTED) {
            homeViewModel.storiesResponse.collect { stories ->
               Log.d("CheckStoriesList", "${stories?.listStory}")
               storiesAdapter.setData(stories?.listStory)
            }
         }
      }

      lifecycleScope.launch {
         repeatOnLifecycle(Lifecycle.State.STARTED) {
            homeViewModel.isLoading.collect { isLoading ->
               binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}