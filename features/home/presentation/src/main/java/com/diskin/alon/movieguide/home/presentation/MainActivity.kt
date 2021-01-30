package com.diskin.alon.movieguide.home.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Application home screen.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: HomeNavigator
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Set app nav graph with bottom navigation
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
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent,
            { controller, destination, arguments ->
                findViewById<AppBarLayout>(R.id.appBar).setExpanded(true)
            },
            { item ->
                findViewById<AppBarLayout>(R.id.appBar).setExpanded(true)
            }
        )

        // Whenever the selected controller changes, setup the action bar
        controller.observe(this, { navController ->
            setupActionBarWithNavController(navController)
        })

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}