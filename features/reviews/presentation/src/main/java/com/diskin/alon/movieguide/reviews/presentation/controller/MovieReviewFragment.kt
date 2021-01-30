package com.diskin.alon.movieguide.reviews.presentation.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.data.Trailer
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModel
import com.google.android.material.snackbar.Snackbar
import com.diskin.alon.movieguide.reviews.presentation.databinding.FragmentMovieReviewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

@OptionalInject
@AndroidEntryPoint
class MovieReviewFragment : Fragment() {

    private val viewModel: MovieReviewViewModel by viewModels()
    private var errorSnackbar: Snackbar? = null
    private lateinit var binding: FragmentMovieReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieReviewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup trailers adapter
        val adapter = TrailersAdapter(this::openTrailerWebLink)
        binding.trailers.adapter = adapter

        // Observe view model review state
        viewModel.movieReview.observe(viewLifecycleOwner) {
            binding.movieReview = it
            adapter.submitList(it?.trailers)
        }

        // Observe view model review update state
        viewModel.reviewUpdate.observe(viewLifecycleOwner) { update ->
            when(update) {
                is UpdateViewData.Update -> binding.progressBar.visibility = View.VISIBLE
                is UpdateViewData.EndUpdate -> binding.progressBar.visibility = View.GONE
            }
        }

        // Observe view model review error state
        viewModel.reviewError.observe(viewLifecycleOwner) { error ->
            when(error) {
                is ErrorViewData.NoError -> errorSnackbar?.dismiss()
                is ErrorViewData.NotRetriable -> showNotRetriableError(error)
                is ErrorViewData.Retriable -> showRetriableError(error)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movie_review, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        viewModel.movieReview.observe(this, { review ->
            review?.let {
                val favoritingItem = menu.findItem(R.id.action_favoriting)
                favoritingItem.isEnabled = true

                when (it.favorite) {
                    true -> favoritingItem
                        .setTitle(getString(R.string.title_action_unfavorite_movie))
                        .setIcon(R.drawable.ic_favorite)
                        .setOnMenuItemClickListener {
                            viewModel.unFavoriteReviewedMovie()
                            true
                        }

                    false -> favoritingItem
                        .setTitle(getString(R.string.title_action_favorite_movie))
                        .setIcon(R.drawable.ic_not_favorite)
                        .setOnMenuItemClickListener {
                            viewModel.favoriteReviewedMovie()
                            true
                        }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_share -> {
                shareReview()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleReviewStateError(error: ErrorViewData) {
        when(error) {
            is ErrorViewData.Retriable -> showRetriableError(error)
            is ErrorViewData.NotRetriable -> showNotRetriableError(error)
        }
    }

    private fun showNotRetriableError(error: ErrorViewData.NotRetriable) {
        errorSnackbar = Snackbar.make(
            binding.reviewContent,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)

        errorSnackbar?.show()
    }

    private fun showRetriableError(error: ErrorViewData.Retriable) {
        errorSnackbar = Snackbar.make(
            binding.reviewContent,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.action_retry)) { error.retry() }

        errorSnackbar?.show()
    }

    private fun shareReview() {
        viewModel.movieReview.value?.let { review ->
            ShareCompat.IntentBuilder
                .from(requireActivity())
                .setType(getString(R.string.mime_type_text))
                .setText(review.webUrl)
                .setChooserTitle(getString(R.string.title_share_review_chooser))
                .startChooser()
        } ?: run {
            Toast.makeText(
                requireActivity(),
                getString(R.string.title_action_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openTrailerWebLink(trailer: Trailer) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(trailer.url))
        val chooser = Intent.createChooser(webIntent, getString(R.string.title_trailer_view_chooser))

        startActivity(chooser)
    }
}