package com.latihan.storyou

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> Flow<T>.getOrAwaitValue(
   time: Long = 2,
   timeUnit: TimeUnit = TimeUnit.SECONDS,
   afterCollect: () -> Unit = {}
): T {
   var data: T? = null
   val latch = CountDownLatch(1)

   runBlocking {
      try {
         afterCollect.invoke()
         data = this@getOrAwaitValue.first()
         latch.countDown()
      } catch (e: Exception) {
         throw TimeoutException("Flow did not emit any value in time")
      }
   }

   if (!latch.await(time, timeUnit)) {
      throw TimeoutException("Flow value was never set.")
   }

   @Suppress("UNCHECKED_CAST")
   return data as T
}