package com.diskin.alon.movieguide.home.presentation

import androidx.annotation.NavigationRes

/**
 * Navigation contract for app home screen.
 */
interface HomeNavigator {

    @NavigationRes
    fun getNewsNavGraph(): Int

    @NavigationRes
    fun getReviewsNavGraph(): Int

    @NavigationRes
    fun getSettingsNavGraph(): Int

    @NavigationRes
    fun getBookmarksNavGraph(): Int
}