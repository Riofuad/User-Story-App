package com.example.userstoryapp.ui.activities

import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.userstoryapp.R
import com.example.userstoryapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*
* nb. Actually I don't know whether this works or not,
* because when I run the test it takes a very long time which doesn't finish
*/
/**
 * UI Testing in MainActivityTest
 * Initial condition: Must already logged in and allow camera and location permissions
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class MainActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addStory_Success() {
        onView(withId(R.id.action_fab)).perform(click())
        onView(withId(R.id.fab_add_story)).perform(click())
        onView(withId(R.id.btn_take_photo)).perform(click())
        onView(withId(R.id.ed_add_description)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(typeText("Description"))
        onView(withId(R.id.button_add)).perform(click())
    }
}