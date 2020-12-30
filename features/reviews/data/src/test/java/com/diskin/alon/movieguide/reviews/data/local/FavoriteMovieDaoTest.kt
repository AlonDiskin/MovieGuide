package com.diskin.alon.movieguide.reviews.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diskin.alon.movieguide.reviews.data.createFavoriteMovie
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * [FavoriteMovieDao] integration test class.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class FavoriteMovieDaoTest {

    // System under test
    private lateinit var dao: FavoriteMovieDao
    private lateinit var db: TestDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.favoritesDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertToDb() {
        // Given an initialized empty db

        // When movie is inserted by dao
        dao.insert(createFavoriteMovie()).blockingAwait()

        // Then db should add movie to favorites table
        val queryResult = db.compileStatement("SELECT COUNT(*) FROM favorites").simpleQueryForLong()
        assertThat(queryResult).isEqualTo(1)
    }

    @Test
    fun insertAndDelete() {
        val movie = createFavoriteMovie()
        dao.insert(movie).blockingAwait()

        // Given a db with single record of favorite movie

        // When
        dao.delete(movie.id).blockingAwait()

        // Then
        val queryResult = db.compileStatement("SELECT COUNT(*) FROM favorites").simpleQueryForLong()
        assertThat(queryResult).isEqualTo(0)
    }

    @Test
    fun insertAndCheckIfExist() {
        val movie = createFavoriteMovie()
        dao.insert(movie).blockingAwait()

        // Given a db with single record of favorite movie

        // When
        val actual = dao.contains(movie.id).blockingFirst()

        // Then
        assertThat(actual).isTrue()
    }
}