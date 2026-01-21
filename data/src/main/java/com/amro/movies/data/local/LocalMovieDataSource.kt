package com.amro.movies.data.local

import androidx.room.withTransaction
import com.amro.movies.data.local.db.MovieDao
import com.amro.movies.data.local.db.MoviesDatabase
import com.amro.movies.data.local.entity.GenreEntity
import com.amro.movies.data.local.entity.MovieEntity
import com.amro.movies.data.local.entity.MovieDetailsEntity
import com.amro.movies.data.local.entity.MovieGenreCrossRef
import com.amro.movies.data.local.entity.MovieListEntryEntity
import com.amro.movies.data.local.entity.MovieListMetaEntity
import com.amro.movies.data.local.model.MovieDetailsWithGenres
import com.amro.movies.data.local.model.MovieListEntryWithMovie
import com.amro.movies.domain.model.MovieListType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalMovieDataSource @Inject constructor(
    private val database: MoviesDatabase,
    private val movieDao: MovieDao
) {
    fun observeMovieList(
        userId: String,
        listType: String,
        limit: Int
    ): Flow<List<MovieListEntryWithMovie>> {
        return movieDao.observeMovieList(userId, listType, limit)
    }

    fun observeMovieDetails(movieId: Int): Flow<MovieDetailsWithGenres?> {
        return movieDao.observeMovieDetails(movieId)
    }

    fun observeIsMovieInList(
        userId: String,
        listType: String,
        movieId: Int
    ): Flow<Boolean> {
        return movieDao.observeIsMovieInList(userId, listType, movieId)
    }

    suspend fun getMovieList(
        userId: String,
        listType: String,
        limit: Int
    ): List<MovieListEntryWithMovie> {
        return movieDao.getMovieList(userId, listType, limit)
    }

    suspend fun getNextPage(userId: String, listType: String): Int? {
        return movieDao.getNextPage(userId, listType)
    }

    suspend fun getMaxPosition(userId: String, listType: String): Int? {
        return movieDao.getMaxPosition(userId, listType)
    }

    suspend fun getListMovieIds(userId: String, listType: String): List<Int> {
        return movieDao.getListMovieIds(userId, listType)
    }

    suspend fun getMovie(movieId: Int): MovieEntity? {
        return movieDao.getMovie(movieId)
    }

    suspend fun getGenreCount(): Int {
        return movieDao.getGenreCount()
    }

    suspend fun getGenres(): List<GenreEntity> {
        return movieDao.getGenres()
    }

    suspend fun upsertGenres(genres: List<GenreEntity>) {
        if (genres.isEmpty()) return
        movieDao.upsertGenres(genres)
    }

    suspend fun upsertMovieDetails(
        details: MovieDetailsEntity,
        genres: List<GenreEntity>,
        crossRefs: List<MovieGenreCrossRef>
    ) {
        database.withTransaction {
            upsertInChunks(genres, 100) { movieDao.upsertGenres(it) }
            movieDao.upsertMovieDetails(details)
            upsertInChunks(crossRefs, 100) { movieDao.upsertMovieGenres(it) }
        }
    }

    suspend fun replaceMovieList(
        userId: String,
        listType: String,
        genres: List<GenreEntity>,
        movies: List<MovieEntity>,
        crossRefs: List<MovieGenreCrossRef>,
        listEntries: List<MovieListEntryEntity>,
        listMeta: MovieListMetaEntity,
        maxCacheSize: Int
    ) {
        database.withTransaction {
            upsertInChunks(genres, 100) { movieDao.upsertGenres(it) }
            upsertInChunks(movies, 100) { movieDao.upsertMovies(it) }
            upsertInChunks(crossRefs, 100) { movieDao.upsertMovieGenres(it) }
            movieDao.clearList(userId, listType)
            upsertInChunks(listEntries, 100) { movieDao.upsertListEntries(it) }
            movieDao.upsertListMeta(listMeta)
            evictExcessMovies(maxCacheSize)
        }
    }

    suspend fun addMovieToList(
        userId: String,
        listType: String,
        movie: MovieEntity,
        genres: List<GenreEntity>,
        crossRefs: List<MovieGenreCrossRef>,
        fetchedAtEpochMs: Long,
        page: Int = 1
    ) {
        database.withTransaction {
            upsertInChunks(genres, 100) { movieDao.upsertGenres(it) }
            movieDao.upsertMovies(listOf(movie))
            upsertInChunks(crossRefs, 100) { movieDao.upsertMovieGenres(it) }
            val position = (movieDao.getMaxPosition(userId, listType) ?: -1) + 1
            movieDao.upsertListEntries(
                listOf(
                    MovieListEntryEntity(
                        userId = userId,
                        listType = listType,
                        movieId = movie.movieId,
                        position = position,
                        page = page,
                        fetchedAtEpochMs = fetchedAtEpochMs
                    )
                )
            )
        }
    }

    suspend fun removeMovieFromList(
        userId: String,
        listType: String,
        movieId: Int
    ) {
        movieDao.deleteListEntry(userId, listType, movieId)
    }

    suspend fun appendMovieList(
        genres: List<GenreEntity>,
        movies: List<MovieEntity>,
        crossRefs: List<MovieGenreCrossRef>,
        listEntries: List<MovieListEntryEntity>,
        listMeta: MovieListMetaEntity,
        maxCacheSize: Int
    ) {
        database.withTransaction {
            upsertInChunks(genres, 100) { movieDao.upsertGenres(it) }
            upsertInChunks(movies, 100) { movieDao.upsertMovies(it) }
            upsertInChunks(crossRefs, 100) { movieDao.upsertMovieGenres(it) }
            upsertInChunks(listEntries, 100) { movieDao.upsertListEntries(it) }
            movieDao.upsertListMeta(listMeta)
            evictExcessMovies(maxCacheSize)
        }
    }

    private suspend fun evictExcessMovies(maxCount: Int) {
        val totalCount = movieDao.getMovieCount()
        val excessCount = totalCount - maxCount
        if (excessCount <= 0) return

        val candidates = movieDao.getEvictionCandidates(
            limit = excessCount,
            protectedListType = MovieListType.FAVORITES.name
        )
        if (candidates.isEmpty()) return

        movieDao.deleteListEntriesForMovies(candidates)
        movieDao.deleteMovieGenres(candidates)
        movieDao.deleteMovieDetails(candidates)
        movieDao.deleteMovies(candidates)
    }

    private suspend fun <T> upsertInChunks(
        items: List<T>,
        chunkSize: Int,
        upsert: suspend (List<T>) -> Unit
    ) {
        if (items.isEmpty()) return
        items.chunked(chunkSize).forEach { chunk ->
            upsert(chunk)
        }
    }
}
