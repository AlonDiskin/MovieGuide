package com.diskin.alon.movieguide.reviews.presentation.controller

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.presentation.setCustomDecoration
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesSearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject
import kotlinx.android.synthetic.main.fragment_search_movies.*

/**
 * Provides ui for searching movies.
 */
@OptionalInject
@AndroidEntryPoint
class MoviesSearchFragment : Fragment(), SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    private val viewModel: MoviesSearchViewModel by viewModels()
    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup search results ui adapter
        val adapter = MoviesAdapter(::navigateToMovieReview)
        search_results.adapter = adapter

        setCustomDecoration(
            requireContext(),
            search_results,
            requireContext().resources.getInteger(R.integer.movies_span)
        )

        // Listen to data set changes and update empty results notification accordingly
        search_results.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener{
            override fun onChildViewAttachedToWindow(view: View) {

            }

            override fun onChildViewDetachedFromWindow(view: View) {
                resolveEmptySearchNotification(adapter.itemCount)
            }

        })
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                resolveEmptySearchNotification(itemCount)
            }
        })

        // Handle adapter paging load state updates
        adapter.addLoadStateListener { state ->
            // remove any prev error notifications
            errorSnackbar?.dismiss()

            when(state.refresh) {
                is LoadState.Loading -> progress_bar.visibility = View.VISIBLE

                is LoadState.NotLoading -> {
                    if (state.append is LoadState.NotLoading) {
                        progress_bar?.visibility = View.GONE
                    }
                }

                is LoadState.Error -> showErrorNotification(
                    (state.refresh as LoadState.Error).error) { adapter.retry() }
            }

            when (state.append) {
                is LoadState.Loading -> progress_bar?.visibility = View.VISIBLE

                is LoadState.NotLoading -> {
                    if (state.refresh is LoadState.NotLoading) {
                        progress_bar?.visibility = View.GONE
                    }
                }

                is LoadState.Error -> showErrorNotification(
                    (state.append as LoadState.Error).error) { adapter.retry() }
            }
        }

        // Observe view model search results paging state
        viewModel.results.observe(viewLifecycleOwner) { adapter.submitData(lifecycle,it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movies_search,menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchItem.expandActionView()

        searchView.setIconifiedByDefault(false)
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setQuery(viewModel.searchText,false)

        searchView.setOnQueryTextListener(this)
        searchItem.setOnActionExpandListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            empty_search_label.visibility = View.GONE
            viewModel.search(it)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { viewModel.searchText = it }
        return true
    }

    private fun showErrorNotification(e: Throwable, retry: () -> (Unit)) {
        progress_bar.visibility = View.GONE
        errorSnackbar = if (e is AppError) {
            when(e.retriable) {
                true -> Snackbar.make(search_results,e.description,Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.action_retry)) { retry() }
                else -> Snackbar.make(search_results,e.description,Snackbar.LENGTH_INDEFINITE)
            }
        } else {
            Snackbar.make(
                search_results,
                e.message ?: getString(R.string.unknown_error),
                Snackbar.LENGTH_INDEFINITE)
        }

        errorSnackbar?.show()
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        findNavController().navigateUp()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        errorSnackbar?.dismiss()
    }

    private fun resolveEmptySearchNotification(resultsCount: Int) {
        when (resultsCount) {
            0 -> empty_search_label.visibility = View.VISIBLE
            else -> empty_search_label.visibility = View.GONE
        }
    }

    private fun navigateToMovieReview(movie: Movie) {
        val bundle = bundleOf(getString(R.string.movie_id_arg) to movie.id)
        findNavController().navigate(R.id.action_moviesSearchFragment_to_movieReviewFragment, bundle)
    }
}
