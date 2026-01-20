package com.amro.movies.data.remote.resource

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MovieDetailsResource(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("genres")
    val genres: List<GenreResource> = emptyList(),
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("budget")
    val budget: Long = 0,
    @SerialName("revenue")
    val revenue: Long = 0,
    @SerialName("status")
    val status: String? = null,
    @SerialName("imdb_id")
    val imdbId: String? = null,
    @SerialName("runtime")
    val runtime: Int? = null,
    @SerialName("release_date")
    val releaseDate: String? = null
)
