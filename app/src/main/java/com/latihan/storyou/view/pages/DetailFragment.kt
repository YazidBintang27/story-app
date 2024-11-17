package com.latihan.storyou.view.pages

import android.annotation.SuppressLint
import android.location.Geocoder
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
import com.bumptech.glide.Glide
import com.latihan.storyou.R
import com.latihan.storyou.databinding.FragmentDetailBinding
import com.latihan.storyou.view.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class DetailFragment : Fragment() {

   private var _binding: FragmentDetailBinding? = null
   private val binding get() = _binding!!
   private lateinit var navController: NavController
   private val detailViewModel: DetailViewModel by viewModels()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      // Inflate the layout for this fragment
      _binding = FragmentDetailBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      navController = Navigation.findNavController(view)
      binding.toolbar.setNavigationOnClickListener { navController.navigateUp() }
      getData()
   }

   @SuppressLint("SetTextI18n")
   private fun getData() {
      val args = DetailFragmentArgs.fromBundle(arguments as Bundle)
      val id = args.id
      viewLifecycleOwner.lifecycleScope.launch {
         detailViewModel.getDetailStory(id)
         detailViewModel.detailResponse.collectLatest { response ->
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
               timeZone = TimeZone.getTimeZone("UTC")
            }
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val parsedDate = response?.story?.createdAt?.let { inputFormat.parse(it) }
            val formattedDate = parsedDate?.let { outputFormat.format(it) } ?: "Unknown Date"
            with (binding) {
               Glide.with(ivDetailPhoto)
                  .load(response?.story?.photoUrl)
                  .error(R.drawable.noimage)
                  .into(ivDetailPhoto)
               tvDetailName.text = response?.story?.name
               tvDetailDate.text = formattedDate
               tvDetailDescription.text = response?.story?.description
               val geoCoder = Geocoder(requireContext(), Locale.getDefault())
               try {
                  if (response?.story?.lat != null && response.story.lon != null) {
                     val addresses = geoCoder.getFromLocation(response.story.lat, response.story.lon, 1)
                     if (!addresses.isNullOrEmpty()) {
                        val cityName = addresses[0].subAdminArea
                        val countryName = addresses[0].countryName
                        tvDetailLocation.text = "$cityName, $countryName"
                     }
                  } else {
                     tvDetailLocation.text = context?.getString(R.string.no_location)
                  }
               } catch (e: IOException) {
                  Log.e("GeoCoderError", "${e.message}")
               }
            }
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}