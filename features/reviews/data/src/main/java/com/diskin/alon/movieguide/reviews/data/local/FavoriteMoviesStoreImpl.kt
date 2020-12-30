package com.diskin.alon.movieguide.reviews.data.local

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
    private val mapper: Mapper<MovieEntity,FavoriteMovie>
) : FavoriteMoviesStore {

    override fun add(movie: MovieEntity): Single<Result<Unit>> {
        return dao.insert(mapper.map(movie))
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
}