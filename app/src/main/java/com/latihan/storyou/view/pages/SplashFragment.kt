package com.latihan.storyou.view.pages

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.latihan.storyou.R
import com.latihan.storyou.databinding.FragmentSplashBinding
import com.latihan.storyou.view.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

   private var _binding: FragmentSplashBinding? = null
   private val binding get () = _binding!!
   private lateinit var navController: NavController
   private val authViewModel: AuthViewModel by viewModels()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      // Inflate the layout for this fragment
      _binding = FragmentSplashBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      navController = Navigation.findNavController(view)
      playAnimation()
      viewLifecycleOwner.lifecycleScope.launch {
         delay(4000)
         authViewModel.isLoggedIn.collectLatest { response ->
            Log.d("Splash", "$response")
            if (response) {
               navController.navigate(R.id.action_splashFragment_to_homeFragment)
               cancel()
            } else {
               navController.navigate(R.id.action_splashFragment_to_onBoardFragment)
               cancel()
            }
         }
      }
   }

   private fun playAnimation() {
      ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
         duration = 2000
         repeatCount = ObjectAnimator.INFINITE
         repeatMode = ObjectAnimator.REVERSE
      }.start()
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}