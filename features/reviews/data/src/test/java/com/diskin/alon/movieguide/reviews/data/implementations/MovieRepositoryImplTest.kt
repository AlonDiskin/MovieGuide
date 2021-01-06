package com.diskin.alon.movieguide.reviews.data.implementations

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toSingleResult
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMoviesStore
import com.diskin.alon.movieguide.reviews.data.remote.MovieStore
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [MovieRepositoryImpl] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MovieRepositoryImplTest {

    // Test subject
    private lateinit var repository: MovieRepositoryImpl

    // Collaborators
    private val movieStore: MovieStore = mockk()
    private val favoriteStore: FavoriteMoviesStore = mockk()

    @Before
    fun setUp() {
        repository = MovieRepositoryImpl(movieStore,favoriteStore)
    }

    @Test
    @Parameters(method = "sortingParams")
    fun getMoviesPagingFromRemoteSourceWhenQueriedNotForFavorite(sorting: MovieSorting) {
        // Test case fixture
        val storeResult: Observable<PagingData<MovieEntity>> = mockk()
        every { movieStore.getAllBySorting(any(),any()) } returns storeResult

        // Given an initialized repository

        // When repository is queried for movies paging other then favorites
        assertThat(sorting).isNotEqualTo(MovieSorting.FAVORITE)
        val config = mockk<PagingConfig>()
        val repoResult = repository.getAllBySorting(config,sorting)

        // Then repository should ask movie store for movies paging
        verify { movieStore.getAllBySorting(config, sorting) }

        // And return store result
        assertThat(repoResult).isEqualTo(storeResult)
    }

    @Test
    fun getMoviesPagingFromLocalSourceWhenQueriedForFavorite() {
        // Test case fixture
        val storeResult: Observable<PagingData<MovieEntity>> = mockk()
        every { favoriteStore.getAll(any()) } returns storeResult

        // Given an initialized repository

        // When repository is queried for favorite movies paging
        val sorting = MovieSorting.FAVORITE
        val config = mockk<PagingConfig>()
        val repoResult = repository.getAllBySorting(config, sorting)

        // Then repository should retrieve paging from favorite movies store
        verify { favoriteStore.getAll(config) }

        // And return favorite store paging
        assertThat(repoResult).isEqualTo(storeResult)
    }

    @Test
    fun addMovieToLocalFavoritesSourceFromRemoteWhenFavoriteAdded() {
        // Test case fixture
        val remoteMovie: MovieEntity = mockk()
        val favoriteStoreResult: Result<Unit> = mockk()

        every { movieStore.get(any()) } returns Single.just(remoteMovie).toSingleResult()
        every { favoriteStore.add(any()) } returns Single.just(favoriteStoreResult)

        // Given an initialized repository

        // When repository is asked to add movie to favorites
        val id = "id"
        val testObserver = repository.addToFavorites(id).test()

        // Then repository should get movie from remote source
        verify { movieStore.get(id) }

        // And repository should add remote movie to local favorite source
        verify { favoriteStore.add(remoteMovie) }

        // And propagate local source result
        testObserver.assertValue(favoriteStoreResult)
    }

    @Test
    fun removeMovieFromLocalFavoritesSourceWhenFavoriteRemoved() {
        // Test case fixture
        val storeResult: Single<Result<Unit>> = mockk()
        every { favoriteStore.remove(any()) } returns storeResult

        // Given an initialized repository

        // When repository is asked to remove movie from favorites
        val id = "id"
        val actualResult = repository.removeFromFavorites(id)

        // Then repository should ask local favorite source to remove movie
        verify { favoriteStore.remove(id) }

        // And return source result
        assertThat(actualResult).isEqualTo(storeResult)
    }

    private fun sortingParams() =
        arrayOf(MovieSorting.POPULARITY,MovieSorting.RATING,MovieSorting.RATING)
}