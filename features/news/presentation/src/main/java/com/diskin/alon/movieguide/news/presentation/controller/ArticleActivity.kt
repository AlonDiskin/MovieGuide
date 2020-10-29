package com.diskin.alon.movieguide.news.presentation.controller

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.presentation.LoadState
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.databinding.ActivityArticleBinding
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_article.*
import javax.inject.Inject

class ArticleActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: ArticleViewModel
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityArticleBinding>(
            this,
            R.layout.activity_article
        )

        //StatusBarUtil.setTransparent(this)

        // Inject activity
        AndroidInjection.inject(this)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Setup up navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Observe view model article state
        viewModel.article.observe(this, Observer { binding.article = it })

        // Observe view model article loading state
        viewModel.loading.observe(this, Observer { state ->
            when(state) {
                is LoadState.Loading -> {
                    progress_bar.visibility = View.VISIBLE
                    snackbar?.dismiss()
                }

                is LoadState.Success -> progress_bar.visibility = View.GONE

                is LoadState.Error -> {
                    progress_bar.visibility = View.GONE
                    showLoadingError(state.error)
                }
            }
        })
    }

    private fun showLoadingError(error: AppError) {
        snackbar = Snackbar.make(
            nestedScrollView,
            error.cause,
            Snackbar.LENGTH_INDEFINITE)

        if (error.retriable) {
            snackbar?.setAction(getString(R.string.action_retry)) { viewModel.reload() }
        }

        snackbar?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_article, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_share -> {
                shareArticle()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun shareArticle() {
        viewModel.article.value?.let { article ->
            ShareCompat.IntentBuilder
                .from(this)
                .setType(getString(R.string.mime_type_text))
                .setText(article.articleUrl)
                .setChooserTitle(getString(R.string.title_share_article_chooser))
                .startChooser()
        } ?: run {
            Toast.makeText(
                this,
                getString(R.string.title_action_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}