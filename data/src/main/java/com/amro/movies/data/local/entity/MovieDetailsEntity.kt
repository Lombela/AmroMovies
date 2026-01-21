package com.amro.movies.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_details")
data class MovieDetailsEntity(
    @PrimaryKey val movieId: Int,
    val title: String,
    val tagline: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val budget: Long,
    val revenue: Long,
    val status: String?,
    val imdbId: String?,
    val runtime: Int?,
    val releaseDate: String?,
    val lastUpdatedEpochMs: Long
)
