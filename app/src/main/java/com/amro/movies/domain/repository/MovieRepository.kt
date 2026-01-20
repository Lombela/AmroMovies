package com.amro.movies.domain.repository

import com.amro.movies.core.util.Result
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.model.MovieDetails

interface MovieRepository {
    suspend fun getTrendingMovies(): Result<List<Movie>>
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails>
}
