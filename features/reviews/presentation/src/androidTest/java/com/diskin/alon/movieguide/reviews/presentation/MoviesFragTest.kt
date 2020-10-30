package com.diskin.alon.movieguide.reviews.presentation

import android.util.Log
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MoviesFragTest {

    private lateinit var scenario: FragmentScenario<MoviesFragment>

    @Before
    fun setUp() {
        android.R.style.Theme_Material_Light_DarkActionBar
        scenario = FragmentScenario.launchInContainer(
            MoviesFragment::class.java, null,
            R.style.AppTheme,
            null
        )

        scenario.onFragment {
            val suca = it.requireActivity().actionBar

            Log.d("IBAT","SUCA:${suca == null}")
        }
    }

    @Test
    fun name() {
//        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
//        Espresso.onView(ViewMatchers.withText(R.string.title_action_sort))
//            .perform(ViewActions.click())
//        Thread.sleep(5000L)
    }
}