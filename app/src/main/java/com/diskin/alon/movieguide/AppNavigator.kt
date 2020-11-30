package com.diskin.alon.movieguide

import com.diskin.alon.movieguide.home.presentation.HomeNavigator
import javax.inject.Inject

/**
 * Provides navigation utilities for app features.
 */
class AppNavigator @Inject constructor() : HomeNavigator {
    override fun getNewsNavGraph(): Int = R.navigation.news_nav_graph

    override fun getReviewsNavGraph(): Int = R.navigation.reviews_nav_graph

    override fun getSettingsNavGraph(): Int = R.navigation.settings_nav_graph

    override fun getBookmarksNavGraph(): Int =  R.navigation.bookmarks_nav_graph
}