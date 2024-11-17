package com.latihan.storyou.view.pages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.latihan.storyou.R
import com.latihan.storyou.databinding.FragmentAddStoryBinding
import com.latihan.storyou.utils.reduceFileImage
import com.latihan.storyou.utils.uriToFile
import com.latihan.storyou.view.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class AddStoryFragment : Fragment() {

   private var _binding: FragmentAddStoryBinding? = null
   private val binding get() = _binding!!
   private lateinit var navController: NavController
   private var currentImageUri: Uri? = null
   private lateinit var fusedLocationClient: FusedLocationProviderClient
   private val postViewModel: PostViewModel by viewModels()

   private var lat: Double? = 0.0
   private var lon: Double? = 0.0

   private val requestPermissionLauncher =
      registerForActivityResult(
         ActivityResultContracts.RequestPermission()
      ) { isGranted: Boolean ->
         if (isGranted) {
            Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
         } else {
            Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
         }
      }

   private fun allPermissionsGranted() =
      ContextCompat.checkSelfPermission(
         requireContext(),
         Manifest.permission.CAMERA
      ) == PackageManager.PERMISSION_GRANTED

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      // Inflate the layout for this fragment
      _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
      return binding.root
   }

   @RequiresApi(Build.VERSION_CODES.Q)
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      navController = Navigation.findNavController(view)
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
      binding.toolbar.setNavigationOnClickListener { navController.navigateUp() }
      binding.btnGallery.setOnClickListener { startGallery() }
      showImage()
      binding.btnCamera.setOnClickListener {
         if (allPermissionsGranted()) {
            startCameraX()
         } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
         }
      }
      parentFragmentManager.setFragmentResultListener("cameraRequestKey", viewLifecycleOwner) { _, bundle ->
         val uri = bundle.getParcelable<Uri>("imageUri")
         if (uri != null) {
            currentImageUri = uri
            showImage()
            Log.d("CheckCurrentImage", "Check if not null: $currentImageUri")
         }
      }
      getCurrentLocation()
      binding.buttonAdd.setOnClickListener { uploadStory() }
   }

   private fun startGallery() {
      launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
   }

   private val launcherGallery = registerForActivityResult(
      ActivityResultContracts.PickVisualMedia()
   ) { uri: Uri? ->
      if (uri != null) {
         requireContext().contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
         )
         currentImageUri = uri
         showImage()
      } else {
         Log.d("Photo Picker", "No media selected")
      }
   }

   private fun showImage() {
      Log.d("CheckShowImage", "Test show image: $currentImageUri")
      currentImageUri?.let {
         Log.d("CheckShowImage", "Test show image: $currentImageUri")
         binding.ivPhoto.setImageURI(currentImageUri)
      }
   }

   private fun startCameraX() {
      navController.navigate(R.id.action_addStoryFragment_to_cameraFragment)
   }

   @RequiresApi(Build.VERSION_CODES.Q)
   private fun uploadStory() {
      if (currentImageUri == null) {
         Snackbar.make(binding.root, "Choose image first!", Snackbar.LENGTH_SHORT).show()
      } else {
         val imageFile = uriToFile(currentImageUri!!, requireContext())
         imageFile.reduceFileImage()
         val description = binding.edAddDescription.text.toString()
         val latToRequestBody = lat.toString().toRequestBody("text/plain".toMediaType())
         val lonToRequestBody = lon.toString().toRequestBody("text/plain".toMediaType())
         val descriptionToRequestBody = description.toRequestBody("text/plain".toMediaType())
         val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
         val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
         )
         viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.postStories(
               descriptionToRequestBody,
               multipartBody,
               latToRequestBody,
               lonToRequestBody
            )
            launch {
               postViewModel.isLoading.collectLatest { loading ->
                  if (loading == true) {
                     binding.buttonAdd.text = ""
                     binding.buttonProgressBar.visibility = View.VISIBLE
                  } else {
                     binding.buttonAdd.text = context?.getString(R.string.add_post)
                     binding.buttonProgressBar.visibility = View.GONE
                  }
               }
            }
            launch {
               postViewModel.postResponse.collectLatest { response ->
                  Log.d("CheckPostResponse", "before null checking: ${response?.error}")
                  response?.let {
                     Log.d("CheckPostResponse", "after null checking: ${response.error}")
                     if (response.error == false) {
                        try {
                           navController.navigate(R.id.action_addStoryFragment_to_homeFragment)
                        } catch (e: IllegalArgumentException) {
                           Log.e("NavigateError", "${e.message}")
                        } finally {
                           Snackbar.make(binding.root, "${response.message}", Snackbar.LENGTH_SHORT).show()
                        }
                     } else {
                        postViewModel.clearPostResponse()
                        Snackbar.make(binding.root, "${response.message}", Snackbar.LENGTH_SHORT).show()
                     }
                  }
               }
            }
         }
      }
   }

   private fun getCurrentLocation() {
      binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
         if (isChecked) {
            if (ActivityCompat.checkSelfPermission(
                  requireContext(),
                  Manifest.permission.ACCESS_FINE_LOCATION
               ) != PackageManager.PERMISSION_GRANTED
            ) {
               ActivityCompat.requestPermissions(
                  requireActivity(),
                  arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                  101
               )
               return@setOnCheckedChangeListener
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
               if (location != null) {
                  lat = location.latitude
                  lon = location.longitude
               } else {
                  Log.e("LocationError", "Cannot find location")
               }
            }
         } else {
            lat = 0.0
            lon = 0.0
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}