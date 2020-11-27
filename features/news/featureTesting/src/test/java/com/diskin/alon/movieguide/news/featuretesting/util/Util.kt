package com.diskin.alon.movieguide.news.featuretesting.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.presentation.data.Headline
import org.joda.time.LocalDateTime
import org.json.JSONObject
import java.io.File

fun parseFeedlyResponseJsonToNewsHeadlines(json: String): List<Headline> {
    val jsonResponseObject = JSONObject(json)
    val jsonItemsArray = jsonResponseObject.getJSONArray("items")
    val newsHeadlines = mutableListOf<Headline>()
    val headlinesDateFormat = ApplicationProvider
        .getApplicationContext<Context>()
        .getString(R.string.headline_date_format)

    for (i in 0 until jsonItemsArray.length()) {
        val jsonItemObject = jsonItemsArray.getJSONObject(i)
        newsHeadlines.add(
            Headline(
                jsonItemObject.getString("id"),
                jsonItemObject.getString("title"),
                LocalDateTime(jsonItemObject.getLong("published")).toString(headlinesDateFormat),
                jsonItemObject.getJSONObject("visual").getString("url"),
                jsonItemObject.getString("originId")
            )
        )
    }

    return newsHeadlines
}