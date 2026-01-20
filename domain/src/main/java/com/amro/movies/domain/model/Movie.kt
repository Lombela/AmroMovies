package com.amro.movies.domain.model

import java.time.LocalDate

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val genres: List<Genre>,
    val popularity: Double,
    val releaseDate: LocalDate?,
    val voteAverage: Double
)
