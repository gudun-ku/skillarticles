package ru.skillbranch.skillarticles

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import ru.skillbranch.skillarticles.matchers.EspressoTestsMatchers.withDrawable
import ru.skillbranch.skillarticles.ui.RootActivity

class EspressoTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(RootActivity::class.java, true, true)

    @Test
    fun myTest() {
        Thread.sleep(2_000L)
        onView(
            withText("CoordinatorLayout Basic")
        ).check(
            matches(isDisplayed())
        )

        Thread.sleep(1_000L)

        onView(
            withId(R.id.btn_like)
        )
            .check(matches(isClickable()))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            //.check(matches(withDrawable(R.drawable.ic_favorite_border_black_24dp)))
            .perform(click())

        onView(
            withId(R.id.btn_like)
        )
            .check(matches(isChecked()))
            //.check(matches(withDrawable(R.drawable.ic_favorite_black_24dp)))


        Thread.sleep(1_000L)
        onView(
            withId(R.id.action_search)
        ).perform(click())
    }
}