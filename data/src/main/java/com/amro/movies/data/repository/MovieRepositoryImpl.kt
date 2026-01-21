package com.amro.movies.data.repository

import com.amro.movies.core.util.Result
import com.amro.movies.data.mapper.toDomain
import com.amro.movies.data.mapper.toDomainList
import com.amro.movies.data.remote.api.TmdbApi
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.model.MovieDetails
import com.amro.movies.domain.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val tmdbApi: TmdbApi
) : MovieRepository {

    private var cachedGenres: Map<Int, Genre>? = null

    override suspend fun getTrendingMovies(): Result<List<Movie>> {
        return try {
            coroutineScope {
                val genreMap = getOrFetchGenreMap()

                val page1Deferred = async { tmdbApi.getTrendingMovies(page = 1) }
                val page2Deferred = async { tmdbApi.getTrendingMovies(page = 2) }
                val page3Deferred = async { tmdbApi.getTrendingMovies(page = 3) }
                val page4Deferred = async { tmdbApi.getTrendingMovies(page = 4) }
                val page5Deferred = async { tmdbApi.getTrendingMovies(page = 5) }

                val allMovies = listOf(
                    page1Deferred.await().results,
                    page2Deferred.await().results,
                    page3Deferred.await().results,
                    page4Deferred.await().results,
                    page5Deferred.await().results
                ).flatten()
                    .distinctBy { it.id }
                    .take(100)
                    .toDomainList(genreMap)

                Result.Success(allMovies)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getPopularMovies(): Result<List<Movie>> {
        return try {
            coroutineScope {
                val genreMap = getOrFetchGenreMap()

                val page1Deferred = async { tmdbApi.getPopularMovies(page = 1) }
                val page2Deferred = async { tmdbApi.getPopularMovies(page = 2) }
                val page3Deferred = async { tmdbApi.getPopularMovies(page = 3) }
                val page4Deferred = async { tmdbApi.getPopularMovies(page = 4) }
                val page5Deferred = async { tmdbApi.getPopularMovies(page = 5) }

                val allMovies = listOf(
                    page1Deferred.await().results,
                    page2Deferred.await().results,
                    page3Deferred.await().results,
                    page4Deferred.await().results,
                    page5Deferred.await().results
                ).flatten()
                    .distinctBy { it.id }
                    .take(100)
                    .toDomainList(genreMap)

                Result.Success(allMovies)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = tmdbApi.getGenres()
            val genres = response.genres.toDomainList()
            cachedGenres = genres.associateBy { it.id }
            Result.Success(genres)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Result<MovieDetails> {
        return try {
            val response = tmdbApi.getMovieDetails(movieId)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun getOrFetchGenreMap(): Map<Int, Genre> {
        return cachedGenres ?: run {
            val response = tmdbApi.getGenres()
            val genres = response.genres.toDomainList()
            val map = genres.associateBy { it.id }
            cachedGenres = map
            map
        }
    }
}
