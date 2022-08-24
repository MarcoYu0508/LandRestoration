package com.mhy.landrestoration

import android.view.View
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mhy.landrestoration.adapter.ProjectListAdapter
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PointDataInstrumentedTest {

    @Before
    fun setup() {
        launchActivity<MainActivity>()
    }

    @Test
    fun test1_new_project_is_displayed_in_list() {
        onView(withId(R.id.btnImport)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.append)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.etInput)).perform(ViewActions.replaceText("Project"))
        Thread.sleep(1000)
        onView(withText("確定"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
        onView(withText("Project")).check(matches(isDisplayed()))
    }

    @Test
    fun test2_new_point_is_displayed_in_list() {
        onView(withId(R.id.btnImport)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ProjectListAdapter.ProjectViewHolder>(
                0, clickItemWithId(R.id.btnInspect)
            )
        )
        onView(withId(R.id.append)).perform(click())
        Thread.sleep(1000)
        onView(withText("填寫新增"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
        onView(withId(R.id.etName)).perform(ViewActions.replaceText("Point"))
        onView(withId(R.id.etN)).perform(ViewActions.replaceText("2767801.436"))
        onView(withId(R.id.etE)).perform(ViewActions.replaceText("296864.655"))
        Thread.sleep(1000)
        onView(withText("確定"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
        onView(withText("Point")).check(matches(isDisplayed()))
    }

    @Test
    fun test3_delete_point_in_list() {
        onView(withId(R.id.btnImport)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ProjectListAdapter.ProjectViewHolder>(
                0, clickItemWithId(R.id.btnInspect)
            )
        )
        Thread.sleep(1000)
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ProjectListAdapter.ProjectViewHolder>(
                0, clickItemWithId(R.id.imgDelete)
            )
        )
        Thread.sleep(1000)
        onView(withText("Point")).check(doesNotExist())
    }

    @Test
    fun test4_delete_project_in_list() {
        onView(withId(R.id.btnImport)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ProjectListAdapter.ProjectViewHolder>(
                0, clickItemWithId(R.id.imgDelete)
            )
        )
        Thread.sleep(1000)
        onView(withText("確定"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
        onView(withText("Project")).check(doesNotExist())
    }


    private fun clickItemWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById(id) as View
                v.performClick()
            }
        }
    }
}