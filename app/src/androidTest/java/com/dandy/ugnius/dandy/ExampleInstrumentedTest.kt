package com.dandy.ugnius.dandy

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.dandy.ugnius.dandy.login.views.LoginFragment
import com.dandy.ugnius.dandy.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    //todo implement later on
//    @Rule @JvmField var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)
//
//    @Before
//    fun restoreState() {
//        activityRule.activity.supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.content, LoginFragment())
//            .commit()
//    }
//
//    @Test
//    fun useAppContext() {
//        onView(withId(R.id.loginButton)).perform(click())
//        onView(withText("LOG IN TO SPOTIFY")).perform(click())
//        onView(withText("Username")).perform(typeText("ugniusvaznys"))
//        onView(withText("Password")).perform(typeText("asrsas3245"))
//        onView(withText("LOG IN")).perform(click())
//    }
}
