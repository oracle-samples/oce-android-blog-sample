/*
 * Copyright (c) 2023, Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.content.samples.blog


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import com.oracle.content.samples.blog.FetchingIdlingResource
import com.oracle.content.samples.blog.MainActivity
import com.oracle.content.samples.supremo.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Rule
import org.junit.Test

@SmallTest
//@RunWith(AndroidJUnit4::class)
class MainActivityTest  {

    private val fetchingIdlingResource = FetchingIdlingResource()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    init {
        mActivityTestRule.launchActivity(null)
        IdlingRegistry.getInstance().register(fetchingIdlingResource)
        mActivityTestRule.activity.setFetcherListener(fetchingIdlingResource)
    }


    @Test
    fun mainActivityTest() {


        val frameLayout = onView(
                allOf(withId(R.id.card_view),
                        childAtPosition(
                                allOf(withId(R.id.topic_list),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                                                1)),
                                0),
                        isDisplayed()))
        frameLayout.check(matches(isDisplayed()))

        val textView = onView(
                allOf(withId(R.id.topic_detail), withText("Learn about how to create beautiful Latte art and pouring just the right cup."),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view),
                                        0),
                                2),
                        isDisplayed()))
        textView.check(matches(withText("Learn about how to create beautiful Latte art and pouring just the right cup.")))

        val cardView = onView(
                allOf(withId(R.id.card_view),
                        childAtPosition(
                                allOf(withId(R.id.topic_list),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()))
        cardView.perform(click())

        val cardView2 = onView(
                allOf(withId(R.id.articlecard_view),
                        childAtPosition(
                                allOf(withId(R.id.article_list),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()))
        cardView2.perform(click())

    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    @After
    fun tearDown(){
        mActivityTestRule.finishActivity() // since you prefer manual
        val idlingRegistry = IdlingRegistry.getInstance()
        idlingRegistry.resources.forEach { idlingRegistry.unregister(it) }
    }

}
