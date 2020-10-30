package com.diskin.alon.movieguide.reviews.featuretesting

import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.reviews.data.MOVIE_DB_BASE_POSTER_PATH
import org.json.JSONObject

fun getExpectedUiMoviesFromTestWebResource(path: String): List<UiMovieData> {
    val json = getJsonFromResource(path)
    val jsonObject = JSONObject(json)
    val jsonArray = jsonObject.getJSONArray("results")
    val res = mutableListOf<UiMovieData>()

    for (i in 0 until jsonArray.length()) {
        val item = jsonArray.getJSONObject(i)

        res.add(
            UiMovieData(
                item.getString("title"),
                item.getDouble("vote_average").toString(),
                MOVIE_DB_BASE_POSTER_PATH.plus(item.get("poster_path"))
            )
        )
    }

    return res
}