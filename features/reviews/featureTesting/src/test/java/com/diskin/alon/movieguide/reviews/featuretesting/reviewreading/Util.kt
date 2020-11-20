package com.diskin.alon.movieguide.reviews.featuretesting.reviewreading

import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.json.JSONObject

fun getExpectedUiMovieReviewFromTestResources(detailPath: String, trailersPath: String): UiMovieReviewData {
    val movieDetailJson = getJsonFromResource(detailPath)
    val movieTrailersJson = getJsonFromResource(trailersPath)
    val jsonDetailObject = JSONObject(movieDetailJson)
    val jsonTrailersObject = JSONObject(movieTrailersJson)
    val jsonGenresArray = jsonDetailObject.getJSONArray("genres")
    val jsonTrailersArray = jsonTrailersObject.getJSONArray("results")
    val releaseDate = LocalDate.parse(jsonDetailObject.getString("release_date")).toDate().time
    var genres = ""
    val trailersUrls = mutableListOf<String>()

    for (i in 0 until jsonGenresArray.length()) {
        val genreJsonObject = jsonGenresArray.getJSONObject(i)
        val genre = genreJsonObject.getString("name")
        genres = if (i == jsonGenresArray.length() - 1) {
            genres.plus(genre)
        } else {
            genres.plus(genre).plus(",")
        }
    }

    for (i in 0 until jsonTrailersArray.length()) {
        val key = jsonTrailersArray.getJSONObject(i).getString("key")
        val url = "https://img.youtube.com/vi/".plus(key).plus("/0.jpg")

        trailersUrls.add(url)
    }

    return UiMovieReviewData(
        jsonDetailObject.getString("title"),
        jsonDetailObject.getDouble("vote_average").toString(),
        genres,
        LocalDateTime(releaseDate).toString("dd MMM yyyy"),
        jsonDetailObject.getString("overview"),
        "review_stub",
        "http://image.tmdb.org/t/p/w342".plus(jsonDetailObject.getString("backdrop_path")),
        trailersUrls
    )
}