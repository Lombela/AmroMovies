package com.amro.movies.data.remote.resource

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MovieResource(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @SerialName("popularity")
    val popularity: Double = 0.0,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("overview")
    val overview: String? = null
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TrendingMoviesResource(
    @SerialName("page")
    val page: Int,
    @SerialName("results")
    val results: List<MovieResource>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PopularMoviesResource(
    @SerialName("page")
    val page: Int,
    @SerialName("results")
    val results: List<MovieResource>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)
