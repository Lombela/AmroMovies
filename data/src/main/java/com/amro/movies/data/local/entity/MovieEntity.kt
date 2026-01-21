package com.amro.movies.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val movieId: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val popularity: Double,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val lastUpdatedEpochMs: Long,
    val lastAccessEpochMs: Long,
    val cacheSizeBytes: Long
)
