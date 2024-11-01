package com.latihan.storyou.view.pages

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
import com.latihan.storyou.databinding.FragmentProfileBinding
import com.latihan.storyou.view.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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
   }

   private fun getData() {
      viewLifecycleOwner.lifecycleScope.launch {
         authViewModel.loginResponse.collect { response ->
            response?.loginResult?.let {
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
            if (!loggedIn) {
               navController.navigate(R.id.action_profileFragment_to_onBoardFragment)
            }
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}