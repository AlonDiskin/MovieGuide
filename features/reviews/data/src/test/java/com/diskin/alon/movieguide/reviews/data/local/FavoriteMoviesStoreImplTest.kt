package com.diskin.alon.movieguide.reviews.data.local

import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * [FavoriteMoviesStoreImpl] unit tests class.
 */
class FavoriteMoviesStoreImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var store: FavoriteMoviesStoreImpl

    // Collaborators
    private val dao: FavoriteMovieDao = mockk()
    private val errorHandler: StorageErrorHandler = mockk()
    private val mapper: Mapper<MovieEntity, FavoriteMovie> = mockk()

    @Before
    fun setUp() {
        store = FavoriteMoviesStoreImpl(dao, errorHandler, mapper)
    }

    @Test
    fun addMovieToLocalStorageWhenAdded() {
        // Test case  fixture
        val favoriteMovie: FavoriteMovie = mockk()

        every { mapper.map(any()) } returns favoriteMovie
        every { dao.insert(any()) } returns Completable.complete()

        // Given an initialized store

        // When store is asked to add a movie
        val movie: MovieEntity = mockk()
        val testObserver = store.add(movie).test()

        // Then store should add movie to favorites dao
        verify { mapper.map(movie) }
        verify { dao.insert(favoriteMovie) }

        // And propagate successful result
        testObserver.assertValue(Result.Success(Unit))
    }

    @Test
    fun handleErrorWhenAddingFavoriteToStorageFail() {
        // Test case fixture
        val favoriteMovie: FavoriteMovie = mockk()
        val error: Throwable = mockk()
        val appError: AppError = mockk()

        every { mapper.map(any()) } returns favoriteMovie
        every { dao.insert(any()) } returns Completable.error(error)
        every { errorHandler.handle(any()) } returns appError

        // Given an initialized store

        // When store is asked to add a movie
        val movie: MovieEntity = mockk()
        val testObserver = store.add(movie).test()

        // Then store should add movie to favorites dao
        verify { mapper.map(movie) }
        verify { dao.insert(favoriteMovie) }

        // When adding fail

        // Then store should delegate error to error handler
        verify { errorHandler.handle(error) }

        // And propagate error result
        testObserver.assertValue(Result.Error(appError))
    }

    @Test
    fun removeMovieFromLocalStorageWhenRemoved() {
        // Test case  fixture
        every { dao.delete(any()) } returns Completable.complete()

        // Given an initialized store

        // When store is asked to remove a movie
        val id = "id"
        val testObserver = store.remove(id).test()

        // Then store should ask dao to delete the movie
        verify { dao.delete(id) }

        // And propagate successful result
        testObserver.assertValue(Result.Success(Unit))
    }

    @Test
    fun handleErrorWhenRemovingFavoriteFromStorageFail() {
        // Test case fixture
        val error: Throwable = mockk()
        val appError: AppError = mockk()

        every { dao.delete(any()) } returns Completable.error(error)
        every { errorHandler.handle(any()) } returns appError

        // Given an initialized store

        // When store is asked to remove a movie
        val id = "id"
        val testObserver = store.remove(id).test()

        // Then store should ask dao to delete the movie
        verify { dao.delete(id) }

        // When removal fail

        // Then store should delegate error to error handler
        verify { errorHandler.handle(error) }

        // And propagate error result
        testObserver.assertValue(Result.Error(appError))
    }
}