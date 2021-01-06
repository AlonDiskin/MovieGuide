package com.diskin.alon.movieguide.reviews.data.local

import androidx.paging.*
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toResult
import com.diskin.alon.movieguide.common.appservices.toSingleResult
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteMoviesStoreImpl @Inject constructor(
    private val dao: FavoriteMovieDao,
    private val errorHandler: StorageErrorHandler,
    private val favoriteMovieMapper: Mapper<MovieEntity,FavoriteMovie>,
    private val movieEntityMapper: Mapper<FavoriteMovie,MovieEntity>
) : FavoriteMoviesStore {

    override fun add(movie: MovieEntity): Single<Result<Unit>> {
        return dao.insert(favoriteMovieMapper.map(movie))
            .subscribeOn(Schedulers.io())
            .toSingleDefault(Unit)
            .toSingleResult(errorHandler::handle)
    }

    override fun remove(id: String): Single<Result<Unit>> {
        return dao.delete(id)
            .subscribeOn(Schedulers.io())
            .toSingleDefault(Unit)
            .toSingleResult(errorHandler::handle)
    }

    override fun contains(id: String): Observable<Result<Boolean>> {
        return dao.contains(id)
            .subscribeOn(Schedulers.io())
            .toResult(errorHandler::handle)
    }

    override fun getAll(config: PagingConfig): Observable<PagingData<MovieEntity>> {
        return Pager(config) { dao.getAll() }
            .observable
            .map { it.map(movieEntityMapper::map) }
    }
}