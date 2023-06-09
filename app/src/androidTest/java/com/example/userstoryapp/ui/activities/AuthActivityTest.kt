package com.example.userstoryapp.ui.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.userstoryapp.utils.EspressoIdlingResource
import com.example.userstoryapp.R
import com.example.userstoryapp.ui.customview.CustomEditTextEmail
import com.example.userstoryapp.ui.customview.CustomEditTextName
import com.example.userstoryapp.ui.customview.CustomEditTextPassword
import com.example.userstoryapp.ui.fragments.LoginFragment
import com.example.userstoryapp.ui.fragments.RegisterFragment
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
 * UI Testing in AuthActivityTest
 * Initial condition: Must already logged out
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AuthActivityTest{
    private val userName = "Riofuad Testing"
    private val userEmail = "riofuad.testing@gmail.com"
    private val userPassword = "riofuadtesting"

    @get:Rule
    val activity = ActivityScenarioRule(AuthActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun register_Success() {
        activity.scenario.onActivity { activity ->
            val fragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val registerFragment = RegisterFragment()
            fragmentTransaction.replace(R.id.auth_container, registerFragment)
            fragmentTransaction.commit()
        }
        onView(withId(R.id.ed_register_name)).perform(click())
        onView(isAssignableFrom(CustomEditTextName::class.java)).perform(typeText(userName))
        onView(withId(R.id.ed_register_email)).perform(click())
        onView(isAssignableFrom(CustomEditTextEmail::class.java)).perform(typeText(userEmail))
        onView(withId(R.id.ed_register_password)).perform(click())
        onView(isAssignableFrom(CustomEditTextPassword::class.java)).perform(typeText(userPassword))
        onView(withId(R.id.btn_signup)).perform(click())
        onView(withId(R.id.btn_login)).perform(click())
    }

    @Test
    fun login_Success() {
        activity.scenario.onActivity { activity ->
            val fragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val loginFragment = LoginFragment()
            fragmentTransaction.replace(R.id.auth_container, loginFragment)
            fragmentTransaction.commit()
        }
        onView(withId(R.id.ed_login_email)).perform(click())
        onView(isAssignableFrom(CustomEditTextEmail::class.java)).perform(typeText(userEmail))
        onView(withId(R.id.ed_login_password)).perform(click())
        onView(isAssignableFrom(CustomEditTextPassword::class.java)).perform(typeText(userPassword))
        onView(withId(R.id.btn_signin)).perform(click())
    }
}