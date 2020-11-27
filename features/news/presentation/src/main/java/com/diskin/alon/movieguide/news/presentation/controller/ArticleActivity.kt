package com.diskin.alon.movieguide.news.presentation.controller

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ViewDataError
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

        // Inject activity
        AndroidInjection.inject(this)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Setup up navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Observe view model article state
        viewModel.article.observe(this, {
            // Update article if data update available
            binding.article = it.data

            // Hide any prev error/loading notification
            snackbar?.dismiss()
            progress_bar?.visibility = View.GONE

            when(it) {
                is ViewData.Updating -> progress_bar?.visibility = View.VISIBLE
                is ViewData.Error -> handleArticleStateError(it.error)
            }
        })
    }

    private fun handleArticleStateError(error: ViewDataError) {
        snackbar = Snackbar.make(
            nestedScrollView,
            error.reason,
            Snackbar.LENGTH_INDEFINITE)

        if (error is ViewDataError.Retriable) {
            snackbar?.setAction(getString(R.string.action_retry)) { error.retry() }
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
        viewModel.article.value?.data?.let { article ->
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