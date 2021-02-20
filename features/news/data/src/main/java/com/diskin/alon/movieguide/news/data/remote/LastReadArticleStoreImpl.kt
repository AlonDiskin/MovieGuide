package com.diskin.alon.movieguide.news.data.remote

import android.content.SharedPreferences
import android.content.res.Resources
import com.diskin.alon.movieguide.news.data.R
import javax.inject.Inject

class LastReadArticleStoreImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources
) : LastReadArticleStore {

    override fun putLastDate(long: Long) {
        val latestArticleDateKey = resources.getString(R.string.key_recent_article_read)
        sharedPreferences.edit()
            .putLong(latestArticleDateKey,long)
            .apply()
    }
}