package com.latihan.storyou.view.pages

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.latihan.storyou.R
import com.latihan.storyou.databinding.FragmentOnBoardBinding

class OnBoardFragment : Fragment(), View.OnClickListener {

   private var _binding: FragmentOnBoardBinding? = null
   private val binding get() = _binding!!
   private lateinit var navController: NavController

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      // Inflate the layout for this fragment
      _binding = FragmentOnBoardBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      navController = Navigation.findNavController(view)
      binding.btnNavigateLogin.setOnClickListener(this)
      binding.btnNavigateRegister.setOnClickListener(this)
      playAnimation()
   }

   override fun onClick(v: View?) {
      when (v?.id) {
         R.id.btn_navigate_register -> navController.navigate(R.id.action_onBoardFragment_to_registerFragment)
         R.id.btn_navigate_login -> navController.navigate(R.id.action_onBoardFragment_to_loginFragment)
      }
   }

   private fun playAnimation() {
      ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
         duration = 4000
         repeatCount = ObjectAnimator.INFINITE
         repeatMode = ObjectAnimator.REVERSE
      }.start()

      val title = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(300)
      val subtitle = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(300)
      val register = ObjectAnimator.ofFloat(binding.btnNavigateRegister, View.ALPHA, 1f)
         .setDuration(400)
      val login = ObjectAnimator.ofFloat(binding.btnNavigateLogin, View.ALPHA, 1f).setDuration(400)

      val together = AnimatorSet().apply {
         playTogether(register, login)
      }

      AnimatorSet().apply {
         playSequentially(title, subtitle, together)
         start()
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}