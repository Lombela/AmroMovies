package com.amro.movies.domain.repository

import com.amro.movies.core.util.Result
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.model.MovieDetails
import com.amro.movies.domain.model.MovieListType
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeMovieList(
        userId: String,
        listType: MovieListType,
        limit: Int = 100
    ): Flow<List<Movie>>

    suspend fun refreshMovieList(userId: String, listType: MovieListType): Result<Unit>

    suspend fun loadMoreMovieList(userId: String, listType: MovieListType): Result<Unit>

    fun observeMovieDetails(movieId: Int): Flow<MovieDetails?>

    suspend fun refreshMovieDetails(movieId: Int): Result<Unit>

    suspend fun getGenres(): Result<List<Genre>>

    fun observeIsFavoriteMovie(userId: String, movieId: Int): Flow<Boolean>
    suspend fun addFavoriteMovie(userId: String, movieDetails: MovieDetails): Result<Unit>
    suspend fun removeFavoriteMovie(userId: String, movieId: Int): Result<Unit>
}
