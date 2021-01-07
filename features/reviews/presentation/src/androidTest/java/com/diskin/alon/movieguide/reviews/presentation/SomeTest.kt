package com.diskin.alon.movieguide.reviews.presentation

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesSearchFragment
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesSearchViewModel
import com.github.ignaciotcrespo.junitwithparams.WithParams
import com.github.ignaciotcrespo.junitwithparams.WithParamsRule
import dagger.android.support.AndroidSupportInjection
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.android.synthetic.main.fragment_search_movies.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class SomeTest {

    private lateinit var scenario: ActivityScenario<TestSingleFragmentActivity>
    private val viewModel: MoviesSearchViewModel = mockk()
    private val results = MutableLiveData<PagingData<Movie>>()
    private val searchTextSlot = slot<String>()

    @get:Rule
    var params = WithParamsRule()

    @Before
    fun setUp() {
        searchTextSlot.captured = ""
        val slot  = slot<MoviesSearchFragment>()
        mockkStatic(AndroidSupportInjection::class)
        every { AndroidSupportInjection.inject(capture(slot)) } answers {
            slot.captured.viewModel = viewModel
        }
        every { viewModel.results } returns results
        every { viewModel.searchText = capture(searchTextSlot) } answers { }
        every { viewModel.searchText } answers { searchTextSlot.captured } // dynamic answer and not fixed return value(!),for ui recreation testing
        scenario = ActivityScenario.launch(TestSingleFragmentActivity::class.java)
    }

    @Test
    @WithParams(
        names = ["name"],
        value = ["steve","jobs"]
    )
    fun name() {
        scenario.onActivity {
            it.empty_search_label.visibility = View.VISIBLE
            it.empty_search_label.text = params["name"]
        }
        Thread.sleep(4000)
    }
}