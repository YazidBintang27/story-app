package com.latihan.storyou.view.pages

import android.content.Intent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.latihan.storyou.MainActivity
import com.latihan.storyou.R
import com.latihan.storyou.utils.IdlingResources
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProfileFragmentTest {

   @get:Rule
   val activityRule  = ActivityScenarioRule(MainActivity::class.java)

   private lateinit var scenario: ActivityScenario<MainActivity>

   @Before
   fun initiate() {
      IdlingRegistry.getInstance().register(IdlingResources.countingIdlingResource)
      scenario = launchActivity(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
      scenario.onActivity {
         val bottomNavView = it.findViewById<BottomNavigationView>(R.id.bottom_nav)
         bottomNavView.visibility = View.VISIBLE
         it.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ProfileFragment())
            .commit()
      }
   }

   @Test
   fun logout_action() {
      onView(withId(R.id.profileFragment)).check(matches(isDisplayed()))
      onView(withId(R.id.action_logout)).check(matches(isDisplayed()))
      onView(withId(R.id.action_logout)).perform(click())
   }

   @After
   fun down() {
      IdlingRegistry.getInstance().unregister(IdlingResources.countingIdlingResource)
   }
}
