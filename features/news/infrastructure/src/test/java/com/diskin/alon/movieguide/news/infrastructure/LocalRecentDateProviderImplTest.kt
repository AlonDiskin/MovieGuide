package com.diskin.alon.movieguide.news.infrastructure

import android.content.SharedPreferences
import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * [LocalRecentDateProviderImpl] unit test class.
 */
class LocalRecentDateProviderImplTest {

    // Test subject
    private lateinit var provider: LocalRecentDateProviderImpl

    // Collaborators
    private val sharedPreferences: SharedPreferences = mockk()
    private val resources: Resources = mockk()

    @Before
    fun setUp() {
        provider = LocalRecentDateProviderImpl(sharedPreferences, resources)
    }

    @Test
    fun getLatestReadByUserArticleDateWhenQueriedForDate() {
        // Test case fixture
        val latestRead = Calendar.getInstance().timeInMillis
        val prefKey = "key"

        every { sharedPreferences.getLong(any(),any()) } returns latestRead
        every { resources.getString(any()) } returns prefKey

        // Given an initialized provider

        // When provider queried for latest article date read by user
        val testObserver = provider.getDate().test()

        // Then provider should get date from sharedPreferences
        verify { sharedPreferences.getLong(prefKey,any()) }

        // And propagate expected result from sharedPreferences date
        testObserver.assertValue(Date(latestRead))
    }
}