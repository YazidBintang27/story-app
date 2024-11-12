package com.latihan.storyou.view.pages

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.latihan.storyou.R
import com.latihan.storyou.view.viewmodel.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

   private lateinit var googleMap: GoogleMap
   private val mapsViewModel: MapsViewModel by viewModels()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View? {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_maps, container, false)
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
   }

   @Deprecated("Deprecated in Java")
   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
      mapFragment.getMapAsync(this)
   }

   override fun onMapReady(googleMap: GoogleMap) {
      this.googleMap = googleMap ?: return
      try {
         val success = googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
               requireContext(),
               R.raw.map_style
            )
         )
         if (!success) {
            Log.e("MapsFragment", "Style parsing failed.")
         }
      } catch (e: Resources.NotFoundException) {
         Log.e("MapsFragment", "Can't find style")
      }
      addMarker(googleMap)
   }

   private fun addMarker(mMap: GoogleMap) {
      viewLifecycleOwner.lifecycleScope.launch {
         mapsViewModel.allStoriesLocation.collectLatest { response ->
            val boundsBuilder = LatLngBounds.Builder()
            response?.listStory?.forEach { story ->
               Log.d("CheckLocation", "Lat: ${story?.lat} Long: ${story?.lon}")
               val name = story?.name
               val description = story?.description
               val lat = story?.lat ?: 0.0
               val lon = story?.lon ?: 0.0
               val latLng = LatLng(lat, lon)
               mMap.addMarker(MarkerOptions().position(latLng).title(name).snippet(description))
               boundsBuilder.include(latLng)
            }
            try {
               val bounds: LatLngBounds = boundsBuilder.build()
               mMap.animateCamera(
                  CameraUpdateFactory.newLatLngBounds(
                     bounds,
                     resources.displayMetrics.widthPixels,
                     resources.displayMetrics.heightPixels,
                     300
                  )
               )
            } catch (e: IllegalStateException) {
               Log.e("MapsFragment", "${e.message}")
            }
         }
      }
   }
}