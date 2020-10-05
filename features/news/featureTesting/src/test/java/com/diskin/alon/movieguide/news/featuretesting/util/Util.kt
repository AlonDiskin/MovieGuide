package com.diskin.alon.movieguide.news.featuretesting.util

import com.diskin.alon.movieguide.news.presentation.viewmodel.DATE_FORMAT
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline
import org.joda.time.LocalDateTime
import org.json.JSONObject
import java.io.File

fun getJsonBodyFromResource(resourceName: String): String {
    val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
    val jsonResource = topLevelClass.classLoader!! // javaClass.classLoader
        .getResource(resourceName)

    return File(jsonResource.toURI()).readText()
}

fun parseFeedlyResponseJsonToNewsHeadlines(json: String): List<NewsHeadline> {
    val jsonResponseObject = JSONObject(json)
    val jsonItemsArray = jsonResponseObject.getJSONArray("items")
    val newsHeadlines = mutableListOf<NewsHeadline>()

    for (i in 0 until jsonItemsArray.length()) {
        val jsonItemObject = jsonItemsArray.getJSONObject(i)
        newsHeadlines.add(
            NewsHeadline(
                jsonItemObject.getString("id"),
                jsonItemObject.getString("title"),
                LocalDateTime(jsonItemObject.getLong("published")).toString(DATE_FORMAT),
                jsonItemObject.getJSONObject("visual").getString("url"),
                jsonItemObject.getString("originId")
            )
        )
    }

    return newsHeadlines
}