package com.dandy.ugnius.dandy

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.runner.AndroidJUnit4
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import org.hamcrest.Matchers.allOf
import com.dandy.ugnius.dandy.home.view.adapters.HorizontalArtistsAdapter
import com.dandy.ugnius.dandy.main.view.MainActivity
import org.junit.Rule
import com.dandy.ugnius.dandy.artist.view.adapters.ArtistsAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapter
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UITest {

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    //A simple test used for application flow testing after refactoring large code bases.
    //sometimes we will get HTTP 429 exception because  espresso walks through the app really fast
    //so in that case just restart the test. Also you must be logged in order to run it
    @Test
    fun testApplicationFlow() {
        val context = InstrumentationRegistry.getTargetContext()

        onView(withId(R.id.homeRecycler)).perform(RecyclerViewActions.actionOnItemAtPosition<HorizontalArtistsAdapter.ViewHolder>(1, click()))
        onView(withText(context.getString(R.string.albums))).perform(click())
        onView(withText(context.getString(R.string.similar))).perform(click())
        onView(allOf(withId(R.id.recycler), isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition<ArtistsAdapter.ViewHolder>(1, click()))
        onView(allOf(withId(R.id.recycler), isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition<TracksAdapter.ViewHolder>(1, click()))

        onView(withId(R.id.shuffle))
        onView(withId(R.id.next)).perform(click())
        executeAction(3) { Espresso.pressBack() }

        onView(withId(R.id.likes)).perform(click())
        onView(allOf(withId(R.id.recycler), isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition<TracksAdapter.ViewHolder>(1, click()))

        executeAction(3) { onView(withId(R.id.next)).perform(click()) }
        executeAction(5) { onView(withId(R.id.previous)).perform(click()) }

        onView(withId(R.id.home)).perform(click())

    }

    private fun executeAction(times: Int, action: () -> Unit = {}) {
        for (i in 0 until times) {
            action()
        }
    }
}
