package com.diskin.alon.movieguide.reviews.presentation.controller

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ViewDataError
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.databinding.ActivityMovieReviewBinding
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_movie_review.*
import javax.inject.Inject

/**
 * Display movie review info,and provide content engagement actions.
 */
class MovieReviewActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MovieReviewViewModel
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
            .setContentView<ActivityMovieReviewBinding>(this,R.layout.activity_movie_review)

        // Inject activity
        AndroidInjection.inject(this)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Setup up navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup trailers adapter
        val adapter = TrailersAdapter()
        binding.trailers.adapter = adapter

        // Observe view model review state
        viewModel.movieReview.observe(this,{ viewData ->
            // Update review data if available
            adapter.submitList(viewData.data?.trailersUrls)
            binding.setReview(viewData.data)

            // Hide any prev error/loading notification
            snackbar?.dismiss()
            progress_bar?.visibility = View.GONE

            when(viewData) {
                is ViewData.Updating -> progress_bar?.visibility = View.VISIBLE
                is ViewData.Error -> handleReviewStateError(viewData.error)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun handleReviewStateError(error: ViewDataError) {
        snackbar = Snackbar.make(
            review_content,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)

        if (error is ViewDataError.Retriable) {
            snackbar?.setAction(getString(R.string.action_retry)) { error.retry() }
        }

        snackbar?.show()
    }
}