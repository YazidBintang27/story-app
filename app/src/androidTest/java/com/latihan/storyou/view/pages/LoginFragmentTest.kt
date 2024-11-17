package com.latihan.storyou.view.pages

import ConvertJson
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.latihan.storyou.MainActivity
import com.latihan.storyou.R
import com.latihan.storyou.utils.ApiConstant
import com.latihan.storyou.utils.IdlingResources
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginFragmentTest {

   private val webMock = MockWebServer()

   @get:Rule
   val activityRule  = ActivityScenarioRule(MainActivity::class.java)

   private lateinit var scenario: ActivityScenario<MainActivity>

   @Before
   fun setUp() {
      webMock.start(8080)
      ApiConstant.BASE_URL = "http://127.0.0.1:8080/"

      IdlingRegistry.getInstance().register(IdlingResources.countingIdlingResource)
      scenario = launchActivity(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
      scenario.onActivity {
         it.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
      }
   }

   @Test
   fun login_action() {
      onView(withId(R.id.ed_login_email)).perform(ViewActions.typeText("awikwok00@gmail.com"))
      onView(withId(R.id.ed_login_password)).perform(ViewActions.typeText("awikwok00"))
      val mockResponse = MockResponse()
         .setResponseCode(200)
         .setBody(ConvertJson.readStringFromFile("login_success_response.json"))
      webMock.enqueue(mockResponse)
      onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
      onView(withId(R.id.btn_login)).perform(click())
   }

   @After
   fun tearDown() {
      webMock.shutdown()
   }
}