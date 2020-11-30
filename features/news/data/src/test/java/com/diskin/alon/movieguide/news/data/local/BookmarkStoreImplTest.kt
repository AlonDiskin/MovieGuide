package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.data.createBookmarks
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * [BookmarkStoreImpl] unit test class.
 */
class BookmarkStoreImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var store: BookmarkStoreImpl

    // Collaborators
    private val dao: BookmarkDao = mockk()
    private val errorHandler: StorageErrorHandler = mockk()

    @Before
    fun setUp() {
        // Init subject
        store = BookmarkStoreImpl(dao,errorHandler)
    }

    @Test
    fun getAllBookmarksFromStorageWhenQueried() {
        // Test case fixture
        val daoBookmarks = createBookmarks().toList()
        every { dao.getAllDesc() } returns Observable.just(daoBookmarks)

        // Given

        // When
        val sorting = BookmarkSorting.NEWEST
        val testObserver = store.getAll(sorting).test()

        // Then
        verify { dao.getAllDesc() }

        // And
        testObserver.assertValue(Result.Success(daoBookmarks))
    }

    @Test
    fun handleErrorWhenQueryForAllFromStorageFail() {
        // Test case fixture
        val storageError = Throwable()
        val handledError: AppError = mockk()

        every { errorHandler.handle(any()) } returns handledError
        every { dao.getAllDesc() } returns Observable.error(storageError)
        every { dao.getAll() } returns Observable.error(storageError)

        // Given

        // When
        val testObserver1 = store.getAll(BookmarkSorting.NEWEST).test()
        val testObserver2 = store.getAll(BookmarkSorting.OLDEST).test()

        // Then
        testObserver1.assertValue(Result.Error(handledError))
        testObserver2.assertValue(Result.Error(handledError))
    }

    @Test
    fun removeBookmarksFromStorageWhenDeleted() {
        // Test case fixture
        every { dao.deleteAllByArticleId(any()) } returns Unit

        // Given

        // When
        val ids = listOf("id1","id2")
        val testObserver = store.remove(ids).test()

        // Then
        verify { dao.deleteAllByArticleId(ids) }

        // And
        testObserver.assertValue(Result.Success(Unit))
    }

    @Test
    fun handleErrorWhenRemovingBookmarksInStorageFail() {
        // TODO
    }

    @Test
    fun createBookmarkInStorageWhenBookmarkAdded() {
        // Test case fixture
        every { dao.insert(any()) } returns Completable.complete()

        // Given an initialized store

        // When store is asked to add bookmark
        val id = "id"
        val testObserver = store.add(id).test()

        // Then store should insert bookmark with given id to dao
        verify { dao.insert(Bookmark(id)) }

        // And return the expected result
        testObserver.assertValue(Result.Success(Unit))
    }

    @Test
    fun handleErrorWhenCreatingBookmarksInStorageFail() {
        // Test case fixture
        val storageError = Throwable()
        val handledError: AppError = mockk()

        every { errorHandler.handle(any()) } returns handledError
        every { dao.insert(any()) } returns Completable.error(storageError)

        // Given

        // When
        val testObserver = store.add("id").test()

        // Then
        testObserver.assertValue(Result.Error(handledError))
    }

    @Test
    fun checkIfStorageContainBookmarkWhenQueried() {
        // Test case fixture
        val bookmarks = listOf(Bookmark("id"))
        every { dao.getAll() } returns Observable.just(bookmarks)

        // Given

        // When
        val id = "id"
        val testObserver = store.contains(id).test()

        // Then
        verify { dao.getAll() }

        // And
        testObserver.assertValue(Result.Success(true))
    }

    @Test
    fun handleErrorWhenCheckingBookmarkInStorageFail() {
        // Test case fixture
        val storageError = Throwable()
        val handledError: AppError = mockk()

        every { errorHandler.handle(any()) } returns handledError
        every { dao.getAll() } returns Observable.error(storageError)

        // Given

        // When
        val testObserver = store.contains("id").test()

        // Then
        testObserver.assertValue(Result.Error(handledError))
    }
}