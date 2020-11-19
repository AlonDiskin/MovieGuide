package com.diskin.alon.movieguide.news.presentation.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModel
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_movies_headlines.*
import javax.inject.Inject

/**
 * Display a listing of [NewsHeadline]s, and provide user interaction
 * with listed items.
 */
class MoviesHeadlinesFragment : Fragment() {

    @Inject
    lateinit var viewModel: MoviesHeadlinesViewModel
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inject fragment
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies_headlines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup news titles adapter
        val adapter = NewsHeadlinesAdapter(this::shareHeadline,this::navigateToArticle)
        headlines.adapter = adapter

        // Handle adapter loading state changes
        adapter.addLoadStateListener { state ->
            // Remove any prev errors
            snackbar?.dismiss()

            // Resolve load state and map to ui function
            when (state.refresh) {
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

        // Observe view model titles state
        viewModel.headlines.observe(viewLifecycleOwner, Observer { adapter.submitData(lifecycle, it) })

        // Handle swipe to refresh UI events
        swipe_refresh.setOnRefreshListener { adapter.refresh() }
    }

    private fun showErrorNotification(e: Throwable, retry: () -> (Unit)) {
        val message = e.message
        // remove any ui load state indicators
        swipe_refresh.isRefreshing = false
        progress_bar.visibility = View.GONE

        // If error message exist,show it with retry action
        snackbar = if (message == null || message.isEmpty()) {
            Snackbar.make(headlines,getString(R.string.unexpected_error),Snackbar.LENGTH_INDEFINITE)
        } else {
            // Else show generic error message indicating unknown error
            Snackbar.make(headlines,message,Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.action_retry)) { retry() }
        }

        snackbar?.show()
    }

    private fun shareHeadline(headline: NewsHeadline) {
        activity?.let {
            ShareCompat.IntentBuilder
                .from(it)
                .setType(getString(R.string.mime_type_text))
                .setText(headline.articleUrl)
                .setChooserTitle(getString(R.string.title_share_headline_chooser))
                .startChooser()
        }
    }

    private fun navigateToArticle(headline: NewsHeadline) {
        val bundle = bundleOf(getString(R.string.key_article_id) to headline.id)
        findNavController().navigate(R.id.articleActivity, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
    }
}