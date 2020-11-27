package com.diskin.alon.movieguide.news.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diskin.alon.movieguide.news.data.local.BookmarkDao
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * [BookmarkDao] integration test class.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class BookmarkDaoTest {

    // System under test
    private lateinit var dao: BookmarkDao
    private lateinit var db: TestDatabase

    @Before
    fun setUp() {
        val context = getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.headlineDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun fetchAllHeadlinesInAscOrderFromDb() {
        // Given
        val headlines = createLocalHeadlines()
        db.testHeadlineDao().insert(*headlines)

        // When
        val actual = dao.getHeadlinesAsc().blockingFirst()

        // Then
        val expected = headlines.toList().mapIndexed { index, localHeadline ->
            Bookmark(
                localHeadline.articleId,
                index + 1
            )
        }
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun fetchAllHeadlinesInDescOrderFromDb() {
        // Given
        val headlines = createLocalHeadlines()
        db.testHeadlineDao().insert(*headlines)

        // When
        val actual = dao.getHeadlinesDesc().blockingFirst()

        // Then
        val expected = headlines.toList().reversed().mapIndexed { index, localHeadline ->
            Bookmark(
                localHeadline.articleId,
                headlines.size - index
            )
        }
        assertThat(actual).isEqualTo(expected)
    }
}