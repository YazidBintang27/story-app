package com.latihan.storyou.view.pages

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.latihan.storyou.R
import com.latihan.storyou.databinding.FragmentProfileBinding
import com.latihan.storyou.view.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

   private var _binding: FragmentProfileBinding? = null
   private val binding get () = _binding!!
   private lateinit var navController: NavController
   private val authViewModel: AuthViewModel by viewModels()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      // Inflate the layout for this fragment
      _binding = FragmentProfileBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      navController = Navigation.findNavController(view)
      getData()
      binding.actionLogout.setOnClickListener { logout() }
      changeLanguage()
   }

   private fun getData() {
      viewLifecycleOwner.lifecycleScope.launch {
         authViewModel.savedLoginResponse.collect { response ->
            Log.d("CheckLoginResponse", " Profile before passing: ${response?.loginResult?.userId}")
            response?.loginResult?.let {
               Log.d("CheckLoginResponse", "Profile after passing: ${response.loginResult.userId}")
               binding.tvName.text = response.loginResult.name
               binding.tvUserId.text = response.loginResult.userId
            }
         }
      }
   }

   private fun logout() {
      viewLifecycleOwner.lifecycleScope.launch {
         authViewModel.logout()
         authViewModel.isLoggedIn.collect { loggedIn ->
            Log.d("LogoutStatus", "Is user logged in? $loggedIn")
            if (!loggedIn) {
               navController.navigate(R.id.action_profileFragment_to_onBoardFragment)
               Snackbar.make(binding.root, "Logout Successful", Snackbar.LENGTH_SHORT).show()
            } else {
               Snackbar.make(binding.root, "Logout Failed", Snackbar.LENGTH_SHORT).show()
            }
         }
      }
   }

   private fun changeLanguage() {
      binding.actionChangeLanguage.setOnClickListener {
         startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}