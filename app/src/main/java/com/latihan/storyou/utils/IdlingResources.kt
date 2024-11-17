package com.latihan.storyou.utils

import androidx.test.espresso.idling.CountingIdlingResource

object IdlingResources {
   private const val RESOURCE = "GLOBAL"

   @JvmField
   val countingIdlingResource = CountingIdlingResource(RESOURCE)

   fun increment() {
      countingIdlingResource.increment()
   }

   fun decrement() {
      if (!countingIdlingResource.isIdleNow) {
         countingIdlingResource.decrement()
      }
   }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
   IdlingResources.increment()
   return try {
      function()
   } finally {
      IdlingResources.decrement() // Set app as idle.
   }
}