package com.diskin.alon.movieguide.home.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * Application home screen.
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: HomeNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inject activity
        AndroidInjection.inject(this)

        // Setup toolbar
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            setupBottomNavigation()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)!!
        val navGraphIds = listOf(
            navigator.getNewsNavGraph(),
            navigator.getReviewsNavGraph(),
            navigator.getBookmarksNavGraph(),
            navigator.getSettingsNavGraph()
        )

        // Setup the bottom navigation view with a list of navigation graphs
        bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
    }
}