package com.diskin.alon.movieguide.news.presentation.controller

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.diskin.alon.movieguide.news.presentation.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_article.*
import javax.inject.Inject

class ArticleActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        // Inject activity
        AndroidInjection.inject(this)

        // Setup toolbar
        setSupportActionBar(toolbar)
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