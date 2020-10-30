package com.diskin.alon.movieguide.reviews.presentation.controller

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import com.diskin.alon.movieguide.reviews.appservices.model.MovieSorting
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_movies.*
import javax.inject.Inject

/**
 * Provides ui for browsing movie reviews.
 */
class MoviesFragment : Fragment() {

    @Inject
    lateinit var viewModel: MoviesViewModel
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movies,menu)

        // Observe view model sorting state
        viewModel.sorting.observe(viewLifecycleOwner, {
            it?.let { sorting ->
                when(sorting) {
                    MovieSorting.POPULARITY -> menu.findItem(R.id.action_sort_popular).isChecked = true

                    MovieSorting.RATING -> menu.findItem(R.id.action_sort_rating).isChecked = true

                    MovieSorting.RELEASE_DATE -> menu.findItem(R.id.action_sort_date).isChecked = true
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup movies adapter
        val adapter = MoviesAdapter()
        movies.adapter = adapter

        adapter.refresh()

        // handle adapter paging load state updates
        adapter.addLoadStateListener { state ->
            // remove any prev error notifications
            snackbar?.dismiss()

            when(state.refresh) {
                is LoadState.Loading -> swipe_refresh?.isRefreshing = true

                is LoadState.NotLoading -> swipe_refresh?.isRefreshing = false

                is LoadState.Error -> showErrorNotification(
                    (state.refresh as LoadState.Error).error) { adapter.retry() }
            }

            when (state.append) {
                is LoadState.Loading -> progress_bar?.visibility = View.VISIBLE

                is LoadState.NotLoading -> progress_bar?.visibility = View.GONE

                is LoadState.Error -> showErrorNotification(
                    (state.append as LoadState.Error).error) { adapter.retry() }
            }
        }

        // Observe view model movies paging state
        viewModel.movies.observe(viewLifecycleOwner, { adapter.submitData(lifecycle,it) })

        // Handle swipe to refresh UI event
        swipe_refresh.setOnRefreshListener { adapter.refresh() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // sort according to selection
        return when(item.itemId) {
            R.id.action_sort_popular -> {
                if (!item.isChecked) {
                    viewModel.sortMovies(MovieSorting.POPULARITY)
                }

                true
            }

            R.id.action_sort_rating -> {
                if (!item.isChecked) {
                    viewModel.sortMovies(MovieSorting.RATING)
                }

                true
            }
            R.id.action_sort_date -> {
                if (!item.isChecked) {
                    viewModel.sortMovies(MovieSorting.RELEASE_DATE)
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showErrorNotification(e: Throwable, retry: () -> (Unit)) {
        val message = e.message

        // remove any ui load state indicators
        swipe_refresh.isRefreshing = false
        progress_bar.visibility = View.GONE

        // If error message exist,show it with retry action
        snackbar = if (message == null || message.isEmpty()) {
            Snackbar.make(movies,getString(R.string.unknown_error),Snackbar.LENGTH_INDEFINITE)
        } else {
            // Else show generic error message indicating unknown error
            Snackbar.make(movies,message,Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.action_retry)) { retry() }
        }

        snackbar?.show()
    }
}