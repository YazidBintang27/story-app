package com.latihan.storyou

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.latihan.storyou.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

   private lateinit var binding: ActivityMainBinding

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)
      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
         v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         insets
      }
      onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
      val bottomNavView = binding.bottomNav
      val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
      val navController = navHostFragment.navController
      navController.addOnDestinationChangedListener { _, destination, _ ->
         when (destination.id) {
            R.id.homeFragment, R.id.profileFragment -> bottomNavView.visibility = View.VISIBLE
            else -> bottomNavView.visibility = View.GONE
         }
      }
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//         installSplashScreen()
//         navController.navigate(R.id.homeFragment)
//      }
      bottomNavView.setupWithNavController(navController)
   }

   private val onBackPressedCallback = object: OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
         val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
         val navController = navHostFragment.navController
         val currentFragmentId = navHostFragment.navController.currentDestination?.id
         if (currentFragmentId == R.id.homeFragment || currentFragmentId == R.id.profileFragment) {
            finish()
         } else {
            navController.popBackStack()
         }
      }
   }
}