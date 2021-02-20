package com.diskin.alon.movieguide.news.infrastructure

import android.content.SharedPreferences
import android.content.res.Resources
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class LocalRecentDateProviderImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources
) : LocalRecentDateProvider {

    override fun getDate(): Single<Date> {
        return Single.fromCallable {
            val recentStamp = sharedPreferences.getLong(
                resources.getString(R.string.key_recent_article_read),
                0
            )

            return@fromCallable Date(recentStamp)
        }
    }
}