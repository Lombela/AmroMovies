package com.amro.movies.domain.model

import java.time.LocalDate

data class MovieDetails(
    val id: Int,
    val title: String,
    val tagline: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val genres: List<Genre>,
    val overview: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val budget: Long,
    val revenue: Long,
    val status: String?,
    val imdbId: String?,
    val runtime: Int?,
    val releaseDate: LocalDate?
)
