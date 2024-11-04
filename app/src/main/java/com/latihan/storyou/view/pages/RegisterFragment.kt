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
import com.google.android.material.snackbar.Snackbar
import com.latihan.storyou.R
import com.latihan.storyou.databinding.FragmentRegisterBinding
import com.latihan.storyou.view.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

   private var _binding: FragmentRegisterBinding? = null
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
      _binding = FragmentRegisterBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      navController = Navigation.findNavController(view)
      binding.btnNavigateLogin.setOnClickListener { navController.navigate(R.id.action_registerFragment_to_loginFragment) }
      register()
   }

   private fun register() {
      binding.btnRegister.setOnClickListener {
         val name = binding.edRegisterName.text.toString()
         val email = binding.edRegisterEmail.text.toString()
         val password = binding.edRegisterPassword.text.toString()
         if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Snackbar.make(binding.root, "Fill out all form!", Snackbar.LENGTH_SHORT).show()
         } else {
            viewLifecycleOwner.lifecycleScope.launch {
               binding.progressIndicator.visibility = View.VISIBLE
               authViewModel.register(name, email, password)
                  authViewModel.registerResponse.collectLatest { response ->
                     response?.let {
                        val message = response.message
                        Log.d("RegisterFragment", "${response.message}")
                        binding.progressIndicator.visibility = View.GONE
                        if (response.error == false) {
                           try {
                              navController.navigate(R.id.action_registerFragment_to_loginFragment)
                           } catch (e: IllegalArgumentException) {
                              Log.e("NavigateError", "${e.message}")
                           } finally {
                              Snackbar.make(binding.root, "$message", Snackbar.LENGTH_SHORT).show()
                           }
                        } else {
                           authViewModel.clearRegisterResponse()
                           Log.d("RegisterFragment", "${response.error}")
                           Snackbar.make(binding.root, "$message", Snackbar.LENGTH_SHORT).show()
                        }
                     }
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