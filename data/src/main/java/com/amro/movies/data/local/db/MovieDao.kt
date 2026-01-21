package com.amro.movies.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.amro.movies.data.local.entity.GenreEntity
import com.amro.movies.data.local.entity.MovieEntity
import com.amro.movies.data.local.entity.MovieGenreCrossRef
import com.amro.movies.data.local.entity.MovieListEntryEntity
import com.amro.movies.data.local.entity.MovieListMetaEntity
import com.amro.movies.data.local.model.MovieListEntryWithMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Transaction
    @Query(
        """
        SELECT * FROM movie_list_entries
        WHERE userId = :userId AND listType = :listType
        ORDER BY position ASC
        LIMIT :limit
        """
    )
    fun observeMovieList(
        userId: String,
        listType: String,
        limit: Int
    ): Flow<List<MovieListEntryWithMovie>>

    @Transaction
    @Query(
        """
        SELECT * FROM movie_list_entries
        WHERE userId = :userId AND listType = :listType
        ORDER BY position ASC
        LIMIT :limit
        """
    )
    suspend fun getMovieList(
        userId: String,
        listType: String,
        limit: Int
    ): List<MovieListEntryWithMovie>

    @Upsert
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Upsert
    suspend fun upsertGenres(genres: List<GenreEntity>)

    @Upsert
    suspend fun upsertMovieGenres(crossRefs: List<MovieGenreCrossRef>)

    @Upsert
    suspend fun upsertListEntries(entries: List<MovieListEntryEntity>)

    @Upsert
    suspend fun upsertListMeta(meta: MovieListMetaEntity)

    @Query("DELETE FROM movie_list_entries WHERE userId = :userId AND listType = :listType")
    suspend fun clearList(userId: String, listType: String)

    @Query(
        "SELECT nextPage FROM movie_list_meta WHERE userId = :userId AND listType = :listType"
    )
    suspend fun getNextPage(userId: String, listType: String): Int?

    @Query(
        "SELECT MAX(position) FROM movie_list_entries WHERE userId = :userId AND listType = :listType"
    )
    suspend fun getMaxPosition(userId: String, listType: String): Int?

    @Query(
        "SELECT movieId FROM movie_list_entries WHERE userId = :userId AND listType = :listType"
    )
    suspend fun getListMovieIds(userId: String, listType: String): List<Int>

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Query(
        """
        SELECT movieId FROM movies
        ORDER BY lastAccessEpochMs ASC, cacheSizeBytes DESC
        LIMIT :limit
        """
    )
    suspend fun getEvictionCandidates(limit: Int): List<Int>

    @Query("DELETE FROM movies WHERE movieId IN (:movieIds)")
    suspend fun deleteMovies(movieIds: List<Int>)

    @Query("DELETE FROM movie_genre WHERE movieId IN (:movieIds)")
    suspend fun deleteMovieGenres(movieIds: List<Int>)

    @Query("DELETE FROM movie_list_entries WHERE movieId IN (:movieIds)")
    suspend fun deleteListEntriesForMovies(movieIds: List<Int>)

    @Query("SELECT * FROM genres")
    suspend fun getGenres(): List<GenreEntity>

    @Query("SELECT COUNT(*) FROM genres")
    suspend fun getGenreCount(): Int
}
