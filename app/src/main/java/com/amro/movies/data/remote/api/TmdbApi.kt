package com.amro.movies.data.remote.api

import com.amro.movies.data.remote.resource.GenreListResource
import com.amro.movies.data.remote.resource.MovieDetailsResource
import com.amro.movies.data.remote.resource.TrendingMoviesResource
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): TrendingMoviesResource

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("language") language: String = "en-US"
    ): GenreListResource

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): MovieDetailsResource
}
